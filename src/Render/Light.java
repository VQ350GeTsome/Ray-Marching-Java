package Render;

import Utility.vec3;

public class Light {

    private vec3 sceneLight = new vec3(0.0f, 0.0f, 1.0f),   //Default lighting
                 lightColor = new vec3(255.0f);
    private float ambientLight = 0.0f;
    
    public void setSceneLighting(vec3 l)    { sceneLight = l.normalize(); }
    public void setLightColor(vec3 c)       { lightColor = c; }
    public vec3 getSceneLighting()          { return sceneLight; }
    public vec3 getLightColor()             { return lightColor; }
    
    public void setAmbientLight(float l)    { ambientLight = l; }
    public float getAmbeintLight()          { return ambientLight; }
    
    public String packLight() {
        return  sceneLight.toString()   + "," +
                lightColor.toString()   + "," +
                ambientLight            + ",\n" ;
    }
    public void unpackLight(String[] parts) {
        sceneLight = new vec3(parts[0]);
        lightColor = new vec3(parts[1]);
        ambientLight = Float.parseFloat(parts[2]);
    }
    
}
