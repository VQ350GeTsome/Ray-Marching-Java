package objects;

import java.awt.Color;
import utilities.Vector3;


public class FractalSphere extends ObjectRM{

    private Vector3 center;
    private double radius, spacing;
    private Color color;
    
    public FractalSphere(Vector3 center, double radius, double spacing, Color color) { this.radius = radius; this.color = color; this.spacing = spacing; this.center = center; }
    
    public double sdf(Vector3 point) {
        Vector3 worldP = point.subtract(center); // move into object space

        // Wrap each coordinate into a cell centered at 0
        double rx = ((worldP.getX() % spacing) + spacing) % spacing - spacing * 0.5;
        double ry = ((worldP.getY() % spacing) + spacing) % spacing - spacing * 0.5;
        double rz = ((worldP.getZ() % spacing) + spacing) % spacing - spacing * 0.5;

        Vector3 local = new Vector3(rx, ry, rz);
        return local.length() - radius;
    }
    public Color getColor() { return color; }
      
}
