package Render;

/**
 * An SDF manager class. Meant to help with the adding, setting, 
 * removing, querying, etc of the SDFs in the scene.
 * 
 * @author Harrison Davis
 */
public final class SDFManager {
    
    // The internal container of SDFs.
    private final java.util.List<SDFs.SDF> sdfs = new java.util.ArrayList<>();
    
    /**
     * Adds and SDF to the internal container.
     * 
     * @param sdf The SDF to add.
     * @return If the operation completed.
     */
    public boolean addSDF(SDFs.SDF sdf) { return sdfs.add(sdf); }
    /**
     * Removes a specific SDF from the internal container.
     * If that specific SDF isn't found, it will search the 
     * blended SDFs in the internal container.
     * 
     * @param sdf The SDF to remove.
     * @return If the operation was completed.
     */
    public boolean removeSDF(SDFs.SDF sdf) { 
        // Try to remove from the list plainly.
        if (!sdfs.remove(sdf)) {
            // If we didn't find the SDF search all the blendedSDFs.
            for (SDFs.SDF s : sdfs) if (s instanceof SDFs.BlendedSDF b) 
                // If the sdf is found inside one of the blendedSDFs return true.
                if (b.remove(sdf)) return true;
        }
        return false;
    }
    /**
     * Replaces an SDF. 
     * 
     * @param removeSDF The SDF to remove.
     * @param newSDF The SDF to add.
     * @return If the operation was completed.
     */
    public boolean setSDF(SDFs.SDF removeSDF, SDFs.SDF newSDF) {
        return sdfs.remove(removeSDF) &&
               sdfs.add(newSDF);
    }
    /**
     * Get an SDF at a specific index location.
     * 
     * @param i The index.
     * @return The SDF at that index.
     */
    public SDFs.SDF getSDF(int i) { return this.sdfs.get(i); }
    
    /**
     * Calculates the signed distance to the surface of each SDF
     * and returns the minimum.
     * 
     * @param p The position vector.
     * @return The distance to the nearest SDF's surface, 
     *      {@link Float#Max_VALUE} if no SDF's are in this manager.
     */
    public float getClosestSDFDist(Vectors.vec3 p) {       
        // For each SDF calculate the distance to it's surface.
        // Return the closest surface, if there is no closest
        // surface return Float.MAX_VALUE.
        return sdfs.stream()
               .map(sdf -> sdf.sdf(p))
               .min(Float::compare)
               .orElse(Float.MAX_VALUE);               
    }
    public float getClosestSDFDist(Vectors.vec3 p, java.util.function.Predicate<SDFs.SDF> condition) {
        // Filter out SDFs using the condition.
        // Then return the closest surface.
        return sdfs.stream()
                   .filter(condition)              
                   .map(sdf -> sdf.sdf(p))         
                   .min(Float::compare)            
                   .orElse(Float.MAX_VALUE);       
    }

    /**
     * Returns the first SDF that's calculated to be 
     * within a small distance of the input vector.
     * If none is found it returns null.
     * 
     * @param p The vector position.
     * @return An SDF is one is near, else null.
     */
    public SDFs.SDF getSDFAtPos(Vectors.vec3 p) {
        // Stream the SDF's
        return sdfs.stream()
                   // Find the first SDF that's close to the input
                   // vector. If none is found return null.
                   .filter(sdf -> sdf.sdf(p) < Core.EPS * 3.0f)
                   .findFirst()
                   .orElse(null);
    }   
    /**
     * Calculates the closest SDF and returns it inside a 
     * {@link Util.HitInfo} object along with the it's distance
     * and the position vector.
     * 
     * @param p The position vector.
     * @return A {@link Util.HitInfo} that contains the position vector, 
     *      the distance to the closest SDF, and the closest SDF.
     */
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
     * Removes any SDFs that need to be removed.
     */
    public void garbageCollector() {
        sdfs.removeIf(sdf -> 
                sdf instanceof SDFs.BlendedSDF && ((SDFs.BlendedSDF) sdf).needsCollected()
        );
    }
    
    /**
     * Packs all the SDFs contained into one String representation, with CSVs.
     * 
     * The {@link Camera} object's SDF will be skipped.
     * 
     * Any special SDF's such as {@link SDFs.BlendedSDF} will be tagged as such
     * with their special values, like the blending factor.
     * 
     * @return A String array that contains the newly packed SDFs.
     * 
     * @see #unpackSDFs(String[]) .
     */
    public String packSDFs() {
        String str = "";
        // Loop through all SDFs skipping the camera SDF.
        for (SDFs.SDF sdf : sdfs) if (!sdf.getType().equals("camera")) { 
            // If the SDF is a special blended SDF
            // we must parse its blending factor as
            // well as tagging it as blended.
            if (sdf instanceof SDFs.BlendedSDF blendedSDF) {    
                float k = blendedSDF.getK();    //Blending factor
                boolean needsTagged = !blendedSDF.needsUnblended();
                if (needsTagged) str += "blended," + k + ",\n";
                str += sdf.toString();
                if (needsTagged) str += "endblend,\n";
            } 
            // Else we just append the SDF to the String.
            else str += sdf.toString();
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
     * @see #packSDFs() .
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
        Util.IntWrapper i = new Util.IntWrapper();
        
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
    
    public String[] getNames() {
        String[] arr = new String[sdfs.size()];
        for (int i = 0; arr.length > i; i++) {
            SDFs.SDF s = sdfs.get(i);
            String name = s.getName();
            if (name == null) name = s.getType();
            arr[i] = name;
        }
        return arr;
    }
}
