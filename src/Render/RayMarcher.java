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
                return new HitInfo(hitPos, totalDistance, sdfMgr.getSDFAtPos(hitPos));
            }
            //If we weren't close enought to hit an object, march forward by the 
            //minimum distance we calculated earlier ( distance )
            pos = pos.add(dir.scale(distance));
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
    
    private HitInfo marchThrough(vec3 pos, vec3 dir) {
        if (sdfMgr.getClosestSDFDist(pos) >= 0f) return marchRay(pos, dir);
        float totalDistance = 0.0f;
        for (int step = 0; step < maxSteps && totalDistance <= maxDist; step++) {
            float d = sdfMgr.getClosestSDFDist(pos);
            if (d >= -Core.getEps()) break; // at/near boundary -> stop phase 1
            float advance = Math.max(Core.getEps() * 2.0f, -d);
            pos = pos.add(dir.scale(advance));
            totalDistance += advance;
        }
        HitInfo exitHit = new HitInfo(pos, totalDistance, sdfMgr.getSDFAtPos(pos));
        return exitHit;
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
        Material objMat = hit.mat;     //Get the object we hits material and save it to objMat.
                
        float shadow = getShadow(hit.hit); //Get shadow amount
        
        //Calculate the normal here, so we can use it in multiple methods without having to recalculate it.
        vec3 norm = estimateNormal(obj, hit.hit);   

        //Get the diffused color & specular color
        vec3 diffusedColor = diffuse(hit, norm).scale(shadow);                       
        vec3 specularColor = specular(norm, dir, objMat).scale(shadow);
        
        //Using recursion calculate the color of the reflected ray
        vec3 reflectionColor = (objMat.reflectivity > 0.01f && depth > 0) ? reflect(hit, norm, dir, depth) : diffusedColor;
        
        vec3 behindColor = (objMat.opacity < 1.0f && depth > 0) ? opacity(hit, norm, dir, depth) : background;
        
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
    
    private vec3 opacity(HitInfo entryHit, vec3 entryNorm, vec3 inDir, int depth) {
        if (depth <= 0) return entryHit.mat.color;

        entryNorm = entryNorm.normalize();
        inDir = inDir.normalize();
        Material mat = entryHit.mat;

        //Entry refraction (air to the obj material). If total internal reflection just reflect.
        vec3 refrEntry = refract(inDir, entryNorm, 1.0f, mat.ior);
        if (refrEntry == null) return reflect(entryHit, entryNorm, inDir, depth - 1);
        
        //Make sure the refraction entry direction is slightly inside the object.
        vec3 insideOrigin = entryHit.hit.add(refrEntry.scale(Core.getEps() * 2.0f));

        //March through the object to find where we will exit.
        HitInfo exitHit = marchThrough(insideOrigin, refrEntry);
        
        //Find the exit normal and attempt exit refraction (material back to air).
        vec3 exitNorm = estimateNormal(exitHit.sdf, exitHit.hit).normalize();
        vec3 refrExit = refract(refrEntry, exitNorm, mat.ior, 1.0f);
        if (refrExit == null) {
            // TIR inside: reflect inside and continue from just inside the surface
            vec3 insideReflectDir = refrEntry.subtract(exitNorm.scale(2.0f * refrEntry.dot(exitNorm))).normalize();
            vec3 reflectOrigin = exitHit.hit.add(insideReflectDir.scale(Core.getEps() * 2.0f));
            HitInfo reflectHit = marchRay(reflectOrigin, insideReflectDir);
            return (reflectHit.sdf == null) ? background : calculateColor(reflectHit, insideReflectDir, depth - 1);
        }

        //Step just outside the exit along refracted exit direction and continue the scene march
        vec3 afterExitOrigin = exitHit.hit.add(exitNorm.scale(Core.getEps() * 2.0f));
        HitInfo behind = marchRay(afterExitOrigin, refrExit);
        return (behind.sdf == null) ? background : calculateColor(behind, refrExit, depth - 1);
    }
   
    private vec3 refract(vec3 dir, vec3 norm, float etai, float etat) {
        dir = dir.normalize();
        norm = norm.normalize();

        float cosi = clamp(-dir.dot(norm), -1.0f, 1.0f);
        float eta = etai / etat;
        float k = 1.0f - eta * eta * (1.0f - cosi * cosi);
        if (k < 0.0f) return null; // TIR
        vec3 refr = dir.scale(eta).add(norm.scale(eta * cosi - (float)Math.sqrt(k)));
        return refr.normalize();
    }
    
    private vec3 reflect(HitInfo hit, vec3 norm, vec3 dir, int depth) {
        //Calculate the direction of a reflected ray
        vec3 reflected = dir.subtract(norm.scale(2.0f * dir.dot(norm))).normalize();

        //If the material is rough add a random vector seeded with the normal & hit position
        if (hit.mat.roughness > 0.01f)
            reflected = reflected.add(vec3.randomHemisphere(norm, hit.hit).scale(customClamp(hit.mat.roughness, 2))).normalize();

        //Start the ray a little off the surface
        vec3 origin = hit.hit.add(norm.scale(Core.getEps() * 2.0f));

        //March that reflected ray
        HitInfo reflectHit = marchRay(origin, reflected);

        //Use recursion if we hit an object
        if (reflectHit.sdf == null) return background;   
        else return calculateColor(reflectHit, reflected, depth - 1);
    }
        
    private vec3 diffuse(HitInfo hit, vec3 normal) {
        vec3 sceneLight = light.getSceneLighting().negate();   
        float brightness = Math.max(0.0f, normal.dot(sceneLight));
        vec3 finalColor = hit.mat.color.scale(brightness);    
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
    private float clamp(float v, float lo, float hi) {
        return v < lo ? lo : (v > hi ? hi : v);
    }
    private float fresnelSchlick(float cosTheta, float ior) {
        float r0 = (1.0f - ior) / (1.0f + ior);
        r0 = r0 * r0;
        return r0 + (1.0f - r0) * (float)Math.pow(1.0f - cosTheta, 5.0f);
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
