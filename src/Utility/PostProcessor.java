package Utility;

import java.awt.Color;
import java.util.stream.IntStream;

public class PostProcessor {
    
    private static int width, height;
    
    public static void initalize(int w, int h) { width = w; height = h; }

    public static Color[][] addBloom(Color background, Color[][] image, int bloomSensitivity, int blurRadius) {
        
        Color[][] brightRegions = isolateBright(image, bloomSensitivity);
        
        Color[][] blurredBright = boxBlur(background, brightRegions, blurRadius);
        
        Color[][] finalImage = new Color[width][height];
        
        for (int x = 0; width > x; x++) for (int y = 0; height > y; y++) {  //Loop screen
            finalImage[x][y] = ColorMath.add(image[x][y], blurredBright[x][y]);
        }
        
        return finalImage;
    }
    
    private static Color[][] boxBlur(Color background, Color[][] image, int radius) {
        Color[][] blurredImage = new Color[width][height];
        
        IntStream.range(0, width).parallel().forEach(x -> {                     //Parellelize each x row and call the for loop for each y column
            for (int y = 0; height > y; y++) {  //Loop screen
                int r = 0, g = 0, b = 0, c = 0;     //Start rgb at 0 and the count (c)
                for (int dx = -radius; dx <= radius; dx++) for (int dy = -radius; dy <= radius; dy++) {     //Loop around the radius
                    int nx = x + dx;
                    int ny = y + dy;
                    if (nx >= 0 && nx < width && ny >= 0 && ny < height) {
                        Color color = image[nx][ny];
                        if (background.equals(color)) color = Color.BLACK;
                        r += color.getRed();
                        g += color.getGreen();
                        b += color.getBlue();
                        c++;
                        
                    }
                }
                if (c == 0) blurredImage[x][y] = Color.BLACK;
                else blurredImage[x][y] = new Color(r / c, g / c, b / c);
            }
        });
        return blurredImage;
    }    
    /**
     * Returns a new Color[][] where it's the isolated bright regions
     * where bright is where the average of the three r , g , b components
     * are greater than k
     * @param image The image to process
     * @param k     The threshold
     * @return      The isolated bright areas
     */
    private static Color[][] isolateBright(Color[][] image, int k) {
        Color[][] brightRegion = new Color[width][height];
        IntStream.range(0, width).parallel().forEach(x -> {                     //Parellelize each x row and call the for loop for each y column
            for (int y = 0; height > y; y++) {
                Color c = image[x][y];                                          //Get the pixels color
                int brightness = (int) ColorMath.getBrightness(c);              //Get the pixels brightness (avg of the three components)
                if (brightness > k) {                    //If brightness is greater than k
                    brightRegion[x][y] = c;              //Add it to the brightRegion
                } else {                        
                    brightRegion[x][y] = Color.BLACK;    //Else set it to black
                }
            }
        });
        return brightRegion;
    }
}
