package SDFs;

import java.awt.Color;
import Utility.vec3;

/**
 * Abstract SDF class. Each SDF Object must implement a method to get
 * the color & the actual signed distance function (how close a ray is 
 * to the surface of it.
 * @author Harrison
 */
public abstract class SDF {
    /**
     * Takes in a point and returns the distance 
     * to the surface of the object.
     * @param point The point we are using to calculate the distance.
     * @return The distance to the surface.
     */
    public abstract float sdf(vec3 point);
    /**
     * Returns the color of the object.
     * @return The color.
     */
    public abstract Color getColor();
    public abstract String getType();
    
    public String colorString() {
        Color c = getColor();
        return c.getRed() + ":" + c.getGreen() + ":" + c.getBlue();
    }
    
    @Override
    public String toString() {
        return getType() + "," + colorString() + ",";
    }
}
