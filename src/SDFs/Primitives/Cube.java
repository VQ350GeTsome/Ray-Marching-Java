package SDFs.Primitives;

import Utility.*;

public class Cube extends SDFs.SDF {
    
    private vec3 center;
    private float size;
    
   
    public Cube(vec3 center, float size, java.awt.Color color) { 
        this.center = center; this.size = size; 
        material = new Material(color);
    }
        
    public float sdf(vec3 point){
        vec3 d = point
	      .subtract(center)
	      .abs()
	      .subtract( new vec3(size, size, size) );
        vec3 max = d.max(new vec3());
        float outsideDist = max.length();
        float insideDist = Math.max(Math.max(d.x, d.y), d.z);
        return (float)(outsideDist + Math.min(insideDist, 0.0));
    }
    
    public String[] getSettingsAndCurrent() {
        return new String[] { "Color: ", "Center: ", "Size: ", 
            material.colorString(), center.toStringParen(), ""+size };
    }
    
    public String getType() { return "cube"; }
    
    @Override
    public String toString() {
        return super.toString() + center.toString() + "," + size + ",\n";
    }
}
