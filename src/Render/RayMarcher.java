package Render;

import SDFs.SDF;
import Utility.*;
import java.awt.Color;
import java.util.stream.IntStream;

public class RayMarcher {

    private Camera      camera;
    private Light       light;
    private SDFManager  sdfMgr;
    
    private Color background    = Color.BLACK;
    private int   maxSteps      = 100,
                  maxDist       = 100,
                  shadowSteps   = 50;

    public RayMarcher(Camera camera, Light light, SDFManager sdfMgr) {
        this.camera     = camera;
        this.light      = light;
        this.sdfMgr     = sdfMgr;
    }
        
    public HitInfo marchRay(int x, int y, int w, int h) {
        float nx = (x + 0.5f) / (float)w;
        float ny = (y + 0.5f) / (float)h;
        vec3 dir = camera.getRayDirection(nx, ny, w / (float)h);
        vec3 pos = camera.getPosition();
        
        float totalDistance = 0.0f;
        for (int step = 0; step < maxSteps; step++) {
            float distance = sdfMgr.getClosestSDFDist(pos);        //Minimum distance we can step

            if (distance < Core.getEps()) { return new HitInfo(pos, totalDistance); }
            if (totalDistance > maxDist) { return new HitInfo(pos, totalDistance); }

            pos =   pos
                    .add(   dir
                            .multiply( distance ) 
                    );
            totalDistance += distance;

        }
        return new HitInfo(pos, totalDistance);
    }
    
    public float getShadow(vec3 orgin) {
        float ambientLight = light.getAmbeintLight();       //Get the scenes ambient lighting
        
        float lighting = 1.0f - ambientLight;               //Minus the starting light % by the ambient light, for it will be added back later ... 
        float t = 0.1f;                                     //Start slightly off the surface ... this must be greater than epsilon
        final float softness = 1.0f;                        //Penumbra width ... i think
        vec3 lightDir = light.getSceneLighting()
                        .multiply(-1.0f);                   //Flip lighting around
        for (int i = 0; shadowSteps > i; i++) {
            vec3 pos =  orgin
                        .add(   lightDir 
                                .multiply(t)
                        );
            float minDist = sdfMgr.getClosestSDFDist(pos);
            if (minDist < Core.getEps()) { return ambientLight; }
            lighting = Math.min(lighting, softness * minDist / t);
            t += minDist;
            if (lighting > maxDist) { break; }
        }
        return lighting + ambientLight;
    }
    
    public Color[][] marchScreen(int w, int h) {
        Color[][] screen = new Color[w][h];                                 //2D array for screen of size { width , height }
        
        IntStream.range(0, w).parallel().forEach(x -> {                     //Parellelize each x row and call the for loop for each y column
            for (int y = 0; h > y; y++) {                                   //This works each column out in parallel
                HitInfo hit = marchRay(x, y, w, h);
                vec3 hitVec = marchRay(x, y, w, h).hit;                     //March ray from pixel { x , y } and get the point it hits
                SDF hitObj = sdfMgr.getSDFAtPos(hitVec);                    //Check to see if their is an SDF where the ray hit
                if (hitObj == null) screen[x][y] = background;              //If there is none, set the current pixel to the background color
                else {
                    Color objColor  = hitObj.getColor();                    //Get the objects we hits color
                    float lighting  = getShadow(hitVec);                    //Cast a ray to the scene light direction to see if it is occluded
                    float fog       = 1 - (hit.totalDist / maxDist);
                                                          
                    Color finalColor =  ColorMath                           
                                        .scale(objColor, lighting);
                    finalColor =        ColorMath
                                        .blend(finalColor, background, fog);
                    screen[x][y] = finalColor;
                }
            }
        });
        return screen;
    }
    
    public String packRayMarcher() {
        return  background.getRed() + ":" + background.getGreen() + ":" + background.getBlue() + "," +
                maxSteps    + "," + maxDist + "," +
                shadowSteps + "," ;
    }
    public void unpackRayMarcher(String[] parts) {
        String[] rgb = parts[0].split(":");
        
        if (rgb.length != 3) 
            throw new IllegalArgumentException("Invalid color format: " + parts[0]);
                
        int r = Integer.parseInt(rgb[0].trim());
        int g = Integer.parseInt(rgb[1].trim());
        int b = Integer.parseInt(rgb[2].trim());
        
        this.background = new Color(r, g, b);

        this.maxSteps       = Integer.parseInt(parts[1].trim());
        this.maxDist        = Integer.parseInt(parts[2].trim());
        this.shadowSteps    = Integer.parseInt(parts[3].trim());
    }

}
