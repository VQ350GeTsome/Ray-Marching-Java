package Render;

import SDFs.*;
import Utility.*;
import java.awt.Color;
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
    /**
     * Returns The SDF at an input position, else null.
     * @param pos The input position.
     * @return An SDF is one is found ... else null.
     */
    public SDF getSDFAtPos(vec3 pos) {
        for (SDF sdf : sdfs) {                                      //For each SDF
            if (sdf.sdf(pos) <= Core.getEps() * 100) { return sdf; }      //If the SDF is within epsilon, return that sdf
        }
        return null;                                                //If no SDF is found return null
    }
    
    public String packSDFs() {
        String str = "";
        for (SDF sdf : sdfs) {
            str += sdf.toString();
        }
        return str;
    }
    public void unpackSDFs(String[] s) { 
        
        sdfs.clear();       //Clear all current SDFs to load the new scene
        
        IntRef i = new IntRef(0);               //Create a integer referance 
        while (i.i < s.length) {                //Loop through all SDF tokens
            String type = s[i.i++].trim();      //Get the type of SDF
            
            if (type.equals("blended")) {       //If the type is special ie, "blended"
                sdfs.add(SDFParser.parseBlended(s, i));
            } else {                            //Else just parse the regular SDF
                sdfs.add(SDFParser.getSDF(type, s, i));
            }
        }
    }
}
