package SDFs;

import SDFs.Primitives.*;
import Utility.*;
import java.awt.Color;
import java.util.ArrayList;

public class SDFParser {
    
    public static SDF getSDF(String type, String[] info, IntRef i) {
                
        String[] rgb = info[i.i++].split(":");               //Parse the RGB of the SDF
        int r = (int) Float.parseFloat(rgb[0].trim()),
            g = (int) Float.parseFloat(rgb[1].trim()),
            b = (int) Float.parseFloat(rgb[2].trim());
        Color color = new Color(r, g, b);
        
        switch (type) {
                case "sphere":
                    return SDFParser.parseSphere(info, color, i);
                case "cube":
                    return SDFParser.parseCube(info, color, i);
                case "torus":
                    return SDFParser.parseTorus(info, color, i);      
                case "plane":
                    return SDFParser.parsePlane(info, color, i);
                default:
                    System.err.println("Unknown SDF type: " + type);
                    break;
        }
        return null;
    }
    
    public static SDF parseBlended(String[] info, IntRef i) {
        ArrayList<SDF> sdfsToBlend = new ArrayList<>(2);
        while (true) {
            String type = info[i.i++].trim(); //Read the type
            if (type.equals("endblend")) { //If the type is endblend end break
                break;
            }     
            sdfsToBlend.add(getSDF(type, info, i));
        }
        return null; //BLEND SDFs AND RETURN THEMMM
    }
    
    public static SDF parseSphere(String[] info, Color c, IntRef i) {
        vec3 center = new vec3(info[i.i++]);
        float radius = Float.parseFloat(info[i.i++].trim());
        return new Sphere(center, radius, c);
    }
    
    public static SDF parseCube(String[] info, Color c, IntRef i) {
        vec3 center = new vec3(info[i.i++]);
        float size = Float.parseFloat(info[i.i++].trim());
        return new Cube(center, size, c);
    }
    
    public static SDF parseTorus(String[] info, Color c, IntRef i) {
        vec3 center = new vec3(info[i.i++]);
        float majorR = Float.parseFloat(info[i.i++].trim());
        float minorR = Float.parseFloat(info[i.i++].trim());
        return new Torus(center, majorR, minorR, c);
    }
    
    public static SDF parsePlane(String[] info, Color c, IntRef i) {
        vec3 pos = new vec3(info[i.i++]);
        vec3 normal = new vec3(info[i.i++]);
        System.out.println(i.i);
        return new Plane(pos, normal, c);
    }
}
