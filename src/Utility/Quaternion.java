package Utility;

public class Quaternion {
    
    public float w, x, y, z;

    public Quaternion() { w = 1; x = 0; y = 0; z = 0; }
    
    public Quaternion(float w, float x, float y, float z) {
        this.w = w; this.x = x; this.y = y; this.z = z;
    }

    public Quaternion normalize() {
        float l = length();
        return new Quaternion(w / l, x / l, y / l, z / l);
    }
    
    public float length() { return (float)Math.sqrt(w*w + x*x + y*y + z*z); }

    public Quaternion multiply(Quaternion q) {
        return new Quaternion(
            w*q.w - x*q.x - y*q.y - z*q.z,
            w*q.x + x*q.w + y*q.z - z*q.y,
            w*q.y - x*q.z + y*q.w + z*q.x,
            w*q.z + x*q.y - y*q.x + z*q.w
        );
    }
    public Quaternion scaleImag(float f) { return new Quaternion(w, x*f, y*f, z*f); }

    public Quaternion conjugate() { return new Quaternion(w, -x, -y, -z); }

    public vec3 rotate(vec3 v) {
        Quaternion q = normalize();
        Quaternion qv = new Quaternion(0, v.x, v.y, v.z);
        Quaternion res = q.multiply(qv).multiply(q.conjugate());
        return new vec3(res.x, res.y, res.z);
    }
    
    @Override
    public String toString() { return "{" + w + ":" + x + ":" + y + ":" + z + "}"; }
    public Quaternion(String s) {
        s = s.trim().substring(1, s.length() - 2);   //Trim off grouping character
        String[] comp = s.split("[,\\:]");           //Split by , or :
        
        if (comp.length != 4) {
            throw new IllegalArgumentException("Invalid Quaternion format: " + s);
        }
                
        // Parse and assign
        this.w = Float.parseFloat(comp[0].trim());
        this.x = Float.parseFloat(comp[1].trim());
        this.y = Float.parseFloat(comp[2].trim());
        this.z = Float.parseFloat(comp[3].trim());
    }
    public Quaternion(String[] s) {
        this.w = Float.parseFloat(s[0].trim());
        this.x = Float.parseFloat(s[1].trim());
        this.y = Float.parseFloat(s[2].trim());
        this.z = Float.parseFloat(s[3].trim());
    }
    public String toStringImag() { return "{" + x + ":" + y + ":" + z + "}"; }
}