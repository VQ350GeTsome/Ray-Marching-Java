package SDFs.Primitives;

import Utility.*;

public class Plane extends SDFs.SDF {
    
    private vec3 c, n;
    
    public Plane(vec3 center, vec3 normal, Material material) {
        type = "plane";
        
        m = material; c = center; n = normal;
    }
    
    @Override
    public float sdf(vec3 p) {
        vec3 d = p.subtract(c);
        return n.dot(d);
    }
    
    @Override
    public String getType() { return "plane"; }
    
    @Override
    public String[] getSettingsAndCurrent() {
        String[] current = ArrayMath.subArray(this.toString().split(","), 1, 3);
        return ArrayMath.add(super.getSettingsAndCurrent(), current);
    }
    
    @Override
    public boolean parseNewParams(String[] inputs) {
        try {
            c = new vec3(inputs[0].trim());
            n = new vec3(inputs[1].trim());
            return true;
        } catch (Exception e) { return false; }
    }
    
    @Override
    public String toString() {
        return super.toString() + c.toString() + "," + n.toString() + ",";
    }

}
