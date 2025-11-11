package SDFs.Primitives;

import Utility.*;

public class Sphere extends SDFs.SDF{
        
    private vec3 c;
    private float r;

    public Sphere(vec3 center, float radius, java.awt.Color color) { 
        r = radius; c = center; 
        material = new Material(color);
    }
    
    public float sdf(vec3 point) { return point.getDist(c) - r; }
    
    public String[] getSettingsAndCurrent() {
        String[] current = new String[] { material.colorString(), c.toStringParen(), ""+r };
        return ArrayMath.add(SDFs.SDFParser.sphereSettings(), current);
        
    }
    
    public String getType() { return "sphere"; }
    
    @Override
    public String toString() {
        return super.toString() + c.toString() + "," + r + ",\n";
    }
 }
