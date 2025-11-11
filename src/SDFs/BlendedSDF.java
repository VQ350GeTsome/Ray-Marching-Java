package SDFs;

import Utility.*;
import java.awt.Color;

public class BlendedSDF extends SDF {
    
    private SDF a, b;
    private float k;
    
    public BlendedSDF(SDF a, SDF b, float k) {
        this.a = a;
        this.b = b;
        this.k = k;
    }

    public float sdf(vec3 p) {      
        if (a == null) return b.sdf(p);
        if (b == null) return a.sdf(p);
        
        float d1 = a.sdf(p);
        float d2 = b.sdf(p);
        float h = Math.max(k - Math.abs(d1 - d2), 0.0f) / k;
        return Math.min(d1, d2) - h * h * k * 0.25f;
    }
    
    public Material getMaterial(vec3 p) {
        if (a == null) return b.material;
        if (b == null) return a.material;
        
        float d1 = a.sdf(p);
        float d2 = b.sdf(p);
        //float h = Math.max(k - Math.abs(d1 - d2), 0.0f) / k;  //At seem coloring
        
        float total = Math.abs(d2) + Math.abs(d1);
        float h = (total == 0) ? 0.5f : Math.abs(d1) / total;

        Color c1 = a.getMaterial(p).color;
        Color c2 = b.getMaterial(p).color;
        Color blendedColor = ColorMath.blend(c1, c2, h);

        return new Material(blendedColor);
    }
    
    public float getK() { return k; }
    public void  setK(float k) { this.k = k; }
    
    public SDF getClosest(vec3 p) {
        if (a == null) return b;
        if (b == null) return a;
        
        SDF closest = (a.sdf(p) > b.sdf(p)) ? b : a;        //Get closest SDF
        if (closest instanceof BlendedSDF) {                //But if the closest SDF itself is also blended
            return ((BlendedSDF) closest).getClosest(p);    //Recurse down until we get the closest component
        }
        return closest;                                     //Return the closest one
    }
    
    public void remove(SDF t) {
        if (a == t) {       //If t is a set a to null
            a = null;  
            return;
        } else if (a instanceof BlendedSDF) {   //Else check if t is a child of a
            ((BlendedSDF) a).remove(t);         //If so remove it
            return;
        }

        if (b == t) {       //Do the same as above, just with b
            b = null;
            return;
        } else if (b instanceof BlendedSDF) { 
            ((BlendedSDF) b).remove(t);
            return;
        }
    }
    
    /**
     * If there is an empty child
     * we set n (new SDF) to the null
     * or empty one
     * @param n The child to add
     */
    public void addChild(SDF n) {
        if (a == null) a = n;
        if (b == null) b = n;
    }
    
    /**
     * Checks if the blended SDFs components
     * have both been deleted
     * @return True if it needs to be collected
     */
    public boolean needsCollected() { return (a == null  & b == null); }
    /**
     * Checks if either of the two child SDFs are null
     * if so we can pack it non-blended
     * @return True if either a or b are null
     */
    public boolean needsUnblended() { return (a == null || b == null); }
    
    public String[] getSettings() { return new String[] { "Blending Amount: ", ""+k }; }
    
    public String getType() { return "blended"; }
    
    @Override
    public String toString() { 
        if (a == null) return b.toString();
        if (b == null) return a.toString();
        return a.toString() + b.toString(); 
    }
}
