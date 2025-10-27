package Utility;

public class HitInfo {
    /**
     * Point of contact
     */
    public vec3 hit;
    /**
     * Total distance traveled
     */
    public float totalDist;
    
    public HitInfo(vec3 hit, float total) {
        this.hit = hit; totalDist = total;
    }
}
