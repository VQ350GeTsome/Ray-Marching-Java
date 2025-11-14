package SDFs.Primitives;

import Utility.*;

public class Plane extends SDFs.SDF {
    
    private vec3 p, n;
    
    public Plane(vec3 position, vec3 normal, Material material) {
        type = "plane";
        
        m = material; p = position; n = normal;
    }
    
    public float sdf(vec3 p) {
        vec3 d = p.subtract(p);
        return n.dot(d);
    }
    
    public String getType() { return "plane"; }
    
    public String[] getSettingsAndCurrent() {
        String[] current = new String[] { m.colorString(), p.toStringParen(), n.toStringParen() };
        return ArrayMath.add(super.getSettingsAndCurrent(), current);
    }
    
    public String toString() {
        return super.toString() + p.toString() + "," + n.toString() + ",";
    }

}
