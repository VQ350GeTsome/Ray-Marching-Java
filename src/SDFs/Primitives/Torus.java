package SDFs.Primitives;

import Vectors.vec3;
import Util.ArrayMath;
import Util.Material;

public class Torus extends SDFs.SDF{
    
    private vec3 c;
    private float r1, r2;
    
    public Torus(vec3 center, float majorR, float minorR, Material material) { 
        type = "torus";
        
        c = center; r1 = majorR; r2 = minorR;
        m = material;
    }
    
    @Override
    public float sdf(vec3 p){
        p = p.subtract(c);
        p = rotQuat.rotate(p);
        
        float radial = (float)Math.sqrt(p.x * p.x + p.y * p.y) - r1;
        float tubeDist = (float)Math.sqrt(radial * radial + p.z * p.z);
        return tubeDist - r2;
    }
    
    @Override
    public String[] getSettingsAndCurrent() {
        String[] current = ArrayMath.subArray(this.toString().split(","), START, START + 3);
        return ArrayMath.add(super.getSettingsAndCurrent(), current);
    }
    
    @Override
    public boolean parseNewParams(String[] inputs) {
        try {
            c = new vec3(inputs[0].trim());
            r1 = Float.parseFloat(inputs[1].trim());
            r2 = Float.parseFloat(inputs[2].trim());
            return true;
        } catch (Exception e) { return false; } 
    }
    
    @Override
    public String toString() { return super.toString() + c.toString() + "," + r1 + "," + r2 + ",\n"; }
}
