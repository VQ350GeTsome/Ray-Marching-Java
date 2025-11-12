package SDFs.Repeating;

import Utility.*;

public class RepeatSphere extends SDFs.SDF{

    private float s;
    
    SDFs.Primitives.Sphere sphere;
    
    public RepeatSphere(vec3 center, float radius, float spacing, java.awt.Color color) {
        type = "sphere";
        s = spacing;
        sphere = new SDFs.Primitives.Sphere(center, radius, color);
    }
    
    public RepeatSphere(SDFs.Primitives.Sphere sphere, float spacing) {
        type = "sphere";
        s = spacing;
        this.sphere = sphere;
    }

    @Override
    public float sdf(vec3 pos) {

        //Wrap each coordinate into a cell centered at 0
        float rx = pos.x - s * (float)Math.floor(pos.x / s + 0.5f);
        float ry = pos.y - s * (float)Math.floor(pos.y / s + 0.5f);
        float rz = pos.z - s * (float)Math.floor(pos.z / s + 0.5f);

        vec3 local = new vec3(rx, ry, rz);
        return sphere.sdf(local); //Query SDF of the primitive Sphere in local wrapped space
    }
    
    @Override
    public Material getMaterial(vec3 p) { return sphere.getMaterial(p); }
    
    @Override
    public String[] getSettingsAndCurrent() {
        String[] current = ArrayMath.subArray(toString().split(","), 1, 5);
        return ArrayMath.add(super.getSettingsAndCurrent(), current);
    } 
    
    @Override
    public String toString() { 
        String temp = "repeat" + sphere.toString();
        int length = temp.length();
        temp = temp.substring(0, length - 1);
        return temp + s + "\n";
    }
      
}
