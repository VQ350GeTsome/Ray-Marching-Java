package SDFs.Primitives;

import SDFs.SDF;
import Utility.*;
import java.awt.Color;

public class Sphere extends SDF{
    
    private vec3 center;
    private float radius;
    
    public Sphere(vec3 center, float radius, Color color) { 
        this.radius = radius; this.center = center; 
        material = new Material(color);
    }
    
    public float sdf(vec3 point) { return point.getDist(center) - radius; }
    
    public String[] getSettings() {
        return new String[] { "Color: ", "Center: ", "Radius: ",
            material.colorString(), center.toStringParen(), ""+radius };
    }
    
    public String getType() { return "sphere"; }
    
    @Override
    public String toString() {
        return super.toString() + center.toString() + "," + radius + ",\n";
    }
 }
