package SDFs.Primitives;

import Utility.*;

public class Sphere extends SDFs.SDF{
        
    private vec3 c;
    private float r;

    public Sphere(vec3 center, float radius, Material material) { 
        type = "sphere";
        
        r = radius; c = center; m = material;
    }

    @Override
    public float sdf(vec3 point) { return point.getDist(c) - r; }
    
    @Override
    public String[] getSettingsAndCurrent() {
        String[] current = ArrayMath.subArray(toString().split(","), 1, 5);
        return ArrayMath.add(super.getSettingsAndCurrent(), current);
    }
    
    @Override
    public String toString() { return super.toString() + c.toString() + "," + r + ",\n"; }
 }
