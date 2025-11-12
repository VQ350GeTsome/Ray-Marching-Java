package SDFs.Special;

import Utility.*;

public class HollowChainCube extends SDFs.SDF {

    private vec3    c;
    private float   scale,
                    iterations = 10,
                    n;
    
    
    public HollowChainCube(vec3 center, float scale, float n, java.awt.Color color) {
        c = center;       
        this.n = n;
        this.scale = scale;
        material = new Material(color);
        type = "hollowcc";
    }
    
    @Override
    public float sdf(vec3 p) {
        p = p.subtract(c).divide(scale);  
        p = p.abs();
        float s = 2.0f, r2;
        
        for (int i = 0; i < 12; i++) {
            vec3 one = new vec3(n);
            p = one.subtract(p.subtract(one).abs());
            r2 = 1.20f / p.dot(p); 
            p = p.multiply(r2);     
            s *= r2;                
        }
        vec3 one = new vec3(1.0f);
        vec3 n = one.normalize();                
        vec3 cr = vec3.cross(p, n);             
        float dist = cr.length();
        return (dist / s) - 0.003f;
    }
    
    @Override
    public String[] getSettingsAndCurrent() { 
        String[] current = { material.colorString(), c.toStringParen(), ""+scale };
        return ArrayMath.add(super.getSettingsAndCurrent(), current);
    }
    
    @Override
    public String toString() { return super.toString() + c.toString() + "," + scale + "," + n + ",\n"; }
}
