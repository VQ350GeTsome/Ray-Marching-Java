package Render;

public final class Light {

    // Default scene lighting direction, and color.
    private Vectors.vec3 sceneLight = new Vectors.vec3(0.0f, 0.0f, 1.0f),  
                         lightColor = new Vectors.vec3(255.0f);
    private float ambientLight = 0.0f;
    
    public Light() {
        sceneLight = new Vectors.vec3(Core.WORLDUP);
    }
    
    public void setSceneLighting(Vectors.vec3 l) { sceneLight = l.normalize(); }
    public void setLightColor(Vectors.vec3 c) { lightColor = c; }
    public Vectors.vec3 getSceneLightingDirection() { return sceneLight; }
    public Vectors.vec3 getLightColor() { return lightColor; }
    
    public void setAmbientLight(float l) { ambientLight = l; }
    public float getAmbientLight() { return ambientLight; }
    
    public String packLight() {
        return  sceneLight.toString()   + "," +
                lightColor.toString()   + "," +
                ambientLight            + ",\n" ;
    }
    public void unpackLight(String[] parts) {
        sceneLight = new Vectors.vec3(parts[0]);
        lightColor = new Vectors.vec3(parts[1]);
        ambientLight = Float.parseFloat(parts[2]);
    }
    
}
