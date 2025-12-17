package Render;

/**
 * Scene class. 
 * 
 * @author Harrison Davis
 */
public final class Scene {
    
    // Scene members
    private final Light       light;
    private final SDFManager  sdfManager;
    private final Camera      camera 
		= new Camera(
	  new Vectors.vec3(-4.0f,  3.0f,  1.0f),        // Position in space
	  new Vectors.vec3( 0.0f,  0.0f, -0.8f),        // Where it is pointing
          new Vectors.vec3( 0.0f,  0.0f,  1.0f),        // Up vector
          90                                            // Field of view
	  );
    private final RayMarcher  raymchr;
    
    // Width & height
    private int w, h; 
    
    public Scene(int width, int height) {
        // Instantiate scene members.
        light = new Light();                                  
        sdfManager = new SDFManager();                          
        raymchr = new RayMarcher(camera, light, sdfManager);    
        
        // Send this scene to the file manager.
        File.FileManager.setScene(this);    
        
        // Save width and height.
        w = width; h = height;
    }
    
    public void setWidthHeight(int w, int h) { this.w = w; this.h = h; }
    
    public void collectGarbageSDFs() { sdfManager.garbageCollector(); }
    
    //<editor-fold defaultstate="collapsed" desc=" Light Abstraction ">
    public void setSceneLighting(Vectors.vec3 l) { light.setSceneLighting(l); }
    public Vectors.vec3 getSceneLighting() { return light.getSceneLighting(); }
    public void setLightColor(Vectors.vec3 l) { light.setLightColor(l); }
    public Vectors.vec3 getLightColor() { return light.getLightColor(); }
    public void  setAmbientLighting(float k) { light.setAmbientLight(k); }
    public float getAmbientLighting() { return light.getAmbientLight(); }  
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc=" SDF Manager Abstraction ">
    public boolean addSDF(SDFs.SDF sdf) { return sdfManager.addSDF(sdf); }
    public boolean removeSDF(SDFs.SDF sdf) { return sdfManager.removeSDF(sdf); }
    public boolean setSDF(SDFs.SDF s, SDFs.SDF n) { return sdfManager.setSDF(s, n); }
    public String[] getSDFNames() { return sdfManager.getNames(); }
    public SDFs.SDF getSDF(int i) { return sdfManager.getSDF(i); }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc=" Camera Abstraction ">
    public void moveCamera(Vectors.vec3 m) { camera.move(m); }
    public void rotateCamera(float y, float p)  { camera.rotate(y, p); }
    public void zoomCamera(float z) { camera.zoom(z); }
    
    public Vectors.vec3 getCameraPos() { return camera.getPosition(); }
    public Vectors.vec3[] getCameraOrien() { return camera.getOrientation(); }
    
    public float getCameraMovementSensitivity() { return this.camera.getMovementSensitivity(); }
    public float getCameraRotationSensitivity() { return this.camera.getRotationSensitivity(); }
    
    public void setCameraMovementSensitivity(float f) { this.camera.setMovementSensitivity(f); }
    public void setCameraRotationSensitivity(float f) { this.camera.setRotationSensitivity(f); }
    
    public void cameraObj(boolean add) {
        if (add) sdfManager.addSDF(camera);
        else sdfManager.removeSDF(camera);
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc=" Ray Marcher Abstraction ">
    public Util.HitInfo marchRay(int x, int y, int w, int h) { 
        float nx = (x + 0.5f) / (float) w;
        float ny = (y + 0.5f) / (float) h;
        Vectors.vec3 pos = camera.getPosition();
        Vectors.vec3 dir = camera.getRayDirection(nx, ny, w / (float) h); 
        
        return raymchr.marchRaySkipCam(pos, dir);
    }
    public Vectors.vec3[][] renderScene() { return raymchr.marchScreen(w, h); }
    
    public Vectors.vec3 getBackground() { return raymchr.background; }
    public void setBackground(Vectors.vec3 bg)  { raymchr.background = bg; }
    public Vectors.vec3 getSecondaryBG() { return raymchr.bgSecondary; }
    public void setSecondaryBG(Vectors.vec3 bg) { raymchr.bgSecondary = bg; }
    
    public float getShadowAmount() { return raymchr.shadowAmount; }
    public void  setShadowAmount(float f) { raymchr.shadowAmount = f; }
    
    public void setMarchParams(String[] params) { raymchr.setMarchParams(params); }
    public String[] getMarchParams() { return raymchr.getMarchParams(); }
    
    public void setSeeLight(boolean b) { raymchr.seeLight = b; }
    public void setUseGradient(boolean b) { raymchr.gradient = b; }
    public void setGradUseZ(boolean b) { raymchr.gradUseZ = b; }
    
    public float getSkyboxLightAmount() { return raymchr.skyboxLightAmount; }
    public void  setSkyboxLightAmount(float f) { raymchr.skyboxLightAmount = f; }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc=" Package / Unpackage ">
    public String packageScene() {
        return  camera.packageCamera() +
                raymchr.packRayMarcher() +
                light.packLight() +
                Util.PostProcessor.packagePostProcessor() +
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
        Util.PostProcessor.setBloomSettings(new String[] { parts[22], parts[23] } );
        
        //Sdf pars 24 -> rest
        String[] sdfs = java.util.Arrays.copyOfRange(parts, 24, parts.length);
        sdfManager.unpackSDFs(sdfs);
    }
    //</editor-fold>
}
