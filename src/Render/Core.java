package Render;

import SDFs.Primitives.*;
import SDFs.Special.*;
import SDFs.Repeating.*;
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

    private static float eps = 0.000001f;
    public static float getEps() { return eps; }
    
    private static float cameraMoveGrain = 0.5f, cameraRotateGrain = 0.5f;
    public static float getCameraMoveGrain()    { return cameraMoveGrain; }
    public static void  setCameraMoveGrain(float f) { cameraMoveGrain = f; }
    public static float getCameraRotateGrain()  { return cameraRotateGrain; }
    public static void  setCameraRotateGrain(float f)  { cameraRotateGrain = f; }
    
    public boolean bloom = true;
    
    private BufferedImage screen;               //What we will use as the screen
    private int width = 200, height = width;    //screens dimensions
    
    public Scene scene;
    private Timer timer;
    
    public Core() {
        imageSizer();           //Size & initialize screen
        scene = new Scene(width, height);    //Initialize scene
        scene.setSceneLighting(new vec3(0.25f, 0.33f, -1.0f));  //Set the lighting
        scene.setAmbientLighting(0.05f);                        //Set the ambient lighting
        
        /* Add some SDFs */
        
        SDF sphere  = new Sphere(    new vec3( 0.0f , 0.0f,  1.0f ), 1.0f, Color.CYAN);
        SDF cube    = new Cube(      new vec3( 0.0f , 0.0f, -1.0f ), 1.0f, Color.GRAY);
        
        SDF blend = new BlendedSDF(sphere, cube, 0.25f);
        blend.setName("Blended Sphere & Cube");
        //scene.addSDF(blend);
        
        SDF floor = new Plane(new vec3(0.0f, 0.0f, -4.0f), new vec3(0.0f, 0.0f, 1.0f), Color.DARK_GRAY);
        floor.setName("Scene Floor");
        //scene.addSDF(floor);
        
        SDF chainCube = new HollowChainCube(new vec3(0.0f, 0.0f, 0.0f), 1.0f, 1.0f, Color.RED);
        //scene.addSDF(chainCube);
        
        SDF repeatSphere = new RepeatSphere(new vec3(0.0f), 1.0f, 10.0f, Color.RED);
        scene.addSDF(repeatSphere);
        
        /* Finish adding SDFs */
        
        PostProcessor.initalize(width, height); //Initalize the post processor
    }
    /**
     * Main loop ... we are constantly updating
     * what is on screen.
     */
    public void mainLoop(){
        int wait = 33;
        timer = 
            new Timer(wait, 
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) { 
                        run(); 
                    }
                }
            );
        timer.start(); /* Start timer */
    }
    public void startStopTimer() {
        if (timer.isRunning()) timer.stop();
        else timer.start();
    }
    public void refresh() { run(); }
    
    private void run() {
        scene.collectGarbageSDFs(); //Deletes any SDFs that need to be 
        renderScene();
    }
    
    //Postprocessing settings 
    private int bloomAmount     = 150,
                bloomRadius     =  25;
    private boolean circleBlur  = true;
    
    /**
     * Calls the scenes raymarcher which will march
     * the entire screen and return a 2D array of
     * Colors to the scene ... and then return that
     * to this to then finally write it to the
     * BufferedImage image ... and repaint it.
     */
    private void renderScene() {
        Color[][] image = scene.renderScene();
              
        //Adds bloom to the image using the current settings
        if (bloom) {
            PostProcessor.addBloom(scene.getBackground(), image, bloomAmount, bloomRadius, circleBlur);
        }
        
        for (int x = 0; width > x; x++) for (int y = 0; height > y; y++)    //Loop screen
            screen.setRGB(x, y, image[x][y].getRGB());  //Process the image to the screen
        repaint();  //Update screen
    }
        
    public void imageSizer() { screen = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB); }
    @Override public void paintComponent(Graphics g) { super.paintComponent(g); g.drawImage(screen, 0, 0, getWidth(), getHeight(), null); }
    
}
