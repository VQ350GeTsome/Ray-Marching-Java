package SDFs.Primitives;

import Utility.*;

public class Torus extends SDFs.SDF{
    
    private vec3 center;
    private float majorR, minorR;
    
    public Torus(vec3 center, float majorR, float minorR, java.awt.Color color) { 
        this.center = center; this.majorR = majorR; this.minorR = minorR;
        material = new Material(color);
    }
    
    public float sdf(vec3 point){
        point = point.subtract(center);
        
        float radial = (float)Math.sqrt(point.x * point.x + point.y * point.y) - majorR;
        float tubeDist = (float)Math.sqrt((radial * radial + point.z) * point.z);
        return tubeDist - minorR;
    }

    public String getType() { return "torus"; }
    
    public String[] getSettingsAndCurrent() {
        String[] current = new String[] { material.colorString(), center.toStringParen(), ""+majorR, ""+minorR };
        return ArrayMath.add(SDFs.SDFParser.torusSettings(), current);
    }
    
    @Override
    public String toString() {
        return super.toString() + center.toString() + "," + majorR + "," + minorR + ",\n";
    }
}
