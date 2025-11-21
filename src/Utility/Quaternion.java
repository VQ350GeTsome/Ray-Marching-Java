package Utility;

public class Quaternion {
    public float w, x, y, z;

    public Quaternion(float w, float x, float y, float z) {
        this.w = w; this.x = x; this.y = y; this.z = z;
    }

    public Quaternion normalize() {
        float l = length();
        return new Quaternion(w/l, x/l, y/l, z/l);
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

    public Quaternion conjugate() { return new Quaternion(w, -x, -y, -z); }

    public vec3 rotate(vec3 v) {
        Quaternion qv = new Quaternion(0, v.x, v.y, v.z);
        Quaternion res = this.multiply(qv).multiply(this.conjugate());
        return new vec3(res.x, res.y, res.z);
    }
}