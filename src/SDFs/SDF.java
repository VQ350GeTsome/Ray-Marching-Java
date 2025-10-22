package SDFs;

import java.awt.Color;
import Utility.vec3;

public abstract class SDF {
    
    //Returns the distance to the objects surface
    public abstract float sdf(vec3 point);
    //Returns the base color of the SDF
    public abstract Color getColor();
    
}
