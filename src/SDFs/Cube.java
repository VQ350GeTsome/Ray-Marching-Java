package SDFs;

import java.awt.Color;
import Utility.vec3;

public class Cube extends SDF {
    
    private vec3 center;
    private float size;
    private Color color;
    
    public Cube(vec3 center, float size, Color color) { this.center = center; this.size = size; this.color = color; }
    
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
    public Color getColor() { return color; }

}
