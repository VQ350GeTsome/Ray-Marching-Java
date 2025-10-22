package SDFs;

import java.awt.Color;
import Utility.vec3;

public class Sphere extends SDF{
    
    private vec3 center;
    private float radius;
    private Color color;
    
    public Sphere(vec3 center, float radius, Color color) { this.radius = radius; this.color = color; this.center = center; }
    
    public float sdf(vec3 point) { return point.getDist(center) - radius; }
    public Color getColor() { return color; }

}
