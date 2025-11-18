package Utility;

public class Material {
    
    public static final int FIELDS = 7;
    
    public vec3 color;
    public float    reflectivity    = 0.0f,
                    specular        = 0.0f,
                    roughness       = 0.0f,
                    metalness       = 0.0f,
                    opacity         = 0.0f,
                    ior             = 0.0f;
    //Light emission later ... maybe
    
    public Material() { color = new vec3(0.0f); }
    public Material(vec3 color) { this.color = color; }
    
    @Override
    public String toString() {
        //Turn the color a string
        String str = colorString() + "," +
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
            colorString(), ""+reflectivity, ""+specular,
            ""+roughness, ""+metalness, ""+opacity, ""+ior
        };
    }
    
    public String colorString() {
        return (int) color.x + ":" + (int) color.y + ":" + (int) color.z;
    }
}
