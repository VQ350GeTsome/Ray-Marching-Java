package Render;

import SDFs.Primitives.*;
import SDFs.Special.*;
import SDFs.*;
import Utility.*;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Core extends JPanel {

    private static float eps = 1e-4f;
    public static float getEps() { return eps; }
    
    public static float cameraMoveGrain = 0.5f, cameraRotateGrain = 5.0f;
    
    public boolean bloom = true;
    
    private BufferedImage screen;               //What we will use as the screen
    private int width = 150, height = width;    //screens dimensions
    
    public Scene scene;
    public Timer timer;
    
    public Core() {
        imageSizer();                           //Size & initialize screen
        scene = new Scene(width, height);       //Initialize scene
        
        //Default lighting
        scene.setSceneLighting(new vec3(0.25f, 0.33f, -1.0f));  //Set the lighting
        scene.setAmbientLighting(0.05f);                        //Set the ambient lighting 
        
        //Default SDFs
        addDefaultSDFs();
        
        PostProcessor.setWidthHeight(width, height); //Initalize the post processor
    }
    
    /**
     * Main loop ... we are constantly updating
     * what is on screen.
     */
    public void mainLoop(){
        timer = 
            new Timer(42, //About 24 fps
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) { 
                        run(); 
                    }
                }
            );
        timer.start(); /* Start timer */
    }
    public void refresh() { run(); }
    
    private void run() {
        scene.collectGarbageSDFs(); //Deletes any SDFs that need to be 
        renderScene();
    }

    private static boolean crosshair = false;
    
    public String[] getResoultion() { return new String[] { ""+width, ""+height }; }
    
    /**
     * Calls the scenes ray marcher which will march
     * the entire screen and return a 2D array of
     * Colors to the scene ... and then return that
     * to this to then finally write it to the
     * image ... and repaint it.
     */
    private void renderScene() {        
        vec3[][] vec3Image = scene.renderScene();
              
        //Adds bloom to the image using the current settings
        if (bloom)
            vec3Image = PostProcessor.addBloom(vec3Image, scene.getBackground());
        if (crosshair)
            vec3Image = PostProcessor.addCrossHair(vec3Image, new vec3(255.0f), 0.005f);
        
        Color[][] colorImage = PostProcessor.convertToColor(vec3Image);
        
        for (int x = 0; width > x; x++) for (int y = 0; height > y; y++)    //Loop screen
            screen.setRGB(x, y, colorImage[x][y].getRGB());  //Process the image to the screen
        repaint();  //Update screen
    }
    
    private void addDefaultSDFs() {
        Material sphereMat = new Material(new vec3(0, 255, 255));
        sphereMat.metalness = 0.33f;
        //sphereMat.reflectivity = 0.80f;
        SDF sphere  = new Sphere(new vec3(), 1.0f, sphereMat);
        SDF cube    = new Cube(new vec3(0.0f , 0.0f, -3.0f), 1.0f, new Material(new vec3(128)));

        SDF blend = new BlendedSDF(sphere, cube, 2.1f);
        blend.setName("Blended Sphere & Cube");
        //scene.addSDF(blend);
        
        
        Material floorMat = new Material(new vec3(100.0f));
        floorMat.reflectivity = 0.25f;
        SDF floor = new Plane(new vec3(0.0f, 0.0f, -4.0f), new vec3(0.0f, 0.0f, 1.0f), floorMat);
        floor.setName("Scene Floor");
        scene.addSDF(floor);
        
        SDF t = new Cylinder(new vec3(), 1.0f, 1.0f, Material.GLASS);
        scene.addSDF(t);
        
        SDF p = new Sphere(new vec3(0, 1, 3), 1.0f, new Material(Color.RED, Material.PLASTIC));
        scene.addSDF(p);
        
        SDF q = new Cube(new vec3(2, -1, 2), 1.0f, Material.GLASS);
        scene.addSDF(q);
        
        SDF z = new Sphere(new vec3(0, 0, -1), 0.333f, new Material(Color.GREEN, Material.PLASTIC));
    }
    
    /**
     * Updates all objects using the width & height, with the new width & height.
     * 
     * @param w The new width.
     * @param h The new height.
     */
    public void changeResolution(int w, int h) {
        width = w; height = h;
        imageSizer();
        scene.setWidthHeight(w, h);
        PostProcessor.setWidthHeight(w, h);
    }
        
    public void imageSizer() { screen = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB); }
    @Override public void paintComponent(Graphics g) { super.paintComponent(g); g.drawImage(screen, 0, 0, getWidth(), getHeight(), null); }
    
    private int hiResW = 2000, hiResH = 2000;
    public void changeF2Res(int w, int h) { hiResW = w; hiResH = h; }
    public String[] getF2Res() { return new String[] { ""+hiResW, ""+hiResH }; }
    public void screenShotHiRes() {
        timer.stop();
        
        //Saves the current width & height
        int pWidth = width, pHeight = height;
        
        changeResolution(hiResW, hiResH);
        
        vec3[][] vec3Image = scene.renderScene();
              
        if (bloom)
            vec3Image = PostProcessor.addBloom(vec3Image, scene.getBackground());
        Color[][] colorImage = PostProcessor.convertToColor(vec3Image);
        
        File.ScreenShot.exportImage(colorImage);
        
        changeResolution(pWidth, pHeight);
        
        timer.start();
    }
    
    public void screenShotAsIs() {
        File.ScreenShot.exportImage(screen);
    }
    
}
