package Utility;

public class Material {
    
    public vec3 color;
    public float reflectivity = 0.0f;
    
    public Material(vec3 color) { this.color = color; }
    public Material(vec3 color, float reflect) { 
        this.color = color; 
        reflectivity = Math.min(Math.max(reflect, 0.0f), 1.0f); //Lock reflectivtiy to [0, 1]
    }
    
    @Override
    public String toString() {
        //Turn the color a string
        String str = colorString() + "," +
                   ""+reflectivity; 
        return str;
    }
    public String[] toStringArray() {
        return new String[] { colorString(), ""+reflectivity };
    }
    
    public String colorString() {
        return (int) color.x + ":" + (int) color.y + ":" + (int) color.x;
    }
}
