package SDFs.Primitives;

import Utility.*;

public class Torus extends SDFs.SDF{
    
    private vec3 c;
    private float r1, r2;
    
    public Torus(vec3 center, float majorR, float minorR, Material material) { 
        type = "torus";
        
        c = center; r1 = majorR; r2 = minorR;
        m = material;
    }
    
    @Override
    public float sdf(vec3 point){
        point = point.subtract(c);
        
        float radial = (float)Math.sqrt(point.x * point.x + point.y * point.y) - r1;
        float tubeDist = (float)Math.sqrt(radial * radial + point.z * point.z);
        return tubeDist - r2;
    }
    
    @Override
    public String[] getSettingsAndCurrent() {
        String[] current = new String[] { m.colorString(), c.toStringParen(), ""+r1, ""+r2 };
        return ArrayMath.add(super.getSettingsAndCurrent(), current);
    }
    
    @Override
    public String toString() { return super.toString() + c.toString() + "," + r1 + "," + r2 + ",\n"; }
}
