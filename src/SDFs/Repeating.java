package SDFs;

import Vectors.vec3;
import Util.ArrayMath;
import Util.Material;

public class Repeating extends SDFs.SDF{

    private float s;
    private SDFs.SDF p;
    
    public Repeating(SDFs.SDF primitive, float spacing) {
        type = "repeatsphere";
        s = spacing;
        p = primitive;
    }

    @Override
    public float sdf(vec3 pos) {
        // Cell index
        vec3 id = (pos.divide(s).round());

        // Neighbor offset direction 
        vec3 o = pos.subtract(id.scale(s)).sign();

        float d = Float.MAX_VALUE;

        // Check 2 neighbors in each axis ... so 8 times.
        for (int k = 0; k < 2; k++) for (int j = 0; j < 2; j++) for (int i = 0; i < 2; i++) {
            vec3 rid = id.add(new vec3(i, j, k).multiply(o));
            vec3 r   = pos.subtract(rid.scale(s));
            d = Math.min(d, p.sdf(r));
        }
        return d;
    }
    
    @Override
    public Material getMaterial(vec3 p) { return this.p.getMaterial(p); }
    
    @Override
    public String[] getSettingsAndCurrent() {
        String[] current = ArrayMath.subArray(toString().split(","), 1, 5);
        return ArrayMath.add(super.getSettingsAndCurrent(), current);
    } 
    
    @Override
    public boolean parseNewParams(String[] inputs) { return p.parseNewParams(inputs); }
    
    public void setPrimitive(SDFs.SDF primitive) { p = primitive; }
    
    @Override
    public String toString() { 
        String temp = "repeat" + p.toString();
        int length = temp.length();
        temp = temp.substring(0, length - 1);
        return temp + s + "\n";
    }
      
}
