package Core;

import Utility.vec3;

public class Camera {

    private vec3	pos,
		forward,
		up,
		right,
		worldUp;
    
    private float	yaw,
		pitch;
    
    private int fov;
    
    /**
     * Camera constructor
     * @param pos
     * @param target
     * @param up
     * @param fov 
     */
    public Camera(vec3 pos, vec3 target, vec3 up, int fov) {
        this.pos = pos;
        forward = pos
	      .subtract( target )
	      .normalize();
        worldUp = up;
        
        yaw = (float)(Math.atan2(forward.x, forward.z) * (Math.PI / 180));
        float fy = forward.y;
        if (fy > 1.0f)  { fy =  1.0f; }
        if (fy < -1.0f) { fy = -1.0f; }
        pitch = (float)(Math.asin(fy) * (Math.PI / 180));
    }
}
