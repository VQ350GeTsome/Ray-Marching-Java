package SDFs;

import Utility.*;

public class BlendedSDF extends SDF {
    
    private SDF a, b;
    private float k;
    
    public BlendedSDF(SDF a, SDF b, float k) {
        this.a = a;
        this.b = b;
        this.k = k;
    }

    @Override
    public float sdf(vec3 p) {      
        if (a == null) return b.sdf(p);
        if (b == null) return a.sdf(p);
        
        float d1 = a.sdf(p);
        float d2 = b.sdf(p);
        float h = Math.max(k - Math.abs(d1 - d2), 0.0f) / k;
        return Math.min(d1, d2) - h * h * k * 0.25f;
    }
    
    @Override
    public Material getMaterial(vec3 p) {
        if (a == null) return b.m;
        if (b == null) return a.m;
        
        float da = a.sdf(p),    //Distance of a
              db = b.sdf(p);    //Distance of b
        
        float total = Math.abs(db) + Math.abs(da),
              h = (total == 0) ? 0.5f : Math.abs(da) / total;
        
        //a & b's materials
        Material aMat = a.getMaterial(p), bMat = b.getMaterial(p);

        //Use a & b's colors ( from material ) to blend the 
        //two in order to get a nice gradient between the two.
        vec3 c1 = aMat.color, c2 = bMat.color;
        vec3  blendedColor = vec3.blend(c1, c2, h);
        
        //Use a & b's reflectivness ( from material ) to
        //blend the two just like the color.
        float ra = aMat.reflectivity, rb = bMat.reflectivity;
        float blendedR = (ra * h) + (rb * (1.0f-h));
        
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
    
    public void remove(SDF c) {
        if (a == c) {       //If t is a set a to null
            a = null;  
            return;
        } else if (a instanceof BlendedSDF) {   //Else check if t is a child of a
            ((BlendedSDF) a).remove(c);         //If so remove it
            return;
        }

        if (b == c) {       //Do the same as above, just with b
            b = null;
            return;
        } else if (b instanceof BlendedSDF) { 
            ((BlendedSDF) b).remove(c);
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
    
    public void editChild(String[] inputs, SDF c) {
        if (a == c) {       //If t is a set a to null
            a.parseNewParams(inputs);  
            return;
        } else if (a instanceof BlendedSDF) {   //Else check if t is a child of a
            ((BlendedSDF) a).editChild(inputs, c);         //If so remove it
            return;
        }

        if (b == c) {       //Do the same as above, just with b
            b.parseNewParams(inputs);
            return;
        } else if (b instanceof BlendedSDF) { 
            ((BlendedSDF) b).editChild(inputs, c);
            return;
        }
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
    
    @Override
    public String[] getSettingsAndCurrent() { return new String[] { "Blending Amount: ", ""+k }; }
    
    @Override
    public String getType() { return "blended"; }
    
    @Override public boolean parseNewParams(String[] inputs) { return false; }
    
    @Override
    public String toString() { 
        if (a == null) return b.toString();
        if (b == null) return a.toString();
        return a.toString() + b.toString(); 
    }
}
