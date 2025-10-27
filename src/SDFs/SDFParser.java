package SDFs;

import SDFs.Primitives.Cube;
import SDFs.Primitives.Sphere;
import SDFs.Primitives.Torus;
import Utility.vec3;
import java.awt.Color;

public class SDFParser {
    
    public static SDF parseSphere(String[] info, Color c, int i) {
        vec3 center = new vec3(info[i++]);
        float radius = Float.parseFloat(info[i++].trim());
        return new Sphere(center, radius, c);
    }
    
    public static SDF parseCube(String[] info, Color c, int i) {
        vec3 center = new vec3(info[i++]);
        float size = Float.parseFloat(info[i++].trim());
        return new Cube(center, size, c);
    }
    
    public static SDF parseTorus(String[] info, Color c, int i) {
        vec3 center = new vec3(info[i++]);
        float majorR = Float.parseFloat(info[i++].trim());
        float minorR = Float.parseFloat(info[i++].trim());
        return new Torus(center, majorR, minorR, c);
    }
}
