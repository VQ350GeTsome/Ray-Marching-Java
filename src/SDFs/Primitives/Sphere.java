package SDFs.Primitives;

import Utility.*;

public class Sphere extends SDFs.SDF{
        
    private vec3 c;
    private float r;

    public Sphere(vec3 center, float radius, Material material) { 
        type = "sphere";
        
        c = center; r = radius; m = material;
    }

    @Override
    public float sdf(vec3 point) { return point.getDist(c) - r; }
    
    @Override
    public String[] getSettingsAndCurrent() {
        String[] current = ArrayMath.subArray(toString().split(","), Material.FIELDS + 1, Material.FIELDS + 3);
        return ArrayMath.add(super.getSettingsAndCurrent(), current);
    }
    
    @Override
    public boolean parseNewParams(String[] inputs) {
        try {
            c = new vec3(inputs[0].trim());
            r = Float.parseFloat(inputs[1].trim());
            return true;
        } catch (Exception e) { return false; }
    }
    
    @Override
    public String toString() { return super.toString() + c.toString() + "," + r + ",\n"; }
 }
