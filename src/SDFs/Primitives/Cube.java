package SDFs.Primitives;

import Utility.*;

public class Cube extends SDFs.SDF {
    
    private vec3 c;
    private float s;
   
    public Cube(vec3 center, float size, Material material) { 
        type = "cube";
        
        c = center; s = size; m = material;
    }
        
    @Override
    public float sdf(vec3 point){
        vec3 d = point
	      .subtract(c)
	      .abs()
	      .subtract( new vec3(s, s, s) );
        vec3 max = d.max(new vec3());
        float outsideDist = max.length();
        float insideDist = Math.max(Math.max(d.x, d.y), d.z);
        return (float)(outsideDist + Math.min(insideDist, 0.0));
    }
    
    @Override
    public String[] getSettingsAndCurrent() {
        String[] current = ArrayMath.subArray(this.toString().split(","), 1, 5);
        return ArrayMath.add(super.getSettingsAndCurrent(), current);
    }
    
    @Override
    public String toString() {
        return super.toString() + c.toString() + "," + s + ",\n";
    }
}
