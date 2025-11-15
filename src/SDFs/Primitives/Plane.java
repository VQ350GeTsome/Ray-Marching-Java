package SDFs.Primitives;

import Utility.*;

public class Plane extends SDFs.SDF {
    
    private vec3 c, n;
    
    public Plane(vec3 center, vec3 normal, Material material) {
        type = "plane";
        
        m = material; c = center; n = normal;
    }
    
    public float sdf(vec3 p) {
        vec3 d = p.subtract(c);
        return n.dot(d);
    }
    
    public String getType() { return "plane"; }
    
    public String[] getSettingsAndCurrent() {
        String[] current = new String[] { m.colorString(), c.toStringParen(), n.toStringParen() };
        return ArrayMath.add(super.getSettingsAndCurrent(), current);
    }
    
    public String toString() {
        return super.toString() + c.toString() + "," + n.toString() + ",";
    }

}
