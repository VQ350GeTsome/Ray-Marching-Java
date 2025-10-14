package objects;

import java.awt.Color;
import utilities.Vector3;

public abstract class ObjectRM {
    
    //Returns the distance to the objects surface
    public abstract double sdf(Vector3 point);
    //Returns the base color of the ObjectRM
    public abstract Color getColor();
    
}
