package SDFs.Special;

import Utility.*;

public class Twisted extends SDFs.SDF {

    private vec3 c;
    private Material m;
    private float scale;
    private float iterations = 10;
    
    
    public Twisted(java.awt.Color color, vec3 center, float scale) {
        c = center;       
        this.scale = scale;
        m = new Material(color);
    }
    
    private float miniBox(vec3 p, vec3 b) {
        vec3 d;
        d = p.abs().subtract(b);
        return Math.min(Math.max(d.x, Math.max(d.y, d.z)), 0) + (d.max(0)).length();
    }
    
    public float sdf(vec3 p) {
        p = p.subtract(c);

        vec3 b = new vec3(1.0f, 1.125f, 0.625f).multiply(scale - 1.0f);
        float r = (new vec2(p.x, p.z)).length();
        float a = (r > 0.0f) ? (float)(Math.atan2(p.z, -p.x) / (2.0 * Math.PI)) : 0.0f;

        float t = 16.0f * a + 1.0f;
        t = ((t % 2.0f) + 2.0f) % 2.0f;
        p.x = t - 1.0f;

        p.z = (float)(r - 32.0f / (2.0f * Math.PI));

        vec2 pyz = new vec2(p.y, p.z);
        pyz = vec2.rotate(pyz, (float)(a * Math.PI));
        p.y = pyz.x;
        p.z = pyz.y;

        for (int i = 0; i < iterations; i++) {
            p = p.abs();

            if (p.x < p.y) { float tmp = p.x; p.x = p.y; p.y = tmp; }
            if (p.x < p.z) { float tmp = p.x; p.x = p.z; p.z = tmp; }
            if (p.y < p.z) { float tmp = p.y; p.y = p.z; p.z = tmp; }

            if (p.z >= -0.5f * b.z) p.z += b.z;
        }

        float denom = (float) Math.pow(scale, iterations);
        return 0.8f * miniBox(p, new vec3(1.0f)) / denom;
    }
    
    public String getType() { return "twisted"; }
    public String[] getSettingsAndCurrent() { 
        return null;
    }
}
