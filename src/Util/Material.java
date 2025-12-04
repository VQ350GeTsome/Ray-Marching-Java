package Util;

public class Material {
    
    public static final int FIELDS = 11;
    public vec3 color, specularColor = new vec3(255);
    public float    reflectivity    =  0.0f,
                    specular        =  0.5f,
                    shinyness       = 16.0f,
                    roughness       =  0.0f,
                    metalness       =  0.0f,
                    opacity         =  0.0f,
                    ior             =  1.5f,    //Glass
                    texture         =  0.0f,
                    textureness     =  0.0f;
    //Light emission later ... maybe
    
    //<editor-fold defaultstate="collapsed" desc=" Common Defaults ">
    public final static Material PLASTIC = new Material(new vec3(128));
    public final static Material MIRROR  = new Material(new vec3(255), 1.0f, 0.5f, 8.0f, 0.0f, 1.0f, 0.0f, 1.0f,  0.0f, 0.0f);
    public final static Material GLASS   = new Material(new vec3(255), 0.0f, 0.5f, 8.0f, 0.0f, 1.0f, 1.0f, 1.52f, 0.0f, 0.0f);
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc=" Constructors ">
    
    /**
     * Default Constructor, everything is 0.0f except the color ( white ) specular, ( 0.5f ),
     * shinyness, ( 16.0f ), & ior, ( 1.5f ).
     */
    public Material() { color = new vec3(0.0f); }
    /**
     * Color only Constructor, everything is 0.0f except the color ( input ) specular, ( 0.5f ),
     * shinyness, ( 16.0f ), & ior, ( 1.5f ).
     * 
     * @param color The color of the material
     */
    public Material(vec3 color) { this.color = color; }
    public Material(java.awt.Color color) { this.color = new vec3(color); }
    /**
     * Specific constructor, everything is as it's inputted.
     * @param color         The material color.
     * @param reflect       The reflectivity.
     * @param spec          The specular.
     * @param shiny         The shinyness.
     * @param rough         The roughness.
     * @param metal         The metalness.
     * @param opacity       The opacity.
     * @param ior           The index of refraction.
     * @param texture       The texture.
     * @param textureness   The textureness ( size of texture ).
     */
    public Material(vec3 color, float reflect, float spec, float shiny, float rough, float metal,
        float opacity, float ior, float texture, float textureness) {
        this.color = color; 
        reflectivity = reflect;
        specular = spec;
        shinyness = shiny;
        roughness = rough;
        metalness = metal;
        this.opacity = opacity;
        this.ior = ior;
        this.texture = texture;
        this.textureness = textureness;
    }
    /**
     * Copy constructor with a custom color.
     * This is intended to be used with the common default materials as 
     * they all have a gray color.
     * 
     * @param color The new color.
     * @param m   The material.
     */
    public Material(vec3 color, Material m) {
        this.color = color;
        reflectivity    = m.reflectivity;
        specular        = m.specular;
        shinyness       = m.shinyness;
        roughness       = m.roughness;
        metalness       = m.metalness;
        opacity         = m.opacity;
        ior             = m.ior;
        texture         = m.texture;
        textureness     = m.textureness;
    }
    public Material(java.awt.Color color, Material m) { this(new vec3(color), m); }
    
    //</editor-fold>
    
    public Material blend(Material b, float w) {
        Material m = new Material(color.blend(b.color, w));
        
        m.reflectivity  = wAvg(reflectivity, b.reflectivity, w);
        m.specular      = wAvg(specular,     b.specular,     w);
        m.roughness     = wAvg(roughness,    b.roughness,    w);
        m.metalness     = wAvg(metalness,    b.metalness,    w);
        m.opacity       = wAvg(opacity,      b.opacity,      w);
        m.ior           = wAvg(ior,          b.ior,          w);
        m.texture       = wAvg(texture,      b.texture,      w);
        m.textureness   = wAvg(textureness,  b.textureness,  w);
        
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
                    shinyness    + "," +
                    roughness    + "," + 
                    metalness    + "," + 
                    opacity      + "," + 
                    ior          + "," +
                    texture      + "," +
                    textureness;
        return str;
    }
    public String[] toStringArray() {
        return new String[] 
        { 
            colorString(color), colorString(specularColor), ""+reflectivity, 
            ""+specular, ""+shinyness, ""+roughness, ""+metalness, ""+opacity, ""+ior,
            ""+texture, ""+textureness
        };
    }
    public String colorString(vec3 color) {
        return (int) color.x + ":" + (int) color.y + ":" + (int) color.z;
    }
}
