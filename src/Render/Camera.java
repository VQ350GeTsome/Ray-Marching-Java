package Render;

import Utility.vec3;

public class Camera {
    
    private static final float DEG_TO_RAD = (float)(Math.PI / 180.0),
                               RAD_TO_DEG = (float)(180.0 / Math.PI);

    private vec3    pos,
                    forward,
                    up,
                    right;
    private final vec3 worldUp = new vec3(0.0f, 0.0f, 1.0f);
    
    private float   yaw,
                    pitch,
                    fov,
                    tanOfHalfFov;   //Storing this saves some computing power ...
    
    /**
     * Camera constructor. It takes in some general information about
     * the camera and calculates the forward vector, right vector, 
     * & yaw, & pitch
     * @param pos The position of the camera in 3D space
     * @param target The point where it is looking at
     * @param up The up vector of the camera
     * @param fov The field of view
     */
    public Camera(vec3 pos, vec3 target, vec3 up, int fov) {
        this.pos = pos;
        forward =   target
                    .subtract( pos )
                    .normalize();
        
        right =     forward
                    .cross( worldUp )
                    .normalize();
        this.up =   right
                    .cross( forward )
                    .normalize();
        
        yaw = (float)(Math.atan2(forward.x, forward.z) * RAD_TO_DEG);
        float fy = forward.y;
        if (fy > 1.0f)  { fy =  1.0f; }
        if (fy < -1.0f) { fy = -1.0f; }
        pitch = (float)(Math.asin(fy) * RAD_TO_DEG);
        
        this.fov = (float)(fov * DEG_TO_RAD);       //Store FOV in radians
        tanOfHalfFov = (float)Math.tan(this.fov / 2.0);
    }
    /**
     * Updates FOV. Takes in FOV in radians, calculates
     * the tangent of half of FOV, and stores both.
     * @param fov New field of view in radians.
     */
    private void updateFov(float fov){
        this.fov = fov;
        tanOfHalfFov = (float)Math.tan(this.fov / 2.0);
    }
    /**
     * Uses the arguments to calculate the direction of the ray to cast.
     * @param x x coord on the canvas.
     * @param y y coord on the canvas.
     * @param aspectRatio width of canvas divided by the height.
     * @return A normalized vector that is the direction we will march.
     */
    public vec3 getRayDirection(float x, float y, float aspectRatio) {
        float px = (float)((2.0f * x - 1.0f) * tanOfHalfFov * aspectRatio);
        float py = (float)((1.0f - 2.0f * y) * tanOfHalfFov);
        return  right.scale( px )
                .add ( up.scale( py ) )
                .add ( forward )
                .normalize();
    }
    
    public void move(vec3 m) {
        pos =   pos
                .add(m);    //Move camera
    }
    public void rotate(float yawDelta, float pitchDelta) {
        forward = rotateAroundAxis(forward, right, pitchDelta);
        forward = rotateAroundAxis(forward, worldUp, yawDelta);
        forward = forward.normalize();
        right = forward.cross(worldUp).normalize();
        up = right.cross(forward).normalize();
    }
    
    private vec3 rotateAroundAxis(vec3 v, vec3 axis, float angleDeg) {
        float angleRad = angleDeg * DEG_TO_RAD;
        float cos = (float)Math.cos(angleRad);
        float sin = (float)Math.sin(angleRad);
        return v.scale(cos)
                .add(axis.cross(v).scale(sin))
                .add(axis.scale(axis.dot(v) * (1 - cos)));
    }
    public void zoom(float zoom) { 
        float newFov = fov * (1 + zoom);
        newFov = (newFov > 3.13f) ? 3.13f : newFov;     //Cap it so it cannot go above pi, which is 180 degrees ... remember
        updateFov(newFov); 
    }
    
    /**
     * Packages the forward, up, right vectors into an array
     * @return The array of size 3
     */
    public vec3[] getOrientation() {
        vec3[] arr = new vec3[4];
        arr[0] = forward;
        arr[1] = right;
        arr[2] = up;
        arr[3] = pos;
        return arr;
    }
    
    public String packageCamera() {
        return  pos.toString()      + "," +
                forward.toString()  + "," + 
                up.toString()       + "," + 
                right.toString()    + "," + 
                yaw + "," + pitch   + "," +
                fov                 + ",\n"; 
                         
    }
    public void unpackageCamera(String[] parts) {
        pos     = new vec3(parts[0]);
        forward = new vec3(parts[1]);
        up      = new vec3(parts[2]);
        right   = new vec3(parts[3]);
        yaw     = Float.parseFloat(parts[4]);
        pitch   = Float.parseFloat(parts[5]);
        updateFov((float) Double.parseDouble(parts[6]));
    }
}
