package SDFs.Special;

import Utility.*;

public class InfiniteColumn extends SDFs.SDF {

    private float r;
    private vec3 c;
    
    public InfiniteColumn(vec3 center, float radius, Material mat) {
        type = "infinitecolumn";
        
        c = center; r = radius; m = mat;
    }
    
    @Override
    public float sdf(vec3 p) {
        p = p.subtract(c);
        p = rotQuat.rotate(p);
        
        float d = new vec2(p.x, p.y).length() - r;
        d = Math.max(d, Math.abs(p.y) - r);
        
        return d;
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
            r = Float.parseFloat(inputs[1].trim());
            return true;
        } catch (Exception e) { return false; }
    }
   
    @Override
    public String toString() { return super.toString() + c.toString() + "," + r  + ",\n"; }
}
