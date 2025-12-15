package Render;

public class SDFManager {
    
    private final java.util.List<SDFs.SDF> sdfs = new java.util.ArrayList<>();
    
    /**
     * Adds and SDF to the internal container.
     * 
     * @param sdf The SDF to add.
     * @return If the operation completed.
     */
    public boolean addSDF(SDFs.SDF sdf)      { return sdfs.add(sdf); }
    /**
     * Removes a specific SDF from the internal container.
     * 
     * @param sdf The SDF to remove.
     * @return If the operation was completed.
     */
    public boolean removeSDF(SDFs.SDF sdf)   { return sdfs.remove(sdf); }
    /**
     * Sets an
     * @param s
     * @param n
     * @return 
     */
    public boolean setSDF(SDFs.SDF s, SDFs.SDF n) {
        return sdfs.remove(s) &&
               sdfs.add(n);
    }
    
    /**
     * Returns the distance to the closest surface of the SDFs
     * from the input vector.
     * 
     * @param pos The input vector, a point in space.
     * @return How close that point in space is to an SDF object.
     */
    public float getClosestSDFDist(Vectors.vec3 pos) {
        float minDist = Float.MAX_VALUE, dist;
        for (SDFs.SDF sdf : sdfs) {          //For each SDF
            dist = sdf.sdf(pos);        //Get the distance to surface
            minDist = (minDist > dist)  //Store the closest surface
                    ? dist : minDist;
        }
        return minDist;                 //Return the closest surface
    }
    /**
     * Returns the closest surface of the SDFs contained
     * from the input vector.
     * 
     * @param pos
     * @return 
     */
    public float getClosestSDFDistSkipCam(Vectors.vec3 pos) {
        float minDist = Float.MAX_VALUE, dist;
        for (SDFs.SDF sdf : sdfs) if (!sdf.getType().equals("camera")) {          //For each SDF that's not the camera
            dist = sdf.sdf(pos);        //Get the distance to surface
            minDist = (minDist > dist)  //Store the closest surface
                    ? dist : minDist;
        }
        return minDist;                 //Return the closest surface
    }
    
    /**
     * Returns The SDF at an input position, else null.
     * 
     * @param p The input position.
     * @return An SDF is one is found ... else null.
     */
    public SDFs.SDF getSDFAtPos(Vectors.vec3 p) {
        for (SDFs.SDF s : sdfs) {                                             //For each SDF
            if (Core.getEps() * 3.0f > s.sdf(p)) { return s; }      //If the SDF is within epsilon, return that sdf
        }
        return null;                                                        //If no SDF is found return null
    }
    public Util.HitInfo getNearestSDFAtPos(Vectors.vec3 p) {
        float minDist = Float.MAX_VALUE, d;
        SDFs.SDF near = null;
        for (SDFs.SDF s : sdfs) {
            d = s.sdf(p);
            if (minDist > d) {
                minDist = d;
                near = s;
            }
        }
        return new Util.HitInfo(p, minDist, near);
    }
    
    /**
     * Garbage collector. Checks if any blended SDFs
     * need to be collected and trashed.
     */
    public void gc() {
        java.util.List<SDFs.SDF> toRemove = new java.util.ArrayList<>();
        for (SDFs.SDF sdf : sdfs) {
            if (sdf instanceof SDFs.BlendedSDF && ((SDFs.BlendedSDF) sdf).needsCollected()) {
                toRemove.add(sdf);
            }
        }
        sdfs.removeAll(toRemove);
    }
    
    /**
     * Packs SDFs into one big String
     * using their .toString()
     * @return 
     */
    public String packSDFs() {
        String str = "";
        // Loop through all SDFs skipping the camera SDF.
        for (SDFs.SDF sdf : sdfs) if (!sdf.getType().equals("camera")) { 
            if (sdf instanceof SDFs.BlendedSDF) {    //If of type blended append the blended tag
                float k = ((SDFs.BlendedSDF) sdf).getK();    //Blending factor
                boolean needsTagged = !((SDFs.BlendedSDF) sdf).needsUnblended(); //If we need to tag the new SDF as blended
                if (needsTagged) str += "blended," + k + ",\n";
                str += sdf.toString();
                if (needsTagged) str += "endblend,\n";
            } else {
                str += sdf.toString();
            }
        }
        return str;
    }
    /**
     * Clears the scene, then adds the packed SDFs to the scene.
     * The packed SDFs must be in the format provided by
     * {@link #packSDFs()}.
     * 
     * @param s String array that contains packed SDFs.
     * 
     * @see #packSDFs()
     */
    public void unpackSDFs(String[] s) { 
        // Clear all current SDFs to load the new scene
        sdfs.clear();       
        // Unpack all the SDFs in s 
        unpack(s);          
    }
    
    /**
     * Parses through a String array
     * @param String array that contains packed SDFs
     */
    private void unpack(String[] s) {
        // Create an integer referance 
        Util.IntRef i = new Util.IntRef();
        
        // Loop through all SDF tokens
        while (i.i < s.length) {    
            
            // Get the type of SDF            
            String type = s[i.i++].trim();      
            
            // Check if the sdf has a special token, if so 
            // we must perform a special action. Else, we
            // just parse the SDF and add it to our list.
            if (type.equals("blended")) {       
                float k = Float.parseFloat(s[i.i++].trim());
                sdfs.add(SDFs.SDFParser.parseBlended(s, k, i));
            } else sdfs.add(SDFs.SDFParser.getSDF(type, s, i));
            
        }
    }
}
