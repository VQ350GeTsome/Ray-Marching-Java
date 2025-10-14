package objects;

import java.awt.Color;
import utilities.Vector3;


public class Torus extends ObjectRM{
    
    private Vector3 center;
    private double majorR, minorR;
    private Color color;
    
    public Torus(Vector3 center, double majorR, double minorR, Color color) { this.center = center; this.majorR = majorR; this.minorR = minorR; this.color = color; }
    
    public double sdf(Vector3 point){
        point = point.subtract(center);
        
        double radial = Math.sqrt(point.getX() * point.getX() + point.getY() * point.getY()) - majorR;
        double tubeDist = Math.sqrt(radial * radial + point.getZ() * point.getZ());
        return tubeDist - minorR;
    }
    public Color getColor() { return color; }

}
