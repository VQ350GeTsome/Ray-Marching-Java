package SDFs;

import Vectors.vec3;
import Util.Material;

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
        
        float da = a.sdf(p);
        float db = b.sdf(p);
        float h = Math.max(k - Math.abs(da - db), 0.0f) / k;
        return Math.min(da, db) - h * h * k * 0.25f;
    }
    
    @Override
    public Material getMaterial(vec3 p) {
        if (a == null) return b.m;
        if (b == null) return a.m;
        
        float da = a.sdf(p),    //Distance of a
              db = b.sdf(p);    //Distance of b
        
        float total = Math.abs(da) + Math.abs(db),
                  h = (total == 0) ? 0.5f : Math.abs(da) / total;
        
        //System.out.println("Dist of A: " + da + "\tDist of B: " + db + "\tCalculated H: " + h);
        
        //a & b's materials
        Material aMat = a.getMaterial(p), bMat = b.getMaterial(p);

        Material blendMat = aMat.blend(bMat, h);
        //System.out.println("Calculated H: " + h + "\tReflectivity: " + blendMat.reflectivity);
        return blendMat;
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
        //If the child, c, we're trying to remove is a child
        //of this, set it to null, removing it.
             if (a == c) a = null;  
        else if (b == c) b = null;
        
        //If the child isn't a child of this, see if any of our
        //children are blended themselves, ( meaning they themselves
        //have children ), and if so remove it.
        else if (a instanceof BlendedSDF) ((BlendedSDF) a).remove(c);      
        else if (b instanceof BlendedSDF) ((BlendedSDF) b).remove(c);
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
    public SDFs.SDF getChildA() { return a; }
    public SDFs.SDF getChildB() { return b; }
    
    public void editChild(String[] inputs, SDF c) {
        //Just like remove(), we first check if the child we are trying
        //to edit is one of our children, and if so we edit it.
             if (a == c) a.parseNewParams(inputs);  
        else if (b == c) b.parseNewParams(inputs);
        
        //Again just like remove(), if it's not one of our children, check 
        //if our children are blended, ( they have children ), and query them.
        else if (a instanceof BlendedSDF) ((BlendedSDF) a).editChild(inputs, c);       
        else if (b instanceof BlendedSDF) ((BlendedSDF) b).editChild(inputs, c);
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
    
    public void setMaterial(SDF c, Material m) {
        //Just like remove() & editChild() use a recursive method to
        //search all our children & grandchildren and so on.
             if (a == c) a.setMaterial(m);
        else if (b == c) b.setMaterial(m);

        else if (a instanceof BlendedSDF) ((BlendedSDF) a).setMaterial(c, m);
        else if (b instanceof BlendedSDF) ((BlendedSDF) b).setMaterial(c, m);
    }
    
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
