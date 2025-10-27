package Utility;

public class vec2 {
    
    public float x;
    public float y;
    
    public vec2(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float length() {
        return (float) Math.sqrt(x*x + y*y);
    }
}
