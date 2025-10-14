package utilities;

import java.awt.Color;


public class ColorMath {

    public static Color blend(Color color1, Color color2){
        int r = (color1.getRed()   + color2.getRed())   / 2;
        int g = (color1.getGreen() + color2.getGreen()) / 2;
        int b = (color1.getBlue()  + color2.getBlue())  / 2;
        return new Color(r, g, b); 
    }
    public static Color blend(Color color1, Color color2, double w){
        double r = w * color1.getRed()   + (1 - w) * color2.getRed();
        double g = w * color1.getGreen() + (1 - w) * color2.getGreen();
        double b = w * color1.getBlue()  + (1 - w) * color2.getBlue();
        return new Color((int)r, (int)g, (int)b); 
    }
    public static Color add(Color p, Color q){
        int r = Math.min(255, p.getRed()   + q.getRed());
        int g = Math.min(255, p.getGreen() + q.getGreen());
        int b = Math.min(255, p.getBlue()  + q.getBlue());
        return new Color(r, g, b);
    }
    public static Color scale(Color p, double amount){
        int r = (int)Math.min(255, p.getRed()   * amount);
        int g = (int)Math.min(255, p.getGreen() * amount);
        int b = (int)Math.min(255, p.getBlue()  * amount);
        return new Color(r, g, b);
    }
    
}
