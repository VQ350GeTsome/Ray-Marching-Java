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
    private int width = 200, height = width;    //screens dimensions
    
    public Scene scene;
    private Timer timer;
    
    public Core() {
        imageSizer();           //Size & initialize screen
        scene = new Scene(width, height);    //Initialize scene
        scene.setSceneLighting(new vec3(0.25f, 0.33f, -1.0f));  //Set the lighting
        scene.setAmbientLighting(0.05f);                        //Set the ambient lighting
        
        /* Add some SDFs */
        
        SDF sphere  = new Sphere(    new vec3( 0.0f , 0.0f,  1.0f ), 1.0f, new Material(new vec3(0,255,255)));
        SDF cube    = new Cube(      new vec3( 0.0f , 0.0f, -1.0f ), 1.0f, new Material(new vec3(128)));
        
        SDF blend = new BlendedSDF(sphere, cube, 1.0f);
        blend.setName("Blended Sphere & Cube");
        scene.addSDF(blend);
        
        SDF floor = new Plane(new vec3(0.0f, 0.0f, -4.0f), new vec3(0.0f, 0.0f, 1.0f), new Material(new vec3(64), 0.33f));
        floor.setName("Scene Floor");
        scene.addSDF(floor);
        
        SDF chainCube = new HollowChainCube(new vec3(0.0f, 0.0f, 0.0f), 1.0f, 1.0f, new Material(new vec3(255,0,0)));
        chainCube.setName("Chain Cube");
        //scene.addSDF(chainCube);
        
        SDF cube2 = new Cube(      new vec3( 0.0f , -4.0f, 2.0f ), 1.0f, new Material(new vec3(0,255,0), 0.80f));
        scene.addSDF(cube2);
        
        /* Finish adding SDFs */
        
        PostProcessor.initalize(width, height); //Initalize the post processor
    }

    public void setWait(int w) { timer.setDelay(w); }
    public int  getWait() { return timer.getDelay(); }
    
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
     * Calls the scenes ray marcher which will march
     * the entire screen and return a 2D array of
     * Colors to the scene ... and then return that
     * to this to then finally write it to the
     * image ... and repaint it.
     */
    private void renderScene() {
        vec3[][] vec3Image = scene.renderScene();
        Color[][] colorImage = null;
              
        //Adds bloom to the image using the current settings
        if (bloom)
            vec3Image = PostProcessor.addBloom(scene.getBackground(), vec3Image, bloomAmount, bloomRadius, circleBlur);
        colorImage = PostProcessor.convertToColor(vec3Image);
        
        
        for (int x = 0; width > x; x++) for (int y = 0; height > y; y++)    //Loop screen
            screen.setRGB(x, y, colorImage[x][y].getRGB());  //Process the image to the screen
        repaint();  //Update screen
    }
        
    public void imageSizer() { screen = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB); }
    @Override public void paintComponent(Graphics g) { super.paintComponent(g); g.drawImage(screen, 0, 0, getWidth(), getHeight(), null); }
    public void screenShot() {
        File.ScreenShot.exportCurrentImage(screen);
    }
    
}
