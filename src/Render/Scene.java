package Render;

import File.FileManager;
import SDFs.SDF;
import Utility.*;
import java.awt.Color;
import java.util.Arrays;
import javax.swing.Timer;

public class Scene {
    
    private Light       light;
    private SDFManager  sdfManager;
    private Camera      camera 
		= new Camera(
	  new vec3(-3.0f,  1.0f,  1.0f),        //Position in space
	  new vec3( 0.0f,  0.0f,  0.0f),        //Where it is pointing
          new vec3( 0.0f,  0.0f,  1.0f),        //Up vector
          90                                    //Field of view
	  );
    private RayMarcher  rayMarcher;
    
    private int w, h;
    
    public Scene(int width, int height) {
        light = new Light();                                    //Instatiate a new light
        sdfManager = new SDFManager();                          //Instatiate a new SDFManager
        rayMarcher = new RayMarcher(camera, light, sdfManager); //Instatiate a new RayMarcher        
        FileManager.setScene(this);     //Send this scene to the file manager
        w = width; h = height;
    }
    
    public void collectGarbageSDFs() { sdfManager.gc(); }
    
    public void setSceneLighting(vec3 l)        { light.setSceneLighting(l); }
    public vec3 getSceneLighting()              { return light.getSceneLighting(); }
    public void setAmbientLighting(float k)     { light.setAmbientLight(k); }
    public float getAmbientLighting()           { return light.getAmbeintLight(); }
    
    public boolean addSDF(SDF sdf)      { return sdfManager.addSDF(sdf); }
    public boolean removeSDF(SDF sdf)   { return sdfManager.removeSDF(sdf); }
    public boolean setSDF(SDF s, SDF n) { return sdfManager.setSDF(s, n); }
    
    public void moveCamera(vec3 m)              { camera.move(m); }
    public vec3 getCameraPos()                  { return camera.getOrientation()[0]; }
    public void rotateCamera(float y, float p)  { camera.rotate(y, p); }
    public void zoomCamera(float z)             { camera.zoom(z); }
    public vec3[] getCameraOrien()              { return camera.getOrientation(); }
    
    public HitInfo marchRay(int x, int y, int w, int h) { return rayMarcher.marchRay(x, y, w, h); }
    public vec3[][] renderScene() { return rayMarcher.marchScreen(w, h); }
    public vec3 getBackground() { return rayMarcher.getBackground(); }
    public String packageScene() {
        return  camera.packageCamera() +
                rayMarcher.packRayMarcher() +
                light.packLight() +
                sdfManager.packSDFs();
    }
    public void unpackageScene(String pack) {
        String[] parts = pack.split(",");       //Split pack by delimiter
        
        //Camera parts 0 - 6
        String[] cameraPack = new String[]      //Get the camera parts
            { parts[0], parts[1], parts[2], parts[3], 
              parts[4], parts[5], parts[6], };
        camera.unpackageCamera(cameraPack);     //Update the camera 
        
        //RayMarcher parts 7 - 11
        String[] rayMarcherPack = new String[]          //Get all the raymarcher parts
            { parts[7] , parts[8] , parts[9] , parts[10], 
              parts[11] };
        rayMarcher.unpackRayMarcher(rayMarcherPack);    //Update the raymarcher
        
        //Light parts 12 - 13
        String[] lightPack = new String[] { parts[12], parts[13] }; //Get the light parts
        light.unpackLight(lightPack);                               //Update light
        
        String[] sdfs = Arrays.copyOfRange(parts, 14, parts.length);
        sdfManager.unpackSDFs(sdfs);
    }
}
