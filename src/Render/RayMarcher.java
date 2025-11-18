package Render;

import SDFs.SDF;
import Utility.*;
import java.util.stream.IntStream;

public class RayMarcher {

    private Camera      camera;
    private Light       light;
    private SDFManager  sdfMgr;
    
    private vec3 background    = new vec3(0.0f);
    public vec3 getBackground() { return background; }
    private int   maxSteps      = 1024,
                  maxDist       =  128,
                  shadowSteps   =   64,
                  fogFalloff    =    5;
    
    public RayMarcher(Camera camera, Light light, SDFManager sdfMgr) {
        this.camera     = camera;
        this.light      = light;
        this.sdfMgr     = sdfMgr;
    }
    
    public void setMarchParams(String[] params) {
        try {
            maxSteps = Integer.parseInt(params[0]);
            maxDist  = Integer.parseInt(params[1]);
            shadowSteps = Integer.parseInt(params[2]);
        } catch (NumberFormatException e) { 
            System.err.println("Error parsing new March Parameters ... ");
            System.err.println(e.getMessage()); 
        }
    }
    public String[] getMarchParams() { return new String[] { ""+maxSteps, ""+maxDist, ""+shadowSteps }; }
        
    /**
     * Marches a ray starting at pos in the direction ( dir )
     * until it hits something, or we take maxStep steps.
     * @param pos The starting position of the ray.
     * @param dir The direction it'll march.
     * @return The position we stopped at
     * ( collision or maxDist ), the distance traveled,
     * and the object hit ( SDF ) if any. All that 
     * packed into a HitInfo object.
     */
    public HitInfo marchRay(vec3 pos, vec3 dir) {
        float totalDistance = 0.0f;
        for (int step = 0; step < maxSteps; step++) {
            //Get the closest SDFs distance
            float distance = sdfMgr.getClosestSDFDist(pos);        
            //If the distance is less than our epsilon, or we go past our max distance,
            //increment the hit position & total distance, get the SDF at the hit pos,
            //if any, and return all that in a HitInfo object.
            if (distance < Core.getEps() || totalDistance > maxDist) {
                vec3 hitPos = pos.add(dir.scale(distance));
                totalDistance += distance;
                return new HitInfo(hitPos, totalDistance, sdfMgr.getSDFAtPos(pos));
            }
            //If we weren't close enought to hit an object, march forward by the 
            //minimum distance we calculated earlier ( distance )
            pos =   pos
                    .add(   dir
                            .scale( distance ) 
                    );
            totalDistance += distance;
        }
        return new HitInfo(pos, totalDistance, sdfMgr.getSDFAtPos(pos));
    }
    
    /**
     * Marches a ray, but instead of feeding the method a 
     * starting position, and direction. We give it the x & y 
     * on the screen, and the width & height to then calculate
     * the direction, and the position is the camera position.
     * @param x on the screen.
     * @param y on the screen.
     * @param w Width of the screen.
     * @param h Height of the screen.
     * @return marchRay( camera position , calculated direction )
     */
    public HitInfo marchRay(int x, int y, int w, int h) {
        float nx = (x + 0.5f) / (float) w;
        float ny = (y + 0.5f) / (float) h;
        vec3 pos = camera.getOrientation()[3];
        vec3 dir = camera.getRayDirection(nx, ny, w / (float) h); 
        
        return marchRay(pos, dir);
    }
    
