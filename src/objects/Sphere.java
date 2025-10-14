package objects;

import java.awt.Color;
import utilities.Vector3;

public class Sphere extends ObjectRM{
    
    private Vector3 center;
    private double radius;
    private Color color;
    
    public Sphere(Vector3 center, double radius, Color color) { this.radius = radius; this.color = color; this.center = center; }
    
    public double sdf(Vector3 point) { return point.getDist(center) - radius; }
    public Color getColor() { return color; }

}
