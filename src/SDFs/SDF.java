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
    /**
     * Default toString method, returns the type & color
     * @return 
     */
    @Override
    public String toString() {
        return getType() + "," + m.toString() + "," +  rotQuat.toString() + ",";
    }
}
