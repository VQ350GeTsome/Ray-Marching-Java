package SDFs.Primitives;

import Utility.*;

public class Cylinder extends SDFs.SDF {

    private float r, h;
    private vec3 c;
    
    public Cylinder(vec3 center, float radius, float height, Material mat) {
        type = "cylinder";
        
        c = center; r = radius; h = height; m = mat;
    }
    
    @Override
    public float sdf(vec3 p) {
        p = p.subtract(c);
        p = rotQuat.rotate(p);
        
        float d = new vec2(p.x, p.y).length() - r;
        d = Math.max(d, Math.abs(p.y) - h);
        
        return d;
    }
    
    @Override
    public String[] getSettingsAndCurrent() {
        return null;
    }
    
    @Override
    public boolean parseNewParams(String[] inputs) {
        try {
            c = new vec3(inputs[0].trim());
            r = Float.parseFloat(inputs[1].trim());
            h = Float.parseFloat(inputs[2].trim());
            return true;
        } catch (Exception e) { return false; }
    }
}
