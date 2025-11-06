package SDFs;

import Utility.Material;
import java.awt.Color;
import Utility.vec3;

/**
 * Abstract SDF class. Each SDF Object must implement a method to get
 * the color & the actual signed distance function (how close a ray is 
 * to the surface of it.
 * @author Harrison
 */
public abstract class SDF {
    
    protected Material material;
    
    /**
     * Takes in a point and returns the distance 
     * to the surface of the object.
     * @param point The point we are using to calculate the distance.
     * @return The distance to the surface.
     */
    public abstract float sdf(vec3 point);
    /**
     * Returns a String array full of the name of each
     * setting, for a Torus it'd be something like
     * { "Center: ", "Radius Major: ", "Radius Minor: " };
     * But is also includes the current setting for each.
     * So given any index i + length / 2 would return
     * the current argument for that setting
     * @return 
     */
    public abstract String[] getSettings();
    /**
     * Returns the material of the SDF object
     * @return The material.
     */
    public Material getMaterial(vec3 p) { return material; }
    /**
     * Gets the type of SDF as a String
     * @return The type, i.e., "sphere"
     */
    public abstract String getType();
    /**
     * Turns the color into a String that can be 
     * saved & parsed later
     * @return The String, i.e., "255:50:150" (Mageneta)
     */
    public String colorString() {
        Color c = material.color;
        return c.getRed() + ":" + c.getGreen() + ":" + c.getBlue();
    }
    /**
     * Default toString method, returns the type & color
     * @return 
     */
    @Override
    public String toString() {
        return getType() + "," + colorString() + ",";
    }
}
