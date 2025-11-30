package Util;

import java.util.stream.IntStream;

public class PostProcessor {
    
    private static int width, height,
                       centerW, centerH;
        
    private static vec3 BLACK      = new vec3();
    
    /**
     * Sets the width and height
     * 
     * @param w The width
     * @param h The height
     */
    public static void setWidthHeight(int w, int h) { 
        width = w; height = h; 
        centerW = w / 2; centerH = h / 2;
    }
    
    private static float bloomSens = 0, bloomRadius = 0;
    
    /**
     * Isolates the brightest areas, blurs them, then adds them back on to image
     * 
     * @param bg                The background color ( what to ignore ).
     * @param image             The image we're adding bloom to.
     * @return                  The final image.
     */
    public static vec3[][] addBloom(vec3[][] image, vec3 bg) {
        
        vec3[][] brightRegions = isolateBright(image, bloomSens);               
        int blurRadiusPX = (int) (bloomRadius * height);
        vec3[][] blurredBright = boxBlur(bg, brightRegions, blurRadiusPX, true);
       
        vec3[][] finalImage = new vec3[width][height];                                //Initalize a new screen that we'll end up returning               
        
        for (int x = 0; width > x; x++) for (int y = 0; height > y; y++)              //Loop the image & add the isolated bright regions
            finalImage[x][y] = image[x][y].add(blurredBright[x][y]);                  //that we blurred back to the original image
        
        return finalImage;
    }
    /**
     * Blurs an image using either a circular area or a box area.
     * 
     * @param background    The color not to blur (bg color)
     * @param image         The image to use
     * @param radius        The size of the blur
     * @param circle        If we are using the circle average, else the square average
     * @return              The final blurred image
     */
    private static vec3[][] boxBlur(vec3 background, vec3[][] image, int radius, boolean circle) {
        vec3[][] blurredImage = new vec3[width][height];
        
        IntStream.range(0, width).parallel().forEach(x -> { //Parellelize each x row and call the 
            for (int y = 0; height > y; y++) {              //for loop for each y column in the image
                blurredImage[x][y] = average(background, image, x, y, radius, circle);
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
    private static vec3[][] isolateBright(vec3[][] image, float k) {
        vec3[][] brightRegion = new vec3[width][height];
        IntStream.range(0, width).parallel().forEach(x -> {                     //Parellelize each x row and call the for loop for each y column
            for (int y = 0; height > y; y++) {
                vec3 c = image[x][y];                                          //Get the pixels color
                int brightness = (int) c.average();              //Get the pixels brightness (avg of the three components)
                if (brightness > k) {                    //If brightness is greater than k
                    brightRegion[x][y] = c;              //Add it to the brightRegion
                } else {                        
                    brightRegion[x][y] = BLACK;    //Else set it to black
                }
            }
        });
        return brightRegion;
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
    private static vec3 average(vec3 background, vec3[][] image, int x, int y, int radius, boolean circle) {
        float r = 0, g = 0, b = 0, c = 0,       //Start rgb at 0 and the count (c)
                radiSqrd = radius * radius;     //Radius sqaured
                
        for (int dx = -radius; radius >= dx; dx++) {    //dx is the x location relative to the center of the point we are getting the average
            
            int nx = x + dx;                            //nx is the location on the screen
            if (nx < 0 || nx >= width) continue;        //Continue if the radius' coordinate is not valid
            int dxSqrd = dx * dx;                       //dx squared
            
            for (int dy = -radius; radius >= dy; dy++) {    //dy is the y location relative to the pixel we are averaging around
                
                int ny = y + dy;                            //ny is the location on screen
                if ((ny < 0 || ny >= height)) continue;     //Check if ny is a valid pixel
                
                if (circle && (dxSqrd + dy * dy > radiSqrd)) continue;  //Check if the (dx, dy) is within a circle, if it's not continue

                vec3 color = image[nx][ny];
                //If the color isn't the bg color we will add it 
                //to the total to be averaged, else it's not added by c
                //is still incremented.
                if (!background.equals(color)) {
                    r += color.x;
                    g += color.y;
                    b += color.z;
                }
                c++;
            }
        }
        if (c == 0) return BLACK;
        else return new vec3((float) r / c, (float) g / c, (float) b / c);
    }
    
    public static vec3[][] addCrossHair(vec3[][] image, vec3 color, float size) {
        //Cross hair pixel size
        int w = (int) (width * size), h = (int) (height * size);
        
        for (int x = -w; x <= w; x++) image[centerW + x][centerH] = color;
        for (int y = -h; y <= h; y++) image[centerW][centerH + y] = color;
 
        return image;
    }
    
    public static java.awt.Color[][] convertToColor(vec3[][] image) {
        java.awt.Color[][] color = new java.awt.Color[width][height];
        for (int x = 0; width > x; x++) for (int y = 0; height > y; y++) 
            color[x][y] = image[x][y].toAwtColor();
        return color;
    }
    
    public static String packagePostProcessor() { return bloomSens + "," + bloomRadius + ",\n"; }
    public static String[] getBloomSettings() { return new String[] { ""+bloomSens, ""+bloomRadius }; }
    public static void setBloomSettings(String[] settings) {
        try {
            bloomSens   = Float.parseFloat(settings[0].trim());
            bloomRadius = (int) (Float.parseFloat(settings[1].trim()) * height);
            
        } catch (NumberFormatException e) {
            System.err.println("Error Parsing New Bloom Settings ...");
            System.err.println(e.getMessage());
        }
    }
}
