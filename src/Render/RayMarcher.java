package Render;

import SDFs.SDF;
import Utility.*;
import java.awt.Color;
import java.util.stream.IntStream;
import javax.swing.Timer;

public class RayMarcher {

    private Camera      camera;
    private Light       light;
    private SDFManager  sdfMgr;
    
    private Color background    = Color.BLACK;//new Color(135, 205, 235);
    public Color getBackground() { return background; }
    private int   maxSteps      = 1024,
                  maxDist       =  128,
                  shadowSteps   =   64,
                  fogFalloff    =    5;
    
    public RayMarcher(Camera camera, Light light, SDFManager sdfMgr) {
        this.camera     = camera;
        this.light      = light;
        this.sdfMgr     = sdfMgr;
    }
        
    public HitInfo marchRay(int x, int y, int w, int h) {
        float nx = (x + 0.5f) / (float) w;
        float ny = (y + 0.5f) / (float) h;
        vec3 dir = camera.getRayDirection(nx, ny, w / (float) h); 
        vec3 pos = camera.getOrientation()[3];
        
        float totalDistance = 0.0f;
        for (int step = 0; step < maxSteps; step++) {
            float distance = sdfMgr.getClosestSDFDist(pos);        //Minimum distance we can step

            if (distance < Core.getEps() || totalDistance > maxDist) {
                vec3 hitPos = pos.add(dir.multiply(distance));
                return new HitInfo(hitPos, totalDistance + distance, sdfMgr.getSDFAtPos(pos));
            }

            pos =   pos
                    .add(   dir
                            .multiply( distance ) 
                    );
            totalDistance += distance;
        }
        return new HitInfo(pos, totalDistance, sdfMgr.getSDFAtPos(pos));
    }
    
    public Color[][] marchScreen(int w, int h) {
        Color[][] image = new Color[w][h];                                 //2D array for image of size { width , height }
        
        IntStream.range(0, w).parallel().forEach(x -> {                     //Parellelize each x row and call the for loop for each y column
            for (int y = 0; h > y; y++) {                                   //This works each column out in parallel
                HitInfo hit = marchRay(x, y, w, h);                         //March ray from pixel { x , y } and get the point it hits
                SDF hitObj = hit.sdf;                                       //Check to see if their is an SDF where the ray hit
                if (hitObj == null) image[x][y] = background;               //If there is none, set the current pixel to the background color
                else {
                    Color color = shade(hitObj, hit);                       //Calculate the objects color depending on light
                    float fog       =  (hit.totalDist / maxDist);           //Fog is the % distance to max distance ie if maxDist is 100 and the objs distance is 10 the fog is 10%
                    fog = (float) Math.pow(fog, fogFalloff);                //We exponentiate fog by the falloff making a convex curve if fogFalloff > 1
                                                          
                    Color finalColor =  ColorMath
                                        .blend(color, background, fog);
                    image[x][y] = finalColor;
                }
            }
        });
        return image;
    }
    
    private Color shade(SDF obj, HitInfo hit) {
        float shadow = getShadow(hit.hit);              //Get shadow amount
        
        vec3 normal = estimateNormal(obj, hit.hit);     //Get the ~normal
        vec3 sceneLight = light.getSceneLighting().multiply(-1);    //Invert the scene lighting
        
        float brightness = Math.max(0.0f, normal.dot(sceneLight));
        brightness = customClamp(brightness, 5);

        
        Color finalColor = ColorMath.blend( obj.getMaterial(hit.hit).color , Color.WHITE, brightness );
              finalColor = ColorMath.scale( finalColor     , shadow );
              
        return finalColor;
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
    
    /**
     * A custom clamp for lighting.
     * @param f What will be exponentiated
     * @param n Hoe many times to multiply
     * @return ( ( f^n ) / ( f^n + 1 ) )
     */
    private float customClamp(float f, int n) {
        float result = (float) Math.pow(f, n);
        
        return result / (result + 1.0f);
    }
    
    /**
     * Estimates a normal based off of an SDF and a position.
     * @param obj   SDF that we are using to estimate
     * @param p     Point we are starting at
     * @return      A normalized vec3 that it about the normal
     */
    private vec3 estimateNormal(SDF obj, vec3 p) {
        float eps = Core.getEps();
        float x = obj.sdf(p.add(new vec3(eps, 0.0f, 0.0f))) - obj.sdf(p.subtract(new vec3(eps, 0.0f, 0.0f)));
        float y = obj.sdf(p.add(new vec3(0.0f, eps, 0.0f))) - obj.sdf(p.subtract(new vec3(0.0f, eps, 0.0f)));
        float z = obj.sdf(p.add(new vec3(0.0f, 0.0f, eps))) - obj.sdf(p.subtract(new vec3(0.0f, 0.0f, eps)));
        return new vec3(x, y, z).normalize();
    }
    
    public String packRayMarcher() {
        return  background.getRed() + ":" + background.getGreen() + ":" + background.getBlue() + "," +
                maxSteps    + "," + maxDist + "," +
                shadowSteps + "," + fogFalloff + ",\n" ;
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
        this.fogFalloff     = Integer.parseInt(parts[4].trim());
    }

}
