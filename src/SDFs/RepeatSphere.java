package SDFs;

import Utility.*;

public class RepeatSphere extends SDF{

    private vec3 center = new vec3(0.0f, 0.0f, 0.0f);
    private float radius, spacing;
    
    public RepeatSphere(float radius, float spacing, java.awt.Color color) {
        this.radius = radius; this.spacing = spacing;
        center = new vec3();
        material = new Material(color);
    }

    
    public float sdf(vec3 point) {
        vec3 worldP = point.subtract(center); // move into object space

        // Wrap each coordinate into a cell centered at 0
        float rx = ((worldP.x % spacing) + spacing) % spacing - spacing * 0.5f;
        float ry = ((worldP.y % spacing) + spacing) % spacing - spacing * 0.5f;
        float rz = ((worldP.z % spacing) + spacing) % spacing - spacing * 0.5f;

        vec3 local = new vec3(rx, ry, rz);
        return (local.length() - radius);
    }
    
    public String[] getSettingsAndCurrent() {
        return new String[] { "Color: ", "Center: ", "Radius: ", "Spacing: ",
            material.colorString(), center.toStringParen(), ""+radius, ""+spacing };
    }
    
    public String getType() { return "repeatsphere"; }
    
    
    @Override
    public String toString() {
        return super.toString() + center.toString() + "," + radius + ",\n";
    }
      
}
