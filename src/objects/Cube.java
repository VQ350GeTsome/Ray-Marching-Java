package objects;

import java.awt.Color;
import utilities.Vector3;

public class Cube extends ObjectRM {
    
    private Vector3 center;
    private double size;
    private Color color;
    
    public Cube(Vector3 center, double size, Color color) { this.center = center; this.size = size; this.color = color; }
    
    public double sdf(Vector3 point){
        Vector3 d = point.subtract(center).abs().subtract(new Vector3(size, size, size));
        Vector3 max = d.max(new Vector3());
        double outsideDist = max.length();
        double insideDist = Math.max(Math.max(d.getX(), d.getY()), d.getZ());
        return outsideDist + Math.min(insideDist, 0.0);
    }
    public Color getColor() { return color; }

}
