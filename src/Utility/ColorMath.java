package Utility;

import java.awt.Color;


public class ColorMath {

    public static Color blend(Color color1, Color color2) {
        int r = (color1.getRed()   + color2.getRed())   / 2;
        int g = (color1.getGreen() + color2.getGreen()) / 2;
        int b = (color1.getBlue()  + color2.getBlue())  / 2;
        return new Color(r, g, b); 
    }
    /**
     * Blends two colors together such that:
     * @param color1 The first color    ( w = 0 : color1 )
     * @param color2 The second color   { w = 1 : color2 )
     * @param w Weight amount
     * @return 
     */
    public static Color blend(Color color1, Color color2, float w) {
        w = Math.min(w, 1.0f);
        w = Math.max(w, 0.0f);
        double r = (1 - w) * color1.getRed()   + w * color2.getRed();
        double g = (1 - w) * color1.getGreen() + w * color2.getGreen();
        double b = (1 - w) * color1.getBlue()  + w * color2.getBlue();
        return new Color((int)r, (int)g, (int)b); 
    }
    public static Color add(Color p, Color q) {
        int r = Math.min(255, p.getRed()   + q.getRed());
        int g = Math.min(255, p.getGreen() + q.getGreen());
        int b = Math.min(255, p.getBlue()  + q.getBlue());
        return new Color(r, g, b);
    }
    public static Color scale(Color p, float amount) {
        int r = (int)Math.min(255, p.getRed()   * amount);
        int g = (int)Math.min(255, p.getGreen() * amount);
        int b = (int)Math.min(255, p.getBlue()  * amount);
        return new Color(r, g, b);
    }
    public static float getBrightness(Color c) {
        return (c.getRed() + c.getGreen() + c.getBlue()) / 3.0f;
    }
    
}
