package Util;

public class HitInfo {
  
    public Vectors.vec3 hit;
    public float totalDist;
    public SDFs.SDF sdf;
    public Util.Material mat;
        
    public HitInfo(Vectors.vec3 hit, float total, SDFs.SDF sdf) {
        this.hit = hit; totalDist = total; this.sdf = sdf;
        if (sdf != null) mat = sdf.getMaterial(hit);
    }
}
