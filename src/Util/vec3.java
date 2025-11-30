package Util;

public class vec3 {
    
    public float x, y, z;

    /**
     * Default constructor. { 0.0 , 0.0 , 0.0 } .
     */
    public vec3() { x = 0.0f; y = 0.0f; z = 0.0f; }
    /**
     * Constructor with just x component { x, x, x } .
     * 
     * @param x The x component.
     */
    public vec3(float x) { this.x = x; y = x; z = x; }
    /**
     * Full constructor with x , y , & z components { x , y , z } .
     * 
     * @param x The x component.
     * @param y The y component.
     * @param z The z component.
     */
    public vec3(float x, float y, float z)  { this.x = x; this.y = y; this.z = z; }
    /**
     * Copy constructor.
     * @param copy The vec3 to be copied
     */
    public vec3(vec3 copy) {
        x = copy.x;
        y = copy.y;
        z = copy.z;
    }
    
    public vec3(java.awt.Color c) { x = c.getRed(); y = c.getGreen(); z = c.getBlue(); }
 
    /**
     * Component wise addition.
     * 
     * @param o The other vector to add.
     * @return A new vector equal to ( x + o.x , y + o.y , z + o.z ) .
     */
    public vec3 add(vec3 o) { 
        return new vec3
            (
                    x + o.x, 
                    y + o.y, 
                    z + o.z
            ); 
    }
    /**
     * Component wise addition.
     * 
     * @param p The first addend vector.
     * @param q The second addend vector.
     * @return A new vector equal to ( p.x + q.x , p.y + q.y , p.z + q.z ) .
     */
    public static vec3 add(vec3 p, vec3 q) { return p.add(q); }
    /**
     * Adds a scalar to each component.
     * 
     * @param f The scalar to add.
     * @return A new vector equal to ( x + f , y + f , z + f ) .
     */
    public vec3 add(float f) {
        return new vec3
            (
                    x + f,
                    y + f,
                    z + f
            );
    }
    /**
     * Adds a scalar to each component.
     * 
     * @param o The vector.
     * @param f The scalar.
     * @return A new vector equal to ( o.x + f , o.y + f , o.z f ) .
     */
    public static vec3 add(vec3 o, float f) { return o.add(f); }
    
    /**
     * Component wise subtraction.
     * 
     * @param o The subtrahend vector.
     * @return A new vector equal to ( x - o.x , y - o.y , z - o.z ) .
     */
    public vec3 subtract(vec3 o) { return this.add(o.scale(-1.0f)); }
    /**
     * Component wise subtraction.
     * 
     * @param p The minuend vector.
     * @param q The subtrahend vector. 
     * @return A new vector equal to ( p.x - q.x , p.y - q.y , p.z - q.z ) .
     */
    public static vec3 subtract(vec3 p, vec3 q) { return p.add(q.scale(-1.0f)); }
    /**
     * Subtracts a scalar from each component.
     * 
     * @param f The subtrahend scalar.
     * @return A new vector equal to ( x - f , y - f , z - f) .
     */
    public vec3 subtract(float f) { return this.add(-f); }
    /**
     * Subtracts a scalar from each component.
     * 
     * @param o The minuend vector.
     * @param f The subtrahend scalar.
     * @return A new vector equal to ( o.x - f , o.y - f , o.z - f ) .
     */
    public static vec3 subtract(vec3 o, float f) { return o.add(-f); }
    
    public vec3 scale(float p) { return new vec3(x * p, y * p, z * p); }
    public vec3 negate() { return this.scale(-1.0f); }
    public vec3 multiply(vec3 o) {
        return new vec3
                (
                        x * o.x,
                        y * o.y,
                        z * o.z
                );
    }
    
    public vec3 divide(float f) { return new vec3(x / f, y / f, z / f); }
    
    public vec3 modulo(float m) { return new vec3(x % m, y % m, z % m); }
    
    public float average() { return (x + y + z) / 3.0f; }
    
    public vec3 clamp(float l, float h) {
        float nx = Math.min(Math.max(x, l), h),
              ny = Math.min(Math.max(y, l), h),
              nz = Math.min(Math.max(z, l), h);
        return new vec3(nx, ny, nz);
    }
    
    public float getDist(vec3 other) { return (float)Math.sqrt(Math.pow(other.x - x, 2) + Math.pow(other.y - y, 2) + Math.pow(other.z - z, 2)); }
    
    public vec3 abs() { return new vec3(Math.abs(x), Math.abs(y), Math.abs(z)); }
    
    public float length() { return (float)Math.sqrt(x*x + y*y + z*z); }
    public float lengthSqr() { return x*x + y*y + z*z; }
    
    public vec3 max(vec3 other) { return new vec3(Math.max(this.x, other.x), Math.max(this.y, other.y), Math.max(this.z, other.z)); }
    public vec3 max(float q) {
        return new vec3(
                Math.max(x, q),
                Math.max(y, q),
                Math.max(z, q)
            );
    }
    
