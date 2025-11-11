package SDFs.Primitives;

import Utility.*;

public class Sphere extends SDFs.SDF{
    
    private static String[] settings = new String[] { "Color: ", "Center: ", "Radius: " };
    
    private vec3 center;
    private float radius;

    public Sphere(vec3 center, float radius, java.awt.Color color) { 
        this.radius = radius; this.center = center; 
        material = new Material(color);
    }
    
    public float sdf(vec3 point) { return point.getDist(center) - radius; }
    
    public String[] getSettingsAndCurrent() {
        String[] current = new String[] { material.colorString(), center.toStringParen(), ""+radius };
        return ArrayMath.add(settings, current);
        
    }
    public static String[] getSettings() { return settings; } 
    
    public String getType() { return "sphere"; }
    
    @Override
    public String toString() {
        return super.toString() + center.toString() + "," + radius + ",\n";
    }
 }
