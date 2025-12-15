package Render;

import Util.HitInfo;
import Vectors.vec3;
import Util.PostProcessor;
import File.FileManager;
import SDFs.SDF;

public class Scene {
    
    private final Light       light;
    private final SDFManager  sdfManager;
    private final Camera      camera 
		= new Camera(
	  new vec3(-4.0f,  3.0f,  1.0f),        //Position in space
	  new vec3( 0.0f,  0.0f, -0.8f),        //Where it is pointing
          new vec3( 0.0f,  0.0f,  1.0f),        //Up vector
          90                                    //Field of view
	  );
    private final RayMarcher  raymchr;
    
    private int w, h; //Width & height
    
    public Scene(int width, int height) {
        light = new Light();                                    //Instatiate a new light
        sdfManager = new SDFManager();                          //Instatiate a new SDFManager
        raymchr = new RayMarcher(camera, light, sdfManager); //Instatiate a new RayMarcher        
        FileManager.setScene(this);     //Send this scene to the file manager
        w = width; h = height;
    }
    
    public void setWidthHeight(int w, int h) {
        this.w = w;
        this.h = h;
    }
    
    public void collectGarbageSDFs() { sdfManager.garbageCollector(); }
    
    //Light object abstraction
    public void setSceneLighting(vec3 l)        { light.setSceneLighting(l); }
    public vec3 getSceneLighting()              { return light.getSceneLighting(); }
    public void setLightColor(vec3 l)           { light.setLightColor(l); }
    public vec3 getLightColor()                 { return light.getLightColor(); }
    public void  setAmbientLighting(float k)    { light.setAmbientLight(k); }
    public float getAmbientLighting()           { return light.getAmbientLight(); }  
    
    //SDF Manager abstraction
    public boolean addSDF(SDF sdf)      { return sdfManager.addSDF(sdf); }
    public boolean removeSDF(SDF sdf)   { return sdfManager.removeSDF(sdf); }
    public boolean setSDF(SDF s, SDF n) { return sdfManager.setSDF(s, n); }
    
    //Camera abstraction
    public void moveCamera(vec3 m)              { camera.move(m); }
    public void rotateCamera(float y, float p)  { camera.rotate(y, p); }
    public void zoomCamera(float z)             { camera.zoom(z); }
    
    public vec3 getCameraPos()                  { return camera.getOrientation()[0]; }
    public vec3[] getCameraOrien()              { return camera.getOrientation(); }
    
    public void cameraObj(boolean add) {
        if (add) sdfManager.addSDF(camera);
        else sdfManager.removeSDF(camera);
    }
    
    //Ray marcher abstraction
    public HitInfo marchRay(int x, int y, int w, int h) { 
        float nx = (x + 0.5f) / (float) w;
        float ny = (y + 0.5f) / (float) h;
        vec3 pos = camera.getOrientation()[3];
        vec3 dir = camera.getRayDirection(nx, ny, w / (float) h); 
        
        return raymchr.marchRaySkipCam(pos, dir);
    }
    public vec3[][] renderScene() { return raymchr.marchScreen(w, h); }
    
    public vec3 getBackground()         { return raymchr.background; }
    public void setBackground(vec3 bg)  { raymchr.background = bg; }
    public vec3 getSecondaryBG()        { return raymchr.bgSecondary; }
    public void setSecondaryBG(vec3 bg) { raymchr.bgSecondary = bg; }
    
    public float getShadowAmount()        { return raymchr.shadowAmount; }
    public void  setShadowAmount(float f) { raymchr.shadowAmount = f; }
    
    public void setMarchParams(String[] params) { raymchr.setMarchParams(params); }
    public String[] getMarchParams()            { return raymchr.getMarchParams(); }
    
    public void setSeeLight(boolean b)    { raymchr.seeLight = b; }
    public void setUseGradient(boolean b) { raymchr.gradient = b; }
    public void setGradUseZ(boolean b)    { raymchr.gradUseZ = b; }
    
    public float getSkyboxLightAmount()        { return raymchr.skyboxLightAmount; }
    public void  setSkyboxLightAmount(float f) { raymchr.skyboxLightAmount = f; }
    
    //Packager & unpackager
    public String packageScene() {
        return  camera.packageCamera() +
                raymchr.packRayMarcher() +
                light.packLight() +
                PostProcessor.packagePostProcessor() +
                sdfManager.packSDFs();
                
    }
    public void unpackageScene(String pack) {
        String[] parts = pack.split(",");       //Split pack by delimiter
        
        //Camera parts 0 - 6
        String[] cameraPack = new String[]      //Get the camera parts
            { parts[0], parts[1], parts[2], parts[3], 
              parts[4], parts[5], parts[6], };
        camera.unpackageCamera(cameraPack);     //Update the camera 
        
        //RayMarcher parts 7 - 18
        String[] rayMarcherPack = new String[]          //Get all the raymarcher parts
        { 
            parts[7],  parts[8],  parts[9],  parts[10], 
            parts[11], parts[12], parts[13], parts[14], 
            parts[15], parts[16], parts[17], parts[18]
        };
        raymchr.unpackRayMarcher(rayMarcherPack);    //Update the raymarcher
        
        //Light parts 19 - 21
        String[] lightPack = new String[] { parts[19], parts[20], parts[21] }; //Get the light parts
        light.unpackLight(lightPack);                               //Update light
        
        //Post proccesor parts 22 - 23
        PostProcessor.setBloomSettings(new String[] { parts[22], parts[23] } );
        
        //Sdf pars 24 -> rest
        String[] sdfs = java.util.Arrays.copyOfRange(parts, 24, parts.length);
        sdfManager.unpackSDFs(sdfs);
    }
}