    public vec3 normalize(){
        float length = length();
        if (length == 0) { return new vec3(); }
        return new vec3(x / length, y / length, z / length);
    }
    
    public vec3 cross(vec3 other){
        return new vec3(y * other.z - z * other.y,
        z * other.x - x * other.z,
        x * other.y - y * other.x);
    }
    public static vec3 cross(vec3 p, vec3 q) {
        return new vec3(
            p.y * q.z - p.z * q.y,
            p.z * q.x - p.x * q.z,
            p.x * q.y - p.y * q.x
            );
    }
    
    public float dot(vec3 other) { return x * other.x + y * other.y + z * other.z; } 
    
    public static vec3 randomHemisphere(vec3 normal, vec3 pos) {
        float u = hash(pos, 1);   
        float v = hash(pos, 2);     
        
        float r = (float)Math.sqrt(u);
        float theta = 2.0f * (float)Math.PI * v;

        float x = r * (float)Math.cos(theta);
        float y = r * (float)Math.sin(theta);
        float z = (float)Math.sqrt(1.0f - u);

        vec3 tangent = normal.anyPerpendicular().normalize();
        vec3 bitangent = normal.cross(tangent).normalize();

        vec3 dir = tangent.scale(x)
                    .add(bitangent.scale(y))
                    .add(normal.scale(z));

        return dir.normalize();
    }
    private static float hash(vec3 p, float q) {
        float dot = p.x * 127.1f * q + p.y * 311.7f * q + p.z * 74.7f * q;
        return fract((float)Math.sin(dot) * 43758.5453f);
    }
    private static float fract(float x) {
        return x - (float)Math.floor(x);
    }
    private vec3 anyPerpendicular() {
        vec3 axis = (Math.abs(x) < 0.9f) ? new vec3(1,0,0) : new vec3(0,1,0);
        vec3 perp = cross(axis);
        return perp.normalize();
    }

    public static vec3 blend(vec3 p, vec3 q, float w) {
        //Clamp weight
        w = Math.min(w, 1.0f);
        w = Math.max(w, 0.0f);
        
        float nx = (1 - w) * p.x + w * q.x;
        float ny = (1 - w) * p.y + w * q.y;
        float nz = (1 - w) * p.z + w * q.z;
        return new vec3(nx, ny, nz); 
    }
    public vec3 blend(vec3 o, float w) {
        //Clamp the weight
        w = Math.min(w, 1.0f);
        w = Math.max(w, 0.0f);
        
        float nx = (1 - w) * x + w * o.x;
        float ny = (1 - w) * y + w * o.y;
        float nz = (1 - w) * z + w * o.z;
        return new vec3(nx, ny, nz); 
    }

    public java.awt.Color toAwtColor() { 
        vec3 clamped = this.clamp(0, 255);
        return new java.awt.Color((int) clamped.x, (int) clamped.y, (int) clamped.z);
    }
    
    public static vec3 round(vec3 other, int places) {
        int q = 1;
        for (int i = 0; places > i; i++) q *= 10;
        
        float nx = other.x, ny = other.y, nz = other.z;
        
        nx = Math.round(nx * q) / (float) q;
        ny = Math.round(ny * q) / (float) q;
        nz = Math.round(nz * q) / (float) q;
        
        return new vec3(nx, ny, nz);
    }
    public static vec3 round(vec3 o) { return round(o, 0); }
    public vec3 round(int places) {
        int q = 1;
        for (int i = 0; places > i; i++) q *= 10;
        
        float nx = x, ny = y, nz = z;
        
        nx = Math.round(nx * q) / (float) q;
        ny = Math.round(ny * q) / (float) q;
        nz = Math.round(nz * q) / (float) q;
        
        return new vec3(nx, ny, nz);
    }
    public vec3 round() { return this.round(0); }
    
    public boolean equals(vec3 o) { return x == o.x && y == o.y && z == o.z; }
    
    @Override
    public String toString() { return "(" + x + " : " + y + " : " + z + ")"; }
    public String[] toStringArray() { return new String[] { ""+x, ""+y, ""+z }; }
    /**
     * Turns the .toString() method back into a vec3 object
     * @param vector A String in the form {x:y:z} or (x, y, z)
     */
    public vec3(String vector) { this(vector.trim().replaceAll("[(){}]", "").split("[,\\:]")); }
    /**
     * Turns the .toStringArray method back into a vec3 object
     * @param comp A length 3 array of Strings that are floats
     */
    public vec3(String[] comp) {
        try {
            x = Float.parseFloat(comp[0].trim());
            y = Float.parseFloat(comp[1].trim());
            z = Float.parseFloat(comp[2].trim());
        } catch (NumberFormatException e) {
            System.err.println("Error parsing string for vec3 ...");
            System.err.println(e.getMessage());
        }
    }
    
    public final static vec3 WHITE = new vec3(255.0f);
    public final static vec3 GRAY  = new vec3(128.0f);

}
