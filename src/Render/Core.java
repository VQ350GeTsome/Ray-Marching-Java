package Render;

import SDFs.Primitives.*;
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
    
    private BufferedImage screen;               //What we will use as the screen
    private int width = 200, height = width;    //screens dimensions
    
    private Scene scene;
    
    public Core() {
        imageSizer();           //Size & initialize screen
        scene = new Scene();    //Initialize scene
        scene.setSceneLighting(new vec3(0.25f, 0.33f, -1.0f));  //Set the lighting
        scene.setAmbientLighting(0.05f);                        //Set the ambient lighting
        
        /* Add some SDFs */
        
        SDF sphere  = new Sphere(    new vec3( 0.0f , 0.0f,  1.0f ), 1.0f, Color.CYAN);
        SDF cube    = new Cube(      new vec3( 0.0f , 0.0f, -1.0f ), 1.0f, Color.GRAY);
        
        SDF blend = new BlendedSDF(sphere, cube, 0.25f);
        
        scene.addSDF(blend);
        
        SDF floor = new Plane(new vec3(0.0f, 0.0f, -4.0f), new vec3(0.0f, 0.0f, 1.0f), Color.DARK_GRAY);
        
        scene.addSDF(floor);
        
        /* Finish adding SDFs */
        
        PostProcessor.initalize(width, height); //Initalize the post processor
    }
    /**
     * Main loop ... we are constantly updating
     * what is on screen.
     */
    public void mainLoop(){
        Timer timer = new Timer(33, new ActionListener() {
            public void actionPerformed(ActionEvent e){
                renderScene();
            }
        });
        timer.start(); /* Start timer */
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
        Color[][] image = scene.renderScene(width, height);
              
        //Adds bloom to the image using the current settings
        image = PostProcessor.addBloom(scene.getBackground(), image, bloomAmount, bloomRadius, circleBlur);
        
        for (int x = 0; width > x; x++) for (int y = 0; height > y; y++)    //Loop screen
            screen.setRGB(x, y, image[x][y].getRGB());  //Process the image to the screen
        repaint();  //Update screen
    }
    
    public void moveSceneCamera(vec3 m)                 { scene.moveCamera(m); }
    public void rotateSceneCamera(float y, float p)     { scene.rotateCamera(y, p); }
    public void zoomSceneCamera(float z)                { scene.zoomCamera(z); }
    public vec3[] getSceneCameraOrien()                 { return scene.getCameraOrien(); }
    
    public void imageSizer() { screen = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB); }
    @Override public void paintComponent(Graphics g) { super.paintComponent(g); g.drawImage(screen, 0, 0, getWidth(), getHeight(), null); }
    
}
