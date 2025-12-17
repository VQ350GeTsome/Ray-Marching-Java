package SDFs;

/**
 * Abstract SDF class. Each SDF Object must implement a method to get
 * the color & the actual signed distance function (how close a ray is 
 * to the surface of it.)
 * As well as methods to get their settings, type, etc... to be used
 * when adding SDFs to the scene or editing them.
 * @author Harrison
 */
public abstract class SDF {
    
    protected Util.Material m;
    protected ComplexNumbers.Quaternion rotQuat = new ComplexNumbers.Quaternion();
    protected String name, type;
    protected final static int START = Util.Material.FIELDS + 2;
    
    /**
     * Takes in a point and returns the distance 
     * to the surface of the object.
     * @param point The point we are using to calculate the distance.
     * @return The distance to the surface.
     */
    public abstract float sdf(Vectors.vec3 point);
    /**
     * Returns a String array full of the name of each
     * setting, for a Torus it'd be something like
     * { "Center: ", "Radius Major: ", "Radius Minor: " };
     * But is also includes the current setting for each.
     * So given any index i + length / 2 would return
     * the current argument for that setting
     * @return 
     */
    public String[] getSettingsAndCurrent() { return SDFParser.getSettings(type); }
    public abstract boolean parseNewParams(String[] inputs);
    
    /**
     * Returns the material of the SDF object
     * @param p The position of the vector
     * @return The material.
     */
    public Util.Material getMaterial(Vectors.vec3 p) { return m; }
    public Util.Material getMaterial() { return m; }
    public void setMaterial(Util.Material material) { m = material; }
    
    public ComplexNumbers.Quaternion getRotQuat() { return rotQuat; }
    public void setRotQuat(ComplexNumbers.Quaternion q) { rotQuat = q; }
    
    public void setColor(Vectors.vec3 color) { m.color = color; }
    public void setHighlightColor(Vectors.vec3 color) { m.specularColor = color; }
    /**
     * Gets the type of SDF as a String
     * @return The type, i.e., "sphere"
     */
    public String getType() { return type; }
    /**
     * Gets and returns the name of this SDF
     * @return The name of it
     */
    public String getName() { return name; }
    /**
     * Sets the name of this SDF
     * @param n The new name
     */
    public void setName(String n) { name = n; }
    
    public static final SDFs.SDF getRandom(Vectors.vec3 center, float maxDistFromCenter) {
        // Get some random color.
        Vectors.vec3 color = Vectors.vec3.getRandom();
        color = color.scale(255.0f);
        
        // Get some random material
        int matType = (int) (Math.random() * 4.0);
        final int PLASTIC = 0, MIRROR = 1, GLASS = 2, METAL = 3;
        Util.Material matToUse = null;
        
        switch (matType) {
            case PLASTIC: matToUse = Util.Material.PLASTIC; break;
            case MIRROR: matToUse = Util.Material.MIRROR; break;
            case GLASS: matToUse = Util.Material.GLASS; break;
            case METAL: matToUse = Util.Material.METAL; break;
        }
        
        Util.Material mat = new Util.Material(color, matToUse);
        
        // Get a random position within the bounds
        Vectors.vec3 pos = Vectors.vec3.getRandom().normalize();
        pos = pos.scale(2.0f)
                 .subtract(1.0f)
                 .scale((float) (Math.random() * maxDistFromCenter))
                 .add(center);

        // Get some random inital size.
        final float SCALE = 3.0f, BIAS = 0.33f;
        float size1 = BIAS + (float) (Math.random() * SCALE);
        
        // Get a random type, sphere, cube, cylinder, or torus.
        int sdfType = (int) (Math.random() * 4);
        final int SPHERE = 0, CUBE = 1, CYLINDER = 2, TORUS = 3;
        
        // Get a second size scalar if needed, & size it correctly.
        float size2 = 0;
        if (sdfType == CYLINDER ) size2 = BIAS + (float) (Math.random() * SCALE);
        if (sdfType == TORUS) size2 = BIAS + (float) (Math.random() * (size1 / 1.5f));
        
        // Parse the type and add everything together to create a random SDF.
        switch (sdfType) {
            case SPHERE: 
                return new SDFs.Primitives.Sphere(pos, size1, mat);
            case CUBE:
                return new SDFs.Primitives.Cube(pos, size1, mat);
            case CYLINDER:
                return new SDFs.Primitives.Cylinder(pos, size1, size2, mat);
            case TORUS:
                return new SDFs.Primitives.Torus(pos, size1, size2, mat);
            // If somehow default is reached just return some random grey sphere...
            default:
                return new SDFs.Primitives.Sphere(
                        new Vectors.vec3(0.0f), 1, new Util.Material(new Vectors.vec3(128), Util.Material.PLASTIC)
                );
        }
    }
    
    /**
     * Default toString method, returns the type & color
     * @return 
     */
    @Override
    public String toString() {
        return getType() + "," + m.toString() + "," +  rotQuat.toString() + ",";
    }
}
