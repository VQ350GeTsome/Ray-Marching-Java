package Utility;

public class Material {
    
    public static final int FIELDS = 8;
    
    public vec3 color, specularColor = new vec3(255.0f);
    public float    reflectivity    =  0.0f,
                    specular        =  0.75f,
                    shinyness       = 16.0f,
                    roughness       =  0.0f,
                    metalness       =  0.0f,
                    opacity         =  0.0f,
                    ior             =  0.0f;
    //Light emission later ... maybe
    
    public Material() { color = new vec3(0.0f); }
    public Material(vec3 color) { this.color = color; }
    
    public Material blend(Material b, float w) {
        Material m = new Material(color.blend(b.color, w));
        
        m.reflectivity  = wAvg(reflectivity, b.reflectivity, w);
        m.specular      = wAvg(specular,     b.specular,     w);
        m.roughness     = wAvg(roughness,    b.roughness,    w);
        m.metalness     = wAvg(metalness,    b.metalness,    w);
        m.opacity       = wAvg(opacity,      b.opacity,      w);
        m.ior           = wAvg(ior,          b.ior,          w);
        
        return m;
    }
    
    private float wAvg(float a, float b, float w) { return (a * (1.0f - w)) + (b * w); }
    
    @Override
    public String toString() {
        //Turn the color a string
        String str = 
                    colorString(color) + "," +
                    colorString(specularColor) + "," +
                    reflectivity + "," + 
                    specular     + "," +
                    roughness    + "," + 
                    metalness    + "," + 
                    opacity      + "," + 
                    ior;
        return str;
    }
    public String[] toStringArray() {
        return new String[] 
        { 
            colorString(color), colorString(specularColor), ""+reflectivity, 
            ""+specular, ""+roughness, ""+metalness, ""+opacity, ""+ior
        };
    }
    
    public String colorString(vec3 color) {
        return (int) color.x + ":" + (int) color.y + ":" + (int) color.z;
    }
}
