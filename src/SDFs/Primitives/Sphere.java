package SDFs.Primitives;

import Utility.*;

public class Sphere extends SDFs.SDF{
        
    private vec3 center;
    private float radius;

    public Sphere(vec3 center, float radius, java.awt.Color color) { 
        this.radius = radius; this.center = center; 
        material = new Material(color);
    }
    
    public float sdf(vec3 point) { return point.getDist(center) - radius; }
    
    public String[] getSettingsAndCurrent() {
        String[] current = new String[] { material.colorString(), center.toStringParen(), ""+radius };
        return ArrayMath.add(SDFs.SDFParser.sphereSettings(), current);
        
    }
    
    public String getType() { return "sphere"; }
    
    @Override
    public String toString() {
        return super.toString() + center.toString() + "," + radius + ",\n";
    }
 }
