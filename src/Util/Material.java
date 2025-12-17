package Util;

import Vectors.vec3;

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
    public final static Material PLASTIC = new Material(new vec3(128)),
                                 MIRROR  = new Material(new vec3(255), 1.0f, 0.5f,  8.0f, 0.0f, 1.0f, 0.0f, 1.00f, 0.0f, 0.0f),
                                 GLASS   = new Material(new vec3(255), 0.0f, 0.5f,  8.0f, 0.0f, 1.0f, 1.0f, 1.52f, 0.0f, 0.0f),
                                 METAL   = new Material(new vec3(128), 0.4f, 0.3f, 16.0f, 0.0f, 1.0f, 0.0f, 1.00f, 0.0f, 0.0f);
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
        this.reflectivity = reflect;
        this.specular = spec;
        this.shinyness = shiny;
        this.roughness = rough;
        this.metalness = metal;
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
        this.reflectivity    = m.reflectivity;
        this.specular        = m.specular;
        this.shinyness       = m.shinyness;
        this.roughness       = m.roughness;
        this.metalness       = m.metalness;
        this.opacity         = m.opacity;
        this.ior             = m.ior;
        this.texture         = m.texture;
        this.textureness     = m.textureness;
    }
    public Material(java.awt.Color color, Material m) { this(new vec3(color), m); }
    
    //</editor-fold>
    
    public Material blend(Material b, float w) {
        Material m = new Material(this.color.blend(b.color, w));
        
        m.reflectivity  = wAvg(this.reflectivity, b.reflectivity, w);
        m.specular      = wAvg(this.specular,     b.specular,     w);
        m.roughness     = wAvg(this.roughness,    b.roughness,    w);
        m.metalness     = wAvg(this.metalness,    b.metalness,    w);
        m.opacity       = wAvg(this.opacity,      b.opacity,      w);
        m.ior           = wAvg(this.ior,          b.ior,          w);
        m.texture       = wAvg(this.texture,      b.texture,      w);
        m.textureness   = wAvg(this.textureness,  b.textureness,  w);
        
        return m;
    }
    
    private float wAvg(float a, float b, float w) { return (a * (1.0f - w)) + (b * w); }
    
    @Override
    public String toString() {
        //Turn the color a string
        String str = 
                    colorString(this.color) + "," +
                    colorString(this.specularColor) + "," +
                    this.reflectivity + "," + 
                    this.specular     + "," +
                    this.shinyness    + "," +
                    this.roughness    + "," + 
                    this.metalness    + "," + 
                    this.opacity      + "," + 
                    this.ior          + "," +
                    this.texture      + "," +
                    textureness;
        return str;
    }
    public String[] toStringArray() {
        return new String[] 
        { 
            colorString(this.color), colorString(this.specularColor), ""+this.reflectivity, 
            ""+this.specular, ""+this.shinyness, ""+this.roughness, ""+this.metalness, ""+this.opacity, ""+this.ior,
            ""+this.texture, ""+this.textureness
        };
    }
    public String colorString(vec3 color) {
        return (int) color.x + ":" + (int) color.y + ":" + (int) color.z;
    }
}
