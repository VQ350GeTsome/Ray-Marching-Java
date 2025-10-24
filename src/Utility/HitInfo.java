package Utility;

public class HitInfo {
    public vec3 hit;
    public float totalDist;
    
    public HitInfo(vec3 hit, float total) {
        this.hit = hit; totalDist = total;
    }
}
