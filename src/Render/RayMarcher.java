package Render;

import Util.HitInfo;
import Util.vec3;
import Util.Material;
import java.util.stream.IntStream;

public class RayMarcher {

    private final Camera      camera;
    private final Light       light;
    private final SDFManager  sdfMgr;
    
    public vec3 background      = new vec3(),
                bgSecondary     = new vec3(255.0f);
    
    public int   maxSteps       = 1024,
                  maxDist       =  128,
                  shadowSteps   =   64,
                  fogFalloff    =    5,
                  maxDepth      =    3;
    
    public float shadowAmount      = 0.66f,
                 skyboxLightAmount = 25.0f;
    
    public boolean seeLight = true,
                   gradient = false,
                   gradUseZ = true;
    
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
    
    private HitInfo marchThrough(vec3 pos, vec3 dir) {
        if (sdfMgr.getClosestSDFDist(pos) >= 0f) return marchRay(pos, dir);
        float totalDist = 0.0f;
        for (int i = 0; maxSteps > i && maxDist > totalDist; i++) {
            float d = sdfMgr.getClosestSDFDist(pos); //Get the nearest surface
            if (d > Core.getEps()) break;
            pos = pos.add(dir.scale(-d));
            totalDist += -d;
        }
        return new HitInfo(pos, totalDist, sdfMgr.getSDFAtPos(pos));
    } 
    
