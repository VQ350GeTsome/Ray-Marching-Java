package Render;

/**
 * Camera object class. Stores it's forward, up, & right vectors,
 * its position, the yaw & pitch, & finally the field of view.
 * 
 * This Camera is mainly used to calculate a ray direction given 
 * its fields, and ray position given its position.
 * 
 * @author Harrison Davis
 */
public final class Camera extends SDFs.SDF {
    
    private static final float DEG_TO_RAD = (float) (Math.PI / 180.0),
                               RAD_TO_DEG = (float) (180.0 / Math.PI);

    // Position vector & camera axes ( the basic vectors ) .
    private Vectors.vec3 pos,
                         forward,
                         up,
                         right;
       
    // Camera ( or viewing ) parameters.
    private float yaw,
                  pitch,
                  fov,
                  tanOfHalfFov;
    
    private static float cameraMoveGrain = 0.5f, cameraRotateGrain = 5.0f;
    
    /**
     * Camera constructor. It takes in some general information about
     * the camera and calculates the forward vector, right vector, 
     * & yaw, & pitch
     * 
     * @param pos The position of the camera in 3D space
     * @param target The point where it is looking at
     * @param up The up vector of the camera
     * @param fov The field of view
     */
    public Camera(Vectors.vec3 pos, Vectors.vec3 target, Vectors.vec3 up, int fov) {
        this.pos = pos;
        
        forward = target
                  .subtract(pos)
                  .normalize();
        
        right = forward
                .cross(Core.WORLDUP)
                .normalize();
        this.up = right
                  .cross(forward)
                  .normalize();
        
        yaw = (float)(Math.atan2(forward.x, forward.z) * RAD_TO_DEG);
        float fy = forward.y;
        if (fy > 1.0f)  { fy =  1.0f; }
        if (fy < -1.0f) { fy = -1.0f; }
        pitch = (float)(Math.asin(fy) * RAD_TO_DEG);
        
        this.fov = (float)(fov * DEG_TO_RAD);       //Store FOV in radians
        tanOfHalfFov = (float)Math.tan(this.fov / 2.0);
        
        m = new Util.Material(new Vectors.vec3(200.0f));
        type = "camera"; name = "camera";
    }
    
    /**
     * Uses normalized screen-space coordinates, along with the fields of 
     * this {@link Camera} to calculate the direction of the ray.
     * 
     * @param x x coordinate on the screen-space.
     * @param y y coordinate on the screen-space.
     * @param aspectRatio width of canvas divided by the height.
     * @return A normalized vector that is the direction we will march.
     */
    public Vectors.vec3 getRayDirection(float x, float y, float aspectRatio) {
        float px = (float)((2.0f * x - 1.0f) * tanOfHalfFov * aspectRatio),
              py = (float)((1.0f - 2.0f * y) * tanOfHalfFov);
        return  right.scale(px)
                .add(up.scale(py))
                .add(forward)
                .normalize();
    }
    
    /**
     * Moves the camera by some movement vector.
     * 
     * @param movement The movement vector.
     */
    public void move(Vectors.vec3 movement) {
        // Add movement vector.
        this.pos = this.pos.add(movement);
    }
    /**
     * Rotates this {@link Camera} horizontally and / or vertically.
     * 
     * @param yawDelta Horizontal rotation.
     * @param pitchDelta Vertical rotation.
     */
    public void rotate(float yawDelta, float pitchDelta) {
        forward = rotateAroundAxis(forward, right, pitchDelta);
        forward = rotateAroundAxis(forward, Core.WORLDUP, yawDelta);
        forward = forward.normalize();
        right = forward.cross(Core.WORLDUP).normalize();
        up = right.cross(forward).normalize();
    }
    /**
     * Zooms this {@link Camera} in or out by some percent.
     * 
     * @param zoomDeltaPercent The change in zoom in percent.
     */
    public void zoom(float zoomDeltaPercent) { 
        // Calculate the new field of view, cap it so it's
        // below 3.13 ( pi ), then update it.
        float newFov = fov * (1 + zoomDeltaPercent);
        newFov = (newFov > 3.13f) ? 3.13f : newFov;     
        updateFov(newFov); 
    }
    
    //<editor-fold defaultstate="collapsed" desc=" Sensitivity Setters, Changes, and Getters ">
    public void setMovementSensitivity(float newSens) { cameraMoveGrain = newSens; }
    public float getMovementSensitivity() { return cameraMoveGrain; }
    
    public void setRotationSensitivity(float newSens) { cameraRotateGrain = newSens; }
    public float getRotationSensitivity() { return cameraRotateGrain; }
    //</editor-fold>
    
    /**
     * Updates FOV. Takes in FOV in radians, calculates
     * the tangent of half of FOV, and stores both.
     * @param fov New field of view in radians.
     */
    private void updateFov(float fov){
        this.fov = fov;
        tanOfHalfFov = (float)Math.tan(this.fov / 2.0);
    }
    private Vectors.vec3 rotateAroundAxis(Vectors.vec3 v, Vectors.vec3 axis, float angleDeg) {
        float angleRad = angleDeg * DEG_TO_RAD;
        float cos = (float)Math.cos(angleRad);
        float sin = (float)Math.sin(angleRad);
        return v.scale(cos)
                .add(axis.cross(v).scale(sin))
                .add(axis.scale(axis.dot(v) * (1 - cos)));
    }
    
    /**
     * Packages the basic vectors into an array 
     * and returns it.
     * 
     * @return The array of size 3
     */
    public Vectors.vec3[] getOrientation() {
        Vectors.vec3[] arr = new Vectors.vec3[3];
        arr[0] = forward;
        arr[1] = right;
        arr[2] = up;
        return arr;
    }
    /**
     * Gets and returns the current position of 
     * this {@link Camera}.
     * @return The current vector position.
     */
    public Vectors.vec3 getPosition() { return pos; }
    
    public String packageCamera() {
        return  pos.toString()      + "," +
                forward.toString()  + "," + 
                up.toString()       + "," + 
                right.toString()    + "," + 
                yaw + "," + pitch   + "," +
                fov                 + ",\n"; 
                         
    }
    public void unpackageCamera(String[] parts) {
        pos     = new Vectors.vec3(parts[0]);
        forward = new Vectors.vec3(parts[1]);
        up      = new Vectors.vec3(parts[2]);
        right   = new Vectors.vec3(parts[3]);
        yaw     = Float.parseFloat(parts[4]);
        pitch   = Float.parseFloat(parts[5]);
        updateFov((float) Double.parseDouble(parts[6]));
    }
    
    // SDFs for the camera.
    private SDFs.Primitives.Cube body = new SDFs.Primitives.Cube(new Vectors.vec3(0.0f), 0.1f, m);
    
    @Override 
    public float sdf(Vectors.vec3 p) { return body.sdf(p.subtract(pos)); }
    
    @Override
    public boolean parseNewParams(String[] p) { return false; }
}
