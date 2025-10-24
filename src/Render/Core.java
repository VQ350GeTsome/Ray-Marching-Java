package Render;

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

    private static float eps = 0.0001f;
    public static float getEps() { return eps; }
    
    private static float cameraMoveGrain = 0.1f, cameraRotateGrain = 1.0f;
    public static float getCameraMoveGrain()    { return cameraMoveGrain; }
    public static float getCameraRotateGrain()  { return cameraRotateGrain; }
    
    private BufferedImage image;
    private int width = 250, height = width;
    
    private Scene scene;
    
    public Core() {
        imageSizer();
        scene = new Scene();
        scene.setSceneLighting(new vec3(0.25f, 0.33f, -1.0f));
        scene.setAmbientLighting(0.05f);
        scene.addSDF(new Sphere(    new vec3( 0.0f , 0.0f,  1.0f ), 1.0f, Color.CYAN));
        scene.addSDF(new Cube(      new vec3( 0.0f , 0.0f, -1.0f ), 1.0f, Color.GRAY));
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
    /**
     * Calls the scenes raymarcher which will march
     * the entire screen and return a 2D array of
     * Colors to the scene ... and then return that
     * to this to then finally write it to the
     * BufferedImage image ... and repaint it.
     */
    private void renderScene() {
        Color[][] screen = scene.renderScene(width, height);
        for (int x = 0; width > x; x++) for (int y = 0; height > y; y++)
            image.setRGB(x, y, screen[x][y].getRGB());
        repaint();
    }
    
    public void moveSceneCamera(vec3 m)                 { scene.moveCamera(m); }
    public void rotateSceneCamera(float y, float p)     { scene.rotateCamera(y, p); }
    public void zoomSceneCamera(float z)                { scene.zoomCamera(z); }
    public vec3[] getSceneCameraOrien()                 { return scene.getCameraOrien(); }
    
    public void imageSizer() { image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB); }
    @Override public void paintComponent(Graphics g) { super.paintComponent(g); g.drawImage(image, 0, 0, getWidth(), getHeight(), null); }
    
}