    public HitInfo marchRaySkipCam(vec3 pos, vec3 dir) {
        float totalDistance = 0.0f;
        for (int step = 0; step < maxSteps; step++) {
            float distance = sdfMgr.getClosestSDFDistSkipCam(pos);        
            if (distance < Core.getEps() || totalDistance > maxDist) {
                vec3 hitPos = pos.add(dir.scale(distance));
                totalDistance += distance;
                return new HitInfo(hitPos, totalDistance, sdfMgr.getSDFAtPos(hitPos));
            }

            pos = pos.add(dir.scale(distance));
            totalDistance += distance;
        }
        return new HitInfo(pos, totalDistance, sdfMgr.getSDFAtPos(pos));
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
                HitInfo hit = marchRaySkipCam(pos, dir);                                                          
                if (hit.sdf == null) 
                    image[x][y] = calcBackground(dir);
                else 
                    image[x][y] = calcColor(hit, dir, maxDepth);
            }
        });
        return image;
    }

    private vec3 calcBackground(vec3 dir) {
        if (!gradient && !seeLight) return background;
        
        vec3 bg = background;
        
        if (gradUseZ && gradient)
            bg = bg.blend(bgSecondary, 0.50f - (dir.z / 2.0f));
        else if (gradient)
            bg = bg.blend(bgSecondary, 0.50f + (dir.dot(light.getSceneLighting()) / 2.0f));
        
        if (seeLight) {
            float k = Math.max(0.0f, dir.dot(light.getSceneLighting().negate()));
            k = (float) Math.pow(k, (1000.0f / skyboxLightAmount));
            bg = bg.add(light.getLightColor().scale(k));
        }
        
        return bg;
    }
    
    private vec3 calcColor(HitInfo hit, vec3 dir, int depth) {
        Material objMat = hit.mat;     //Get the object we hits material and save it to objMat.
                
        float shadow = getShadow(hit.hit); //Get shadow amount
        
        //Calculate the normal here, so we can use it in multiple methods without having to recalculate it.
        vec3 norm = estimateNormal(hit);   
        
        vec3 baseColor = diffuse(hit, norm)
                         .add(specular(norm, dir, objMat))
                         .scale(shadow);
        
        //Using recursion calculate the color of the reflected ray & the transmitted ray
        vec3 reflectColor = (objMat.reflectivity > 0.01f && depth > 0) ? reflect(hit, norm, dir, depth).scale(objMat.reflectivity) : new vec3();
        vec3 transmitColor = (objMat.opacity > 0.01f && depth > 0) ? opacity(hit, norm, dir, depth).scale(objMat.opacity) : new vec3();
        
        vec3 finalColor = baseColor.blend(reflectColor.add(transmitColor), Math.max(objMat.reflectivity, objMat.opacity) );

        float fog = hit.totalDist / maxDist;           //Fog is the % distance to max distance ie if maxDist is 100 and the objs distance is 10 the fog is 10%
        fog = (float) Math.pow(fog, fogFalloff);       //We exponentiate fog by the falloff making a convex curve if fogFalloff > 1

        return finalColor.blend(calcBackground(dir), fog);
    }
    
    private vec3 opacity(HitInfo entryHit, vec3 norm, vec3 inDir, int depth) {
        if (depth == 0) return entryHit.mat.color;
        
        inDir = inDir.normalize();
        Material mat = entryHit.mat;
        
        vec3 refractDirIn = refract(inDir, norm, 1.0f, mat.ior);
        if (refractDirIn == null) return reflect(entryHit, norm, inDir, depth - 1);

        vec3 surface = entryHit.hit.add(norm.scale(-Core.getEps()));
        HitInfo exitHit = marchThrough(surface, refractDirIn);
        
        if (exitHit.sdf == null) return calcBackground(refractDirIn);
        
        vec3 exitNorm = estimateNormal(exitHit);
        
        vec3 surfaceExitPos = exitHit.hit.add(exitNorm.scale(Core.getEps()*2.0f));
 
        vec3 refractDirExit = refract(refractDirIn.negate(), exitNorm, mat.ior, 1.0f);
        if (refractDirExit == null) {
            vec3 insideReflectDir = refractDirIn.subtract(exitNorm.scale(refractDirIn.dot(exitNorm.negate())*2.0f)).normalize();
            HitInfo reflectHit = marchRay(surfaceExitPos, insideReflectDir);
            return (reflectHit.sdf == null) ? calcBackground(insideReflectDir) 
                                            : calcColor(reflectHit, insideReflectDir, depth - 1);
        }

        refractDirExit = refractDirExit.negate();
        HitInfo behind = marchRay(surfaceExitPos, refractDirExit);
        return (behind.sdf == null) ? calcBackground(refractDirExit) : calcColor(behind, refractDirExit, depth - 1);
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
        vec3 reflectDir = dir.subtract(norm.scale(2.0f * dir.dot(norm))).normalize();

        //If the material is rough add a random vector seeded with the normal & hit position
        if (hit.mat.roughness > 0.01f)
            reflectDir = reflectDir.add(randomHemisphere(norm, hit.hit).scale(customClamp(hit.mat.roughness, 2))).normalize();

        //Start the ray a little off the surface
        vec3 origin = hit.hit.add(norm.scale(Core.getEps() * 2.0f));

        //March that reflected ray
        HitInfo reflectHit = marchRay(origin, reflectDir);

        //Use recursion if we hit an object
        if (reflectHit.sdf == null) return calcBackground(reflectDir);   
        else return calcColor(reflectHit, reflectDir, depth - 1);
    }
        
    private vec3 diffuse(HitInfo hit, vec3 normal) {
        vec3 sceneLight = light.getSceneLighting().negate();   
        float brightness = Math.max(0.0f, normal.dot(sceneLight));
        vec3 finalColor = hit.mat.color.scale(brightness);    
        return finalColor;
    }
    
    private float getShadow(vec3 orgin) {
        float ambientLight = light.getAmbientLight();           //Get the scenes ambient lighting
        float lighting = 1.0f - ambientLight;                   //Minus the starting light % by the ambient light, for it will be added back later ... 
        float t = 0.10f;                                        //Start slightly off the surface ... this must be greater than epsilon
        final float softness = 1 / shadowAmount;                //Penumbra width ... i think
        vec3 lightDir = light.getSceneLighting().negate();      //Flip lighting around
        float accumOpac = 1.0f;
        
        for (int i = 0; shadowSteps > i; i++) {
            vec3 pos = orgin.add(lightDir.scale(t));
            float minDist = sdfMgr.getClosestSDFDist(pos);
            
            if (minDist < Core.getEps()) {                      //If we hit an object.
                HitInfo info = sdfMgr.getNearestSDFAtPos(pos);  //Get the obj we hit.
                float objOpac = info.mat.opacity;               //As well as its opacity.
                
                if (objOpac < 0) return ambientLight;           //If we hit a solid object return the ambient light
                
                accumOpac *= objOpac;
                
                HitInfo mt = marchThrough(pos.add(lightDir.scale(Core.getEps() * 2.0f)), lightDir);
                orgin = mt.hit;
                orgin = orgin.add(lightDir.scale(Core.getEps() * 2.0f));
                t += mt.totalDist;
            }
            lighting = Math.min(lighting, softness * minDist / t);
            t += minDist;
            if (lighting > maxDist) break; 
        }
        return Math.min(1.0f, ambientLight + lighting*accumOpac);
    }
    
    private vec3 specular(vec3 norm, vec3 viewDir, Material mat) {
        vec3 lightDir = light.getSceneLighting();//.negate();

        vec3 reflectDir = lightDir.negate()
                          .subtract(norm.scale(2.0f * lightDir.negate().dot(norm)))
                          .normalize();

        float specAngle = Math.max(0.0f, reflectDir.dot(viewDir));
        float shinyness = mat.shinyness;
        float spec = (float)Math.pow(specAngle, shinyness);

        vec3 highlightColor = mat.specularColor.blend(mat.color, mat.metalness);
        highlightColor = light.getLightColor().blend(highlightColor, 0.50f);        //Add a little bit of color from the lights color
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
    private float clamp(float v, float lo, float hi) { return v < lo ? lo : (v > hi ? hi : v); }
    private float fract(float x) { return x - (float)Math.floor(x); }
    
    /**
     * Estimates a normal based off of an SDF and a position.
     * @param obj   SDF that we are using to estimate
     * @param p     Point we are starting at
     * @return      A normalized vec3 that it about the normal
     */
    private vec3 estimateNormal(HitInfo i) {
        float e = Core.getEps();
        float x = i.sdf.sdf(i.hit.add(new vec3(e, 0.0f, 0.0f))) - i.sdf.sdf(i.hit.subtract(new vec3(e, 0.0f, 0.0f)));
        float y = i.sdf.sdf(i.hit.add(new vec3(0.0f, e, 0.0f))) - i.sdf.sdf(i.hit.subtract(new vec3(0.0f, e, 0.0f)));
        float z = i.sdf.sdf(i.hit.add(new vec3(0.0f, 0.0f, e))) - i.sdf.sdf(i.hit.subtract(new vec3(0.0f, 0.0f, e)));
        return new vec3(x, y, z).normalize();
    }
    
    public vec3 randomHemisphere(vec3 normal, vec3 pos) {
        float u = hash(pos, 1);   
        float v = hash(pos, 2);     
        
        float r = (float)Math.sqrt(u);
        float theta = 2.0f * (float)Math.PI * v;

        float x = r * (float)Math.cos(theta);
        float y = r * (float)Math.sin(theta);
        float z = (float)Math.sqrt(1.0f - u);

        vec3 tangent = anyPerpendicular(normal).normalize();
        vec3 bitangent = normal.cross(tangent).normalize();

        vec3 dir = tangent.scale(x)
                    .add(bitangent.scale(y))
                    .add(normal.scale(z));

        return dir.normalize();
    }
    private float hash(vec3 p, float q) {
        float dot = p.x * 127.1f * q + p.y * 311.7f * q + p.z * 74.7f * q;
        return fract((float)Math.sin(dot) * 43758.5453f);
    }
    private vec3 anyPerpendicular(vec3 a) {
        vec3 axis = (Math.abs(a.x) < 0.9f) ? new vec3(1,0,0) : new vec3(0,1,0);
        vec3 perp = a.cross(axis);
        return perp.normalize();
    }
    
    public String packRayMarcher() {
        return  (int) background.x + ":" + (int) background.y + ":" + (int) background.z            + "," +
                (int) bgSecondary.x + ":" + (int) bgSecondary.y + ":" + (int) bgSecondary.z         + "," +
                maxSteps    + "," + maxDist + "," + shadowSteps + "," + fogFalloff + "," + maxDepth + "," +
                shadowAmount + "," + skyboxLightAmount + "," + ((seeLight) ? 1 : 0)                 + "," + 
                ((gradient) ? 1 : 0) + "," + ((gradUseZ) ? 1 : 0)
                
                + ",\n";
    }
    public void unpackRayMarcher(String[] parts) {
        int i = 0;
        
        background  = new vec3(parts[i++].split(":"));
        bgSecondary = new vec3(parts[i++].split(":"));

        maxSteps       = Integer.parseInt(parts[i++].trim());
        maxDist        = Integer.parseInt(parts[i++].trim());
        shadowSteps    = Integer.parseInt(parts[i++].trim());
        fogFalloff     = Integer.parseInt(parts[i++].trim());
        maxDepth       = Integer.parseInt(parts[i++].trim());
        
        shadowAmount      = Float.parseFloat(parts[i++].trim());
        skyboxLightAmount = Float.parseFloat(parts[i++].trim());
        
        seeLight = parts[i++].equals("1");
        gradient = parts[i++].equals("1");
        gradUseZ = parts[i++].equals("1");
        
    }

}
