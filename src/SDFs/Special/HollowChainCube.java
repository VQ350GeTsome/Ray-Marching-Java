package SDFs.Special;

import Utility.*;

public class HollowChainCube extends SDFs.SDF {

    private vec3 c;
    private float scale;
    private float iterations = 10;
    
    
    public HollowChainCube(java.awt.Color color, vec3 center, float scale) {
        c = center;       
        this.scale = scale;
        material = new Material(color);
    }
    
    public float sdf(vec3 p) {
        p = p.subtract(c);

        float s = 2.0f, r2;

        p = p.abs();
        for (int i = 0; i < 12; i++) {
            vec3 one = new vec3(1.0f);
            p = one.subtract(p.subtract(one).abs());
            r2 = 1.20f / p.dot(p); 
            p = p.multiply(r2);     
            s *= r2;                
        }

        vec3 one = new vec3(1.0f);
        vec3 n = one.normalize();                
        vec3 cr = vec3.cross(p, n);             
        float dist = cr.length();
        return dist / s - 0.003f;
    }
    
    public String getType() { return "twisted"; }
    public String[] getSettingsAndCurrent() { 
        return null;
    }
}
