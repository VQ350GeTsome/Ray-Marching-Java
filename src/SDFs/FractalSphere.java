package SDFs;

import java.awt.Color;
import Utility.vec3;


public class FractalSphere extends SDF{

    private vec3 center;
    private float radius, spacing;
    private Color color;
    
    public FractalSphere(vec3 center, float radius, float spacing, Color color) { this.radius = radius; this.color = color; this.spacing = spacing; this.center = center; }
    
    public float sdf(vec3 point) {
        vec3 worldP = point.subtract(center); // move into object space

        // Wrap each coordinate into a cell centered at 0
        float rx = ((worldP.x % spacing) + spacing) % spacing - spacing * 0.5f;
        float ry = ((worldP.y % spacing) + spacing) % spacing - spacing * 0.5f;
        float rz = ((worldP.z % spacing) + spacing) % spacing - spacing * 0.5f;

        vec3 local = new vec3(rx, ry, rz);
        return (local.length() - radius);
    }
    public Color getColor() { return color; }
      
}
