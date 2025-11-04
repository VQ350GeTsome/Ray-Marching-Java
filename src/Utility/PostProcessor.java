package Utility;

import java.awt.Color;
import java.util.stream.IntStream;

public class PostProcessor {
    
    private static int width, height;
    
    /**
     * Sets the width and height
     * @param w The width
     * @param h The height
     */
    public static void initalize(int w, int h) { width = w; height = h; }
    
    /**
     * Isolates the brightest areas, blurs them, then adds them back on to image
     * @param background        The background color (what to ignore)
     * @param image             The base image
     * @param bloomSensitivity  How bright a color must be for it to be isolated
     * @param blurRadius        How much to blur by
     * @return                  The final image
     */
    public static Color[][] addBloom(Color background, Color[][] image, int bloomSensitivity, int blurRadius, boolean circle) {
        
        Color[][] brightRegions = isolateBright(image, bloomSensitivity);               //Isolate the bright regions using the sensitivity
        
        Color[][] blurredBright = blur(background, brightRegions, blurRadius, circle);  //Blur those bright regions using the settings
        
        Color[][] finalImage = new Color[width][height];                            
        
        for (int x = 0; width > x; x++) for (int y = 0; height > y; y++)                //Loop the image & add the isolated bright regions
            finalImage[x][y] = ColorMath.add(image[x][y], blurredBright[x][y]);         //that we blurred back to the original image
        
        return finalImage;
    }
    /**
     * Blurs an image using either a circle average or box average
     * @param background    The color not to blur (background color)
     * @param image         The image to use
     * @param radius        The size of the blur
     * @param circle        If we are using the circle average, else the square average
     * @return              The final blurred image
     */
    private static Color[][] blur(Color background, Color[][] image, int radius, boolean circle) {
        Color[][] blurredImage = new Color[width][height];
        
        IntStream.range(0, width).parallel().forEach(x -> { //Parellelize each x row and call the 
            for (int y = 0; height > y; y++) {              //for loop for each y column in the image
                blurredImage[x][y] = 
                        (circle) ? circleAverage(background, image, x, y, radius) 
                                 : squareAverage(background, image, x, y, radius);
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
    /**
     * Gets the average of a color at (x, y) using a box area to sample
     * @param background    The color to ignore
     * @param image         The image we are using to sample
     * @param x             The x value we are centered on
     * @param y             The y value we are centered on
     * @param radius        How large of an area we are looking at
     * @return              The average color around (including) (x, y)
     */
    private static Color squareAverage(Color background, Color[][] image, int x, int y, int radius) {
        int r = 0, g = 0, b = 0, c = 0;     //Start rgb at 0 and the count (c)
        for (int dx = -radius; dx <= radius; dx++) {
            
            int nx = x + dx;
            
            for (int dy = -radius; dy <= radius; dy++) {     //Loop around the radius
                
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
        }
        if (c == 0) return Color.BLACK;
        else return new Color(r / c, g / c, b / c);
    }
    /**
     * Gets the average of a color at (x, y) using a circular area to sample
     * @param background    The color to ignore
     * @param image         The image we are using to sample
     * @param x             The x value we are centered on
     * @param y             The y value we are centered on
     * @param radius        How large of a circle we are looking at
     * @return              The average color around (including) (x, y)
     */
    private static Color circleAverage(Color background, Color[][] image, int x, int y, int radius) {
        int r = 0, g = 0, b = 0, c = 0,     //Start rgb at 0 and the count (c)
                radiSqrd = radius * radius; //Radius sqaured
                
        for (int dx = -radius; dx <= radius; dx++) {    //dx is the x location relative to the center of the point we are getting the average
            
            int nx = x + dx;                            //nx is the location on the screen
            if (nx < 0 || nx >= width) continue;        //Continue if the radius' coordinate is not valid
            int dxSqrd = dx * dx;                       //dx squared
            
            for (int dy = -radius; dy <= radius; dy++) {    //dy is the y location relative to the pixel we are averaging around
                
                if (dxSqrd + dy * dy > radiSqrd) continue;  //Check if the (dx, dy) is within a circle, if it's not continue
                int ny = y + dy;                            //ny is the location on screen
                if (ny < 0 || ny >= height) continue;       //Check if ny is a valid pixel

                Color color = image[nx][ny];
                if (background.equals(color)) color = Color.BLACK;
                r += color.getRed();
                g += color.getGreen();
                b += color.getBlue();
                c++;
            }
        }
        if (c == 0) return Color.BLACK;
        else return new Color(r / c, g / c, b / c);
    }
}
