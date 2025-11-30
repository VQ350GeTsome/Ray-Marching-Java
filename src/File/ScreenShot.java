package File;

import java.awt.image.BufferedImage;

public class ScreenShot {
    
    private static String dir = "";
    private static String finalDir = "";
    
    private static int frame = 0;
    
    public static void getCurrentDirectory(){
        dir = System.getProperty("user.dir");
        int i = dir.length();
        String slashCheck = "";
        int slashIndex = 0;
        while (i > 0){
            slashCheck = dir.substring(i - 1, i);
            if (slashCheck.equals("\\")){
               slashIndex = i;
               i = 0;
               finalDir = dir.substring(0, slashIndex);
            }
            i--;
        } 
    }
    
    public static void screenshot(BufferedImage image){
        if (dir.isBlank() || finalDir.isBlank()) getCurrentDirectory(); //If the directories have yet to be found find it
        
        try {
            System.out.println("\nScreen shotting ...");
            BufferedImage bi = image;
            java.io.File outputfile = new java.io.File("ScreenShot.png");
            javax.imageio.ImageIO.write(bi, "png", outputfile);
            String rename = dir + "\\" + "images" + "\\" + frame + Math.round(Math.random() * 10000) + ".png";
            outputfile.renameTo(new java.io.File(rename));
            frame++;
            System.out.println("\nScreen shot successful !!!\nSaved to: " + rename + "\n");
        } catch (java.io.IOException e) { 
            System.err.println(dir + "is broken / not a valid path."); 
        } catch (Exception e) {
            System.err.println("Error in exporting image ...");
            System.err.println(e.getMessage());
        }
    }
    public static void screenshot(java.awt.Color[][] screen) { screenshot(TwoDColorToBufferedImage(screen)); }
    
    private static BufferedImage TwoDColorToBufferedImage(java.awt.Color[][] colors) {
        int width = colors.length, height = colors[0].length;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        for (int x = 0; width > x; x++) for (int y = 0; height > y; y++) image.setRGB(x, y, colors[x][y].getRGB());
        return image;
    }
}
