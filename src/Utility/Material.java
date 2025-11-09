package Utility;

import java.awt.Color;

public class Material {
    
    public Color color;
    
    public Material(Color c) {
        color = c;
    }
    
    public String colorString() {
        return color.getRed() + ":" + color.getGreen() + ":" + color.getBlue();
    }
}
