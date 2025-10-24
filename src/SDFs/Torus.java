package SDFs;

import java.awt.Color;
import Utility.vec3;


public class Torus extends SDF{
    
    private vec3 center;
    private float majorR, minorR;
    private Color color;
    
    public Torus(vec3 center, float majorR, float minorR, Color color) { this.center = center; this.majorR = majorR; this.minorR = minorR; this.color = color; }
    
    public float sdf(vec3 point){
        point = point.subtract(center);
        
        float radial = (float)Math.sqrt(point.x * point.x + point.y * point.y) - majorR;
        float tubeDist = (float)Math.sqrt((radial * radial + point.z) * point.z);
        return tubeDist - minorR;
    }
    public Color getColor() { return color; }
    public String getType() { return "torus"; }
}
