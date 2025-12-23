package Render;

/**
 * Core class, extends {@link java.swing.JPanel}. 
 * 
 * This class stores
 * 
 * 
 * @author Harrison Davis
 */
public final class Core extends javax.swing.JPanel {

    // Epsilon and our worlds up vector ( z-up world ) .
    public static final float EPS = 1e-4f;
    public static final Vectors.vec3 WORLDUP = new Vectors.vec3(0.0f, 0.0f, 1.0f);

    // Some post processor settings.
    public boolean bloom = true;
    private static boolean crosshair = false;
    
    // Screen dimensions and the screen itself.
    private int width = 125, height = width;  
    private java.awt.image.BufferedImage screen;          

    // Our scene and timer.
    public Scene scene;
    public javax.swing.Timer timer;
    
    public Core() {
        // Initalize screen / image and the scene.
        this.imageSizer();                          
        this.scene = new Scene(width, height);
        
        // Default lighting
        this.scene.setSceneLighting(new Vectors.vec3(0.25f, 0.33f, -1.0f));
        this.scene.setAmbientLighting(0.05f);                      
        
        // Random SDFs or test SDFs
        //this.addRandomSDFs(5);
        this.addTestingSDFs();
        
        // Initalize the post processor
        Util.PostProcessor.setWidthHeight(width, height); 
    }
    
    /**
     * Main loop ... we are constantly updating
     * what is on screen.
     */
    public void mainLoop(){
        timer = new javax.swing.Timer
        (
              42, //About 24 fps
             (java.awt.event.ActionEvent e) -> { run(); }
        );
        // Start timer.
        timer.start(); 
    }
    public void refresh() { run(); }
    
    private void run() {
        //Deletes any SDFs that need to be 
        scene.collectGarbageSDFs(); 
        renderScene();
    }
    
    public String[] getResoultion() { return new String[] { ""+width, ""+height }; }
    
    /**
     * Calls the scenes ray marcher which will march
     * the entire screen and return a 2D array of
     * Colors to the scene ... and then return that
     * to this to then finally write it to the
     * image ... and repaint it.
     */
    private void renderScene() {        
        // Render the scene
        Vectors.vec3[][] vec3Image = scene.renderScene();
              
        // Add post processing depending on the settings.
        if (bloom)
            vec3Image = Util.PostProcessor.addBloom(vec3Image, scene.getBackground());
        if (crosshair)
            vec3Image = Util.PostProcessor.addCrossHair(vec3Image, new Vectors.vec3(255.0f), 0.005f);
        
        // Conver the settings 2D array to a java.awt.Color 2D array.
        java.awt.Color[][] colorImage = Util.PostProcessor.convertToColor(vec3Image);
        
        // Fill in the actual screen using the 2D screen array and then update it.
        java.util.stream.IntStream.range(0, this.width).parallel().forEach(x -> {
            for (int y = 0; height > y; y++)
                screen.setRGB(x, y, colorImage[x][y].getRGB());
        });
        repaint();  
    }
    
    private void addRandomSDFs(int n) {
        // Add a floor.
        Util.Material floorMat = new Util.Material(new Vectors.vec3(128));
        floorMat.reflectivity = 0.25f;
        SDFs.SDF floor = new SDFs.Primitives.Plane(new Vectors.vec3(0, 0, -10), new Vectors.vec3(0, 0, 1), floorMat);
        floor.setName("Scene Floor");
        scene.addSDF(floor);
        
        // Add n SDFs
        while (n-- > 0) scene.addSDF(SDFs.SDF.getRandom(new Vectors.vec3(0, 0, 10), 10));
    }
    
    private void addTestingSDFs() {
        Util.Material sphereMat = new Util.Material(new Vectors.vec3(0, 255, 255), Util.Material.GLASS);
        sphereMat.metalness = 0.33f;
        //sphereMat.reflectivity = 0.80f;
        SDFs.SDF sphere  = new SDFs.Primitives.Sphere(new Vectors.vec3(), 1, sphereMat);
        SDFs.SDF cube    = new SDFs.Primitives.Cube(new Vectors.vec3(0, 0, -3), 1, new Util.Material(new Vectors.vec3(128)));

        SDFs.SDF blend = new SDFs.BlendedSDF(sphere, cube, 2.1f);
        blend.setName("Blended Sphere & Cube");
        //scene.addSDF(blend);

        SDFs.SDF cube1 = new SDFs.Primitives.Cube(new Vectors.vec3(0, 0, -3), 1, new Util.Material(new Vectors.vec3(128)));
        scene.addSDF(cube1);
        
        Util.Material floorMat = new Util.Material(new Vectors.vec3(100.0f));
        floorMat.reflectivity = 0.25f;
        SDFs.SDF floor = new SDFs.Primitives.Plane(new Vectors.vec3(0.0f, 0.0f, -4.5f), new Vectors.vec3(0.0f, 0.0f, 1.0f), floorMat);
        floor.setName("Scene Floor");
        scene.addSDF(floor);

        scene.setSceneLighting(new Vectors.vec3(0, 1, -0.5f));
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
        Util.PostProcessor.setWidthHeight(w, h);
    }
        
    public void imageSizer() { screen = new java.awt.image.BufferedImage(width, height, java.awt.image.BufferedImage.TYPE_INT_ARGB); }
    @Override public void paintComponent(java.awt.Graphics g) { super.paintComponent(g); g.drawImage(screen, 0, 0, getWidth(), getHeight(), null); }
    
    // The resolution of F2 screenshots.
    private int hiResW = 2000, hiResH = 2000;
    public void changeF2Res(int w, int h) { hiResW = w; hiResH = h; }
    public String[] getF2Res() { return new String[] { ""+hiResW, ""+hiResH }; }
    public void screenShotHiRes() {
        timer.stop();
        
        System.out.println("Screenshotting ...");
        
        // Saves the current width & height
        int pWidth = width, pHeight = height;
        
        changeResolution(hiResW, hiResH);
        
        Vectors.vec3[][] vec3Image = scene.renderScene();
              
        if (bloom)
            vec3Image = Util.PostProcessor.addBloom(vec3Image, scene.getBackground());
        java.awt.Color[][] colorImage = Util.PostProcessor.convertToColor(vec3Image);
        
        File.ScreenShot.screenshot(colorImage);
        
        changeResolution(pWidth, pHeight);
        
        timer.start();
    }
    
    public void screenShotAsIs() {
        File.ScreenShot.screenshot(screen);
    }
    
}
