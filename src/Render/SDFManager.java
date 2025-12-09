package Render;

import Util.IntRef;
import Vectors.vec3;
import SDFs.*;
import java.util.ArrayList;

public class SDFManager {
    
    private ArrayList<SDF> sdfs = new ArrayList<>();
    
    /**
     * Adds and SDF to SDFManagers list of SDFs.
     * @param sdf The SDF to add.
     * @return If the operation completed.
     */
    public boolean addSDF(SDF sdf)      { return sdfs.add(sdf); }
    /**
     * Removes a specific SDF.
     * @param sdf The SDF to remove.
     * @return If the operation was completed.
     */
    public boolean removeSDF(SDF sdf)   { return sdfs.remove(sdf); }
    public boolean setSDF(SDF s, SDF n) {
        return sdfs.remove(s) &&
               sdfs.add(n);
    }
    
    /**
     * Returns the closest surface of the SDFs contained
     * from the input vector.
     * @param pos The input vector, a point in space.
     * @return How close that point in space is to an SDF object.
     */
    public float getClosestSDFDist(vec3 pos) {
        float minDist = Float.MAX_VALUE, dist;
        for (SDF sdf : sdfs) {          //For each SDF
            dist = sdf.sdf(pos);        //Get the distance to surface
            minDist = (minDist > dist)  //Store the closest surface
                    ? dist : minDist;
        }
        return minDist;                 //Return the closest surface
    }
    
    public float getClosestSDFDistSkipCam(vec3 pos) {
        float minDist = Float.MAX_VALUE, dist;
        for (SDF sdf : sdfs) if (!sdf.getType().equals("camera")) {          //For each SDF that's not the camera
            dist = sdf.sdf(pos);        //Get the distance to surface
            minDist = (minDist > dist)  //Store the closest surface
                    ? dist : minDist;
        }
        return minDist;                 //Return the closest surface
    }
    
    /**
     * Returns The SDF at an input position, else null.
     * @param p The input position.
     * @return An SDF is one is found ... else null.
     */
    public SDF getSDFAtPos(vec3 p) {
        for (SDF s : sdfs) {                                             //For each SDF
            if (Core.getEps() * 3.0f > s.sdf(p)) { return s; }      //If the SDF is within epsilon, return that sdf
        }
        return null;                                                        //If no SDF is found return null
    }
    public Util.HitInfo getNearestSDFAtPos(vec3 p) {
        float minDist = Float.MAX_VALUE, d;
        SDF near = null;
        for (SDF s : sdfs) {
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
        ArrayList<SDF> toRemove = new ArrayList<>();
        for (SDF sdf : sdfs) {
            if (sdf instanceof BlendedSDF && ((BlendedSDF) sdf).needsCollected()) {
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
        for (SDF sdf : sdfs) if (!sdf.getType().equals("camera")) { //Don't save the camera
            if (sdf instanceof BlendedSDF) {    //If of type blended append the blended tag
                float k = ((BlendedSDF) sdf).getK();    //Blending factor
                boolean needsTagged = !((BlendedSDF) sdf).needsUnblended(); //If we need to tag the new SDF as blended
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
     * Unpacks multiple SDFs in the format used by unpack()
     * @param s String array that contains packed SDFs
     */
    public void unpackSDFs(String[] s) { 
        sdfs.clear();       //Clear all current SDFs to load the new scene
        unpack(s);          //Unpack all the SDFs in s 
    }
    
    /**
     * Parses through a String array
     * @param String array that contains packed SDFs
     */
    private void unpack(String[] s) {
        IntRef i = new IntRef(0);               //Create a integer referance 
        while (i.i < s.length) {                //Loop through all SDF tokens
            String type = s[i.i++].trim();      //Get the type of SDF
            
            if (type.equals("blended")) {       //If the type is special ie, "blended"
                float k = Float.parseFloat(s[i.i++].trim());
                sdfs.add(SDFParser.parseBlended(s, k, i));
            } else {                            //Else just parse the regular SDF
                sdfs.add(SDFParser.getSDF(type, s, i));
            }
        }
    }
}
