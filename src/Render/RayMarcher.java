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
                if (hit.sdf == null) { image[x][y] = background; continue; }
                image[x][y] = calculateColor(hit, dir);
            }
        });
        return image;
    }
    
    private vec3 calculateColor(HitInfo hit, vec3 dir, int depth) {
        SDF obj = hit.sdf;
        Material objMat = obj.getMaterial(hit.hit);     //Get the object we hits material and save it to objMat.
                
        float shadow = getShadow(hit.hit); //Get shadow amount
        
        //Calculate the normal here, so we can use it in multiple methods without having to recalculate it.
        vec3 norm = estimateNormal(obj, hit.hit);   

        //Get the diffused color & specular color
        vec3 diffusedColor = diffuse(obj, hit, norm).scale(shadow);                       
        vec3 specularColor = specular(norm, dir, objMat).scale(shadow);
        
        //Using recursion calculate the color of the reflected ray
        vec3 reflectionColor = (objMat.reflectivity > 0.01f && depth > 0) ? reflect(hit, norm, dir, objMat, depth) : diffusedColor;
        
        vec3 behindColor = (objMat.opacity < 1.0f && depth > 0) ? opacity() : background;
        if (objMat.opacity < 1.0f && depth > 0) {
            // Continue ray through the surface
            vec3 origin = hit.hit.add(dir.scale(Core.getEps() * 2.0f));
            HitInfo behindHit = marchRay(origin, dir);
            if (behindHit.sdf != null) {
                behindColor = calculateColor(behindHit, dir, depth - 1);
            }
        }
        
        vec3 finalColor = diffusedColor
                          .blend(reflectionColor, objMat.reflectivity)
                          .add(specularColor); //Add specular color last so you can see the light in reflections
        finalColor = finalColor.blend(behindColor, objMat.opacity);

        float fog = hit.totalDist / maxDist;           //Fog is the % distance to max distance ie if maxDist is 100 and the objs distance is 10 the fog is 10%
        fog = (float) Math.pow(fog, fogFalloff);                //We exponentiate fog by the falloff making a convex curve if fogFalloff > 1

        finalColor =  vec3.blend(finalColor, background, fog);
        return finalColor;
    }
    private vec3 calculateColor(HitInfo hit, vec3 dir) { return calculateColor(hit, dir, 4); }
    
    private vec3 opacity() {
        
    }
    
    private vec3 reflect(HitInfo hit, vec3 norm, vec3 dir, Material mat, int depth) {
        //Calculate the direction of a reflected ray
        vec3 reflected = dir.subtract(norm.scale(2.0f * dir.dot(norm))).normalize();

        //If the material is rough add a random vector seeded with the normal & hit position
        if (mat.roughness > 0.01f)
            reflected = reflected.add(vec3.randomHemisphere(norm, hit.hit).scale(customClamp(mat.roughness, 2))).normalize();

        //Start the ray a little off the surface
        vec3 origin = hit.hit.add(norm.scale(Core.getEps() * 2.0f));

        //March that reflected ray
        HitInfo reflectHit = marchRay(origin, reflected);

        //Use recursion if we hit an object
        if (reflectHit.sdf == null) return background;   
        else return calculateColor(reflectHit, reflected, depth - 1);
    }
        
    private vec3 diffuse(SDF obj, HitInfo hit, vec3 normal) {
        vec3 sceneLight = light.getSceneLighting().negate();   
        float brightness = Math.max(0.0f, normal.dot(sceneLight));
        vec3 finalColor = obj.getMaterial(hit.hit).color.scale(brightness);    
        return finalColor;
    }
    
    private float getShadow(vec3 orgin) {
        float ambientLight = light.getAmbeintLight();       //Get the scenes ambient lighting
        
        float lighting = 1.0f - ambientLight;               //Minus the starting light % by the ambient light, for it will be added back later ... 
        float t = 0.1f;                                     //Start slightly off the surface ... this must be greater than epsilon
        final float softness = 1.0f;                        //Penumbra width ... i think
        vec3 lightDir = light.getSceneLighting()
                        .negate();                          //Flip lighting around
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
    
    private vec3 specular(vec3 norm, vec3 viewDir, Material mat) {
        vec3 lightDir = light.getSceneLighting();//.negate();

        vec3 reflectDir = lightDir.negate()
                          .subtract(norm.scale(2.0f * lightDir.negate().dot(norm)))
                          .normalize();

        float specAngle = Math.max(0.0f, reflectDir.dot(viewDir));
        float shinyness = mat.shinyness;
        float spec = (float)Math.pow(specAngle, shinyness);

        // Use normalized white highlight
        vec3 highlightColor = mat.specularColor.blend(mat.color, mat.metalness);
        return highlightColor.scale(spec * mat.specular);
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
