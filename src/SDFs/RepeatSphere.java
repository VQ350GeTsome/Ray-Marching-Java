package SDFs;

import Utility.*;

public class RepeatSphere extends SDFs.SDF{

    private vec3 c;
    private float r, s;
    
    public RepeatSphere(vec3 center, float radius, float spacing, java.awt.Color color) {
        r = radius; s = spacing; c = center;
        
        material = new Material(color);
    }

    
    public float sdf(vec3 point) {
        vec3 worldP = point.subtract(c); // move into object space

        // Wrap each coordinate into a cell centered at 0
        float rx = ((worldP.x % s) + s) % s - s * 0.5f;
        float ry = ((worldP.y % s) + s) % s - s * 0.5f;
        float rz = ((worldP.z % s) + s) % s - s * 0.5f;

        vec3 local = new vec3(rx, ry, rz);
        return (local.length() - r);
    }
    
    public String[] getSettingsAndCurrent() {
        String[] current = new String[] { material.colorString(), c.toStringParen(), ""+r, ""+s };
        return ArrayMath.add(SDFs.SDFParser.repeatSphereSettings(), current);
    }
    
    public String getType() { return "repeatsphere"; }
    
    
    @Override
    public String toString() {
        return super.toString() + c.toString() + "," + r + ",\n";
    }
      
}
