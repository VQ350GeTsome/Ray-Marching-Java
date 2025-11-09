package SDFs.Primitives;

import SDFs.SDF;
import Utility.*;
import java.awt.Color;

public class Plane extends SDF {
    
    private vec3 pos, normal;
    
    public Plane(vec3 pos, vec3 normal, Color c) {
        material = new Material(c);
        this.pos = pos;
        this.normal = normal;
    }
    
    public float sdf(vec3 p) {
        vec3 d = p.subtract(pos);
        return normal.dot(d);
    }
    
    public String getType() { return "plane"; }
    
    public String[] getSettings() {
        return new String[] { "Color: ", "Position: ", "Normal: ", 
            material.colorString(), pos.toStringParen(), normal.toStringParen() };
    }
    
    public String toString() {
        return super.toString() + pos.toString() + "," + normal.toString() + ",";
    }

}
