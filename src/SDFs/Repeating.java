package SDFs;

import Utility.*;

public class Repeating extends SDFs.SDF{

    private float s;
    
    SDFs.SDF p;
    
    public Repeating(SDFs.SDF primitive, float spacing) {
        type = "repeatsphere";
        s = spacing;
        p = primitive;
    }

    @Override
    public float sdf(vec3 pos) {

        //Wrap each coordinate into a cell centered at 0
        float rx = pos.x - s * (float)Math.floor(pos.x / s + 0.5f);
        float ry = pos.y - s * (float)Math.floor(pos.y / s + 0.5f);
        float rz = pos.z - s * (float)Math.floor(pos.z / s + 0.5f);

        vec3 local = new vec3(rx, ry, rz);
        return p.sdf(local); //Query SDF of the p in local wrapped space
    }
    
    @Override
    public Material getMaterial(vec3 p) { return this.p.getMaterial(p); }
    
    @Override
    public String[] getSettingsAndCurrent() {
        String[] current = ArrayMath.subArray(toString().split(","), 1, 5);
        return ArrayMath.add(super.getSettingsAndCurrent(), current);
    } 
    
    @Override
    public boolean parseNewParams(String[] inputs) { return p.parseNewParams(inputs); }
    
    public void setPrimitive(SDFs.SDF primitive) { p = primitive; }
    
    @Override
    public String toString() { 
        String temp = "repeat" + p.toString();
        int length = temp.length();
        temp = temp.substring(0, length - 1);
        return temp + s + "\n";
    }
      
}
