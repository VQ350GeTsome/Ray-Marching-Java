package Utility;

public class HitInfo {
  
    public vec3 hit;
    public float totalDist;
    public SDFs.SDF sdf;
    
    public HitInfo(vec3 hit, float total, SDFs.SDF sdf) {
        this.hit = hit; totalDist = total; this.sdf = sdf;
    }
}
