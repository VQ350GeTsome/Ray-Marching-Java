package Render;

import File.FileManager;
import SDFs.SDF;
import Utility.*;
import java.util.Arrays;

public class Scene {
    
    private Light       light;
    private SDFManager  sdfManager;
    private Camera      camera 
		= new Camera(
	  new vec3(-4.0f,  3.0f,  1.0f),        //Position in space
	  new vec3( 0.0f,  0.0f, -0.8f),        //Where it is pointing
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
    
    public void setWidthHeight(int w, int h) {
        this.w = w;
        this.h = h;
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
    public void cameraObj(boolean add) {
        if (add) sdfManager.addSDF(camera);
        else sdfManager.removeSDF(camera);
    }
    
    public HitInfo marchRay(int x, int y, int w, int h) { 
        float nx = (x + 0.5f) / (float) w;
        float ny = (y + 0.5f) / (float) h;
        vec3 pos = camera.getOrientation()[3];
        vec3 dir = camera.getRayDirection(nx, ny, w / (float) h); 
        
        return rayMarcher.marchRaySkipCam(pos, dir);
    }
    public vec3[][] renderScene() { return rayMarcher.marchScreen(w, h); }
    public vec3 getBackground() { return rayMarcher.getBackground(); }
    public vec3 getSecondaryBG() { return rayMarcher.getSecondaryBG(); }
    public void setBackground(vec3 bg) { rayMarcher.setBackground(bg); }
    public void setSecondaryBG(vec3 bg) { rayMarcher.setSecondaryBG(bg); }
    public float getShadowAmount() { return rayMarcher.getShadowAmount(); }
    public void setShadowAmount(float f) { rayMarcher.setShadowAmount(f); }
    public void setMarchParams(String[] params) { rayMarcher.setMarchParams(params); }
    public String[] getMarchParams() { return rayMarcher.getMarchParams(); }
    public void setSeeLight(boolean b) { rayMarcher.setSeeLight(b); }
    public void setUseGradient(boolean b) { rayMarcher.setUseGradient(b); }
    public void setGradUseZ(boolean b) { rayMarcher.setGradUseZ(b); }
    
    public String packageScene() {
        return  camera.packageCamera() +
                rayMarcher.packRayMarcher() +
                light.packLight() +
                Core.packagePostProcessor() +
                sdfManager.packSDFs();
                
    }
    public void unpackageScene(String pack) {
        String[] parts = pack.split(",");       //Split pack by delimiter
        
        //Camera parts 0 - 6
        String[] cameraPack = new String[]      //Get the camera parts
            { parts[0], parts[1], parts[2], parts[3], 
              parts[4], parts[5], parts[6], };
        camera.unpackageCamera(cameraPack);     //Update the camera 
        
        //RayMarcher parts 7 - 13
        String[] rayMarcherPack = new String[]          //Get all the raymarcher parts
            { parts[7],  parts[8],  parts[9], parts[10], 
              parts[11], parts[12], parts[13] };
        rayMarcher.unpackRayMarcher(rayMarcherPack);    //Update the raymarcher
        
        //Light parts 14 - 15
        String[] lightPack = new String[] { parts[14], parts[15] }; //Get the light parts
        light.unpackLight(lightPack);                               //Update light
        
        //Post proccesor parts 16 - 17
        Core.setBloomSettings(new String[] { parts[16], parts[17] } );
        
        //Sdf pars 18 -> rest
        String[] sdfs = Arrays.copyOfRange(parts, 18, parts.length);
        sdfManager.unpackSDFs(sdfs);
    }
}
