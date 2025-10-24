package Render;

import Utility.vec3;
import java.util.ArrayList;

public class Light {

    private vec3 sceneLight = new vec3(0.0f, 0.0f, 1.0f);   //Default lighting
    private float ambientLight = 0.0f;
    private ArrayList<vec3> lightSources;   /* Unemplemented */
    
    public void setSceneLighting(vec3 l)    { sceneLight = l; }
    public vec3 getSceneLighting()          { return sceneLight; }
    
    public void setAmbientLight(float l)    { ambientLight = l; }
    public float getAmbeintLight()          { return ambientLight; }
    
    public String packLight() {
        return  sceneLight.toString()   + "," +
                ambientLight            + "," ;
    }
    public void unpackLight(String[] parts) {
        sceneLight = new vec3(parts[0]);
        ambientLight = Float.parseFloat(parts[1]);
    }
    
}