    public vec3[][] marchScreen(int w, int h) {
        vec3[][] image = new vec3[w][h];                                 //2D array for image of size { width , height }
        
        IntStream.range(0, w).parallel().forEach(x -> {                     //Parellelize each x row and call the for loop for each y column
            for (int y = 0; h > y; y++) {                                   //This works each column out in parallel
                
                float nx = (x + 0.5f) / (float) w;
                float ny = (y + 0.5f) / (float) h;
                vec3 pos = camera.getOrientation()[3];
                vec3 dir = camera.getRayDirection(nx, ny, w / (float) h); 
                                
                /*
                March a ray in the direction, from the camera position. If we don't hit
                an object just set the pixels color to the background, else calculate the 
                color at that pixel depending on the object & its material.
                */
                HitInfo hit = marchRay(pos, dir);                     
                SDF obj = hit.sdf;                                       
                if (obj == null) { image[x][y] = background; continue; }
                
                Material objMat = obj.getMaterial(pos);     //Get the object we hits material and save it to objMat.
                
                //Calculate the normal here, so we can use it in multiple methods without having to recalculate it.
                vec3 norm = estimateNormal(obj, hit.hit);   
                
                vec3 diffusedColor = diffuse(obj, hit, norm);                       
                vec3 reflectionColor = (objMat.reflectivity > 0) ? reflect(hit, dir, norm, 3) : diffusedColor;
                
                vec3 finalColor = vec3.blend(diffusedColor, reflectionColor , objMat.reflectivity);

                float fog = hit.totalDist / maxDist;           //Fog is the % distance to max distance ie if maxDist is 100 and the objs distance is 10 the fog is 10%
                fog = (float) Math.pow(fog, fogFalloff);                //We exponentiate fog by the falloff making a convex curve if fogFalloff > 1

                finalColor =  vec3.blend(finalColor, background, fog);
                image[x][y] = finalColor;
            }
        });
        return image;
    }
    
    private vec3 reflect(HitInfo hit, vec3 dir, vec3 normal, int depth) {
        if (depth <= 0) return background;
        
        //Use the normal we already calculated in marchScreen() to calculate the reflected ray.
        vec3 reflected = dir.subtract(normal.scale(2.0f * dir.dot(normal))).normalize();
        vec3 origin = hit.hit.add(normal.scale(Core.getEps() * 1.25f)); 
        
        HitInfo info = marchRay(origin, reflected);
        if (info.sdf == null) return background;
                
        vec3 diffusedColor = diffuse(info.sdf, info, normal);
        vec3 recursiveReflection = reflect(info, reflected, normal, depth - 1);
        
        float reflectivity = info.sdf.getMaterial(info.hit).reflectivity;
        vec3 combined = vec3.blend(diffusedColor, recursiveReflection, reflectivity);

        float fog = (info.totalDist / maxDist);
        fog = (float) Math.pow(fog, fogFalloff);
        
        return vec3.blend(combined, background, fog);
    }
    
    private vec3 diffuse(SDF obj, HitInfo hit, vec3 normal) {
        float shadow = getShadow(hit.hit); //Get shadow amount
         
        vec3 sceneLight = light.getSceneLighting().negate();   
        
        float brightness = Math.max(0.0f, normal.dot(sceneLight));
        brightness = customClamp(brightness, 5);

        vec3 finalColor = vec3.blend(obj.getMaterial(hit.hit).color , new vec3(255.0f), brightness);
             finalColor = finalColor.scale(shadow);
              
        return finalColor;
    }
    
    public float getShadow(vec3 orgin) {
        float ambientLight = light.getAmbeintLight();       //Get the scenes ambient lighting
        
        float lighting = 1.0f - ambientLight;               //Minus the starting light % by the ambient light, for it will be added back later ... 
        float t = 0.1f;                                     //Start slightly off the surface ... this must be greater than epsilon
        final float softness = 1.0f;                        //Penumbra width ... i think
        vec3 lightDir = light.getSceneLighting()
                        .scale(-1.0f);                   //Flip lighting around
        for (int i = 0; shadowSteps > i; i++) {
            vec3 pos =  orgin
                        .add(   lightDir 
                                .scale(t)
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
     * @param n Hoe many times to scale
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
        float e = Core.getEps();
        float x = obj.sdf(p.add(new vec3(e, 0.0f, 0.0f))) - obj.sdf(p.subtract(new vec3(e, 0.0f, 0.0f)));
        float y = obj.sdf(p.add(new vec3(0.0f, e, 0.0f))) - obj.sdf(p.subtract(new vec3(0.0f, e, 0.0f)));
        float z = obj.sdf(p.add(new vec3(0.0f, 0.0f, e))) - obj.sdf(p.subtract(new vec3(0.0f, 0.0f, e)));
        return new vec3(x, y, z).normalize();
    }
    
    public String packRayMarcher() {
        return  (int) background.x + ":" + (int) background.y + ":" + (int) background.z + "," +
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
        
        this.background = new vec3(r, g, b);

        this.maxSteps       = Integer.parseInt(parts[1].trim());
        this.maxDist        = Integer.parseInt(parts[2].trim());
        this.shadowSteps    = Integer.parseInt(parts[3].trim());
        this.fogFalloff     = Integer.parseInt(parts[4].trim());
    }

}
