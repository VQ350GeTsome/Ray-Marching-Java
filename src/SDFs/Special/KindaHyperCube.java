package SDFs.Special;

import Util.vec3;
import Util.ArrayMath;
import Util.Material;

public class KindaHyperCube extends SDFs.SDF {
    
    private vec3 c;
    private float s;
   
    public KindaHyperCube(vec3 center, float size, Material material) { 
        type = "kindahypercube";
        
        c = center; s = size; m = material;
    }
        
    @Override
    public float sdf(vec3 p){
        vec3 d = p
	        .subtract(c)
	        .abs()
	        .subtract(new vec3(s, s, s));
        d = rotQuat.rotate(d);
        
        vec3 max = d.max(new vec3());
        float outsideDist = max.length();
        float insideDist = Math.max(Math.max(d.x, d.y), d.z);
        return (float)(outsideDist + Math.min(insideDist, 0.0));
    }
    
    @Override
    public String[] getSettingsAndCurrent() {
        String[] current = ArrayMath.subArray(this.toString().split(","), START, START + 2);
        return ArrayMath.add(super.getSettingsAndCurrent(), current);
    }
    
    @Override
    public boolean parseNewParams(String[] inputs) {
        try {
            c = new vec3(inputs[0].trim());
            s = Float.parseFloat(inputs[1].trim());
            return true;
        } catch (Exception e) { 
            System.err.println(e.getMessage());
            return false; 
        }        
    }
    
    @Override
    public String toString() {
        return super.toString() + c.toString() + "," + s + ",\n";
    }
}
