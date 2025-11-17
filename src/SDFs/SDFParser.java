package SDFs;

import SDFs.Primitives.*;
import SDFs.Special.*;
import Utility.*;
import java.util.ArrayList;

public class SDFParser {
    
    public static SDF getSDF(String type, String[] info, IntRef i) {
        
                
        String[] rgb = info[i.i++].split(":");               //Parse the RGB of the SDF
        int r = (int) Float.parseFloat(rgb[0].trim()),
            g = (int) Float.parseFloat(rgb[1].trim()),
            b = (int) Float.parseFloat(rgb[2].trim());
        
        
        //Use the color to instantiate the material then fill the fields.
        Material mat = new Material(new vec3(r, g, b));
        mat.reflectivity    = Float.parseFloat(info[i.i++].trim());
        mat.specular        = Float.parseFloat(info[i.i++].trim());
        mat.roughness       = Float.parseFloat(info[i.i++].trim());
        mat.metalness       = Float.parseFloat(info[i.i++].trim());
        mat.opacity         = Float.parseFloat(info[i.i++].trim());
        mat.ior             = Float.parseFloat(info[i.i++].trim());

        
        switch (type) {
            case "sphere":
                return parseSphere(info, mat, i);
            case "cube":
                return parseCube(info, mat, i);
            case "torus":
                return parseTorus(info, mat, i);      
            case "plane":
                return parsePlane(info, mat, i);
                
            case "repeatsphere":
                SDF s = parseSphere(info, mat, i);
                return new Repeating((Sphere) s, Float.parseFloat(info[i.i++]));
                
            case "hollowcc":
                return parseHollowCC(info, mat, i);
                
            default:
                System.err.println("Unknown SDF type: " + type);
                break;
        }
        return null;
    }
    public static SDF getSDF(String type, String[] info) { return getSDF(type, info, new IntRef(0)); }
    
    public static String[] getTypes() {
        return new String[] { "Primitive", "Repeating" };
    }
    public static String[] getPrimitives() {
        return new String[] { "Sphere", "Cube", "Torus", "Plane" };
    }
    public static String[] getSettings(String type) { return getSettings(type, false); }
    public static String[] getSettings(String type, boolean repeat) {
        
        String[] objDependant = null;
        
        switch (type.toLowerCase()) {           
            case "sphere": 
            case  "cube" :
                objDependant = new String[] { "Center: ", "Size: " };    
                break;
            case "torus":
                objDependant = new String[] { "Center: ", "Radius Major: ", "Radius Minor: " };
                break;
            case "plane":
                objDependant = new String[] { "Position: ", "Normal: " };
                break;
                
            case "hollowcc":
                objDependant = new String[] { "Center: ", "Scale: ", "n: " };
                break;
        }
        
        //Create the return array which is the material settings + object dependant settings
        //And if it's a repeating SDF + the extra settings.

        return (!repeat) ? 
                objDependant : ArrayMath.add(objDependant, new String[] { "Spacing: " } );
    }
    
    public static SDF parseBlended(String[] info, float k, IntRef i) {
        ArrayList<SDF> sdfsToBlend = new ArrayList<>(2);
        while (true) {
            String type = info[i.i++].trim(); //Read the type
            if (type.equals("endblend")) { //If the type is endblend end break
                break;
            }     
            sdfsToBlend.add(getSDF(type, info, i));
        }
        return mergeBlended(sdfsToBlend, k);
    }
    private static SDF mergeBlended(ArrayList<SDF> sdfs, float k) {
        if (sdfs.size() == 1) return sdfs.get(0);   //Base case
        SDF a = sdfs.get(0);    //Get the next SDF
        sdfs.remove(a);         //Then remove it
        return new BlendedSDF(a, mergeBlended(sdfs, k), k);
    }
    
    private static SDF parseSphere(String[] info, Material m, IntRef i) {
        vec3 center = new vec3(info[i.i++]);
        float radius = Float.parseFloat(info[i.i++].trim());
        return new Sphere(center, radius, m);
    }
    
    private static SDF parseCube(String[] info, Material m, IntRef i) {
        vec3 center = new vec3(info[i.i++]);
        float size = Float.parseFloat(info[i.i++].trim());
        return new Cube(center, size, m);
    }
    
    private static SDF parseTorus(String[] info, Material m, IntRef i) {
        vec3 center = new vec3(info[i.i++]);
        float majorR = Float.parseFloat(info[i.i++].trim());
        float minorR = Float.parseFloat(info[i.i++].trim());
        return new Torus(center, majorR, minorR, m);
    }
    
    private static SDF parsePlane(String[] info, Material m, IntRef i) {
        vec3 pos = new vec3(info[i.i++]);
        vec3 normal = new vec3(info[i.i++]);
        return new Plane(pos, normal, m);
    }
    
    private static SDF parseHollowCC(String[] info, Material m, IntRef i) {
        vec3 center = new vec3(info[i.i++]);
        float scale = Float.parseFloat(info[i.i++]);
        float n     = Float.parseFloat(info[i.i++]);
        return new HollowChainCube(center, scale, n, m);
    }
}
