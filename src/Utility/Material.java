package Utility;

import java.awt.Color;

public class Material {
    
    public Color c;
    public float r = 0.0f;
    
    public Material(Color color) { c = color; }
    public Material(Color color, float reflect) { c = color; r = reflect; }
    
    @Override
    public String toString() {
        //Turn the color a string
        String str = colorString() + "," +
                   ""+r; 
        return str;
    }
    
    public String colorString() {
        return c.getRed() + ":" + c.getGreen() + ":" + c.getBlue();
    }
}
