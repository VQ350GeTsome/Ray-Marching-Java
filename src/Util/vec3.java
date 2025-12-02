package Util;

/**
 * A three component vector class, with many common operations & operators.
 * 
 * @author Harrison
 */
public class vec3 {
    
    //The three components
    public float x, y, z;

    //<editor-fold defaultstate="collapsed" desc=" Constructors ">
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
    /**
     * From java.awt.Color constructor. { r , g , b }
     * 
     * @param c a java.awt.Color that'll be used to create a new vec3
     */
    public vec3(java.awt.Color c) { x = c.getRed(); y = c.getGreen(); z = c.getBlue(); }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc=" 4-Function Scalar Operators (Object & Static) ">
    //Object methods
    /**
     * Adds a scalar to each component.
     * 
     * @param f The scalar to add.
     * @return A new vector equal to ( x + f , y + f , z + f ) .
     */
    public vec3 add(float f) { return new vec3 ( x + f , y + f , z + f ); }
    /**
     * Subtracts a scalar from each component.
     * 
     * @param f The subtrahend scalar.
     * @return A new vector equal to ( x - f , y - f , z - f) .
     */
    public vec3 subtract(float f) { return this.add( -f ); }
    /**
     * Scales ( multiplies ) each component by a scalar.
     * 
     * @param f The scalar to scale
     * @return A new vector equal to ( x * f , y * f , z * f )
     */
    public vec3 scale(float f) { return new vec3( x * f , y * f , z * f ); }
    /**
     * Divides each component by a scalar.
     * 
     * @param f
     * @return 
     */
    public vec3 divide(float f) { return this.scale( 1.0f / f ); }
    
    //Static methods
    /**
     * Adds a scalar to each component.
     * 
     * @param o The vector.
     * @param f The scalar.
     * @return A new vector equal to ( o.x + f , o.y + f , o.z f ) .
     */
    public static vec3 add(vec3 o, float f) { return o.add(f); }
    /**
     * Subtracts a scalar from each component.
     * 
     * @param o The minuend vector.
     * @param f The subtrahend scalar.
     * @return A new vector equal to ( o.x - f , o.y - f , o.z - f ) .
     */
    public static vec3 subtract(vec3 o, float f) { return o.add(-f); }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc=" 4-Function Vector Operators (Object & Static) ">
    //Object methods
    /**
     * Component wise addition.
     * 
     * @param o The other vector to add.
     * @return A new vector equal to ( x + o.x , y + o.y , z + o.z ) .
     */
    public vec3 add(vec3 o) { return new vec3( x + o.x , y + o.y , z + o.z ); }
    /**
     * Component wise subtraction.
     * 
     * @param o The subtrahend vector.
     * @return A new vector equal to ( x - o.x , y - o.y , z - o.z ) .
     */
    public vec3 subtract(vec3 o) { return this.add(o.scale(-1.0f)); }
    /**
     * Multiplies a vector with another. ( Per-component / Hadamard product )
     * 
     * @param o The multiplicator vector.
     * @return A new vector equal to ( x * o.x , y * o.y , z * z.y ) .
     */
    public vec3 multiply(vec3 o) { return new vec3( x * o.x , y * o.y , z * o.z ); }
    /**
     * Divideds a vector by another ( Per-component / Hadamard quotient )
     * 
     * @param o The dividend.
     * @return A new vector equal to ( x / o.x , y / o.y , z / o.z )
     */
    public vec3 divide(vec3 o) { return new vec3( x / o.x , y / o.y , z / o.z ); }
    
    //Static methods
    /**
     * Component wise addition.
     * 
     * @param p The first addend vector.
     * @param q The second addend vector.
     * @return A new vector equal to ( p.x + q.x , p.y + q.y , p.z + q.z ) .
     */
    public static vec3 add(vec3 p, vec3 q) { return p.add(q); }
    /**
     * Component wise subtraction.
     * 
     * @param p The minuend vector.
     * @param q The subtrahend vector. 
     * @return A new vector equal to ( p.x - q.x , p.y - q.y , p.z - q.z ) .
     */
    public static vec3 subtract(vec3 p, vec3 q) { return p.add(q.scale(-1.0f)); }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc=" Special Scalar Operators ">
    /**
     * Component-wise max ( Java's % operator ) .
     * 
     * @param m The dividend.
     * @return A new vector that is equal to ( x % m , y % m , z % m ) .
     */
    public vec3 remainder(float m) { return new vec3(x % m, y % m, z % m); }
    /**
     * Component-wise mathematical modulus ( Always positive ) .
     * 
     * @param m The dividend.
     * @return A new vector equal to ( x mod m , y mod m , z mod m ) .
     */
    public vec3 modulus(float m) { 
        return new vec3(
                ((x % m) + m) % m,
                ((y % m) + m) % m,
                ((z % m) + m) % m
        );
    }
    /**
     * Per-component maximum operation.
     * 
     * @param f The scalar.
     * @return A new vector that's equal to ( max(x , f) , max(y , f) , max(z , f) ) .
     */
    public vec3 max(float f) { return new vec3( Math.max(x, f) , Math.max(y, f) , Math.max(z, f) ); }
    /**
     * Per-component minimum operation.
     * 
     * @param f The scalar.
     * @return A new vector that's equal to ( min(x , f) , min(y , f) , min(z , f) ) .
     */
    public vec3 min (float f) { return new vec3( Math.min(x, f) , Math.min(y, f) , Math.min(z, f) ); }
    /**
     * Per-component clamping operation.
     * Throws an error is h is less than l.
     * 
     * @param l The lowest allowed value.
     * @param h The highest allowed value.
     * @return A new vector where each component is within [l, h].
     */
    public vec3 clamp(float l, float h) { 
        if (h < l) throw new ArithmeticException("Highest allowed value cannot be less than the lowest allowed value ... " + h + " < " + l);
        return this.max(l).min(h); 
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc=" Special Vector Operators ">
    //Object methods
    /**
     * Per-component maximum against another vector.
     * 
     * @param o The other vector whose components will be used.
     * @return A new vector that's equal to ( max(x , o.x) , max(y , o.y) , max(z , o.z) ) .
     */
    public vec3 max(vec3 o) { return new vec3(Math.max(x, o.x), Math.max(y, o.y), Math.max(z, o.z)); }
    /**
     * Per-component minimum against another vector.
     * 
     * @param o The other vector whose components will be used.
     * @return A new vector that's equal to ( min(x , o.x) , min(y , o.y) , min(z , o.z) ) .
     */
    public vec3 min(vec3 o) { return new vec3(Math.min(x, o.x), Math.min(y, o.y), Math.min(z, o.z)); }
    /**
     * Per-component clamping operation.
     * Throws an error if any component of h is less 
     * than the corresponding component of l. For example
     * if you try to clamp some vector in between [ (1, 2, 3) , (0 , 3, 5) ]
     * this will throw an error because l.x > h.x .
     * 
     * @param l The other vector whose components will be used for the low.
     * @param h The other vector whose components will be used for the high.
     * @return A new vector where each component is within [l, h].
     */
    public vec3 clamp(vec3 l, vec3 h) {
        if (h.x < l.x) throw new ArithmeticException("Highest allowed value cannot be less than the lowest allowed value... " + h.x + " < " + l.x);
        if (h.y < l.y) throw new ArithmeticException("Highest allowed value cannot be less than the lowest allowed value... " + h.y + " < " + l.y);
        if (h.z < l.z) throw new ArithmeticException("Highest allowed value cannot be less than the lowest allowed value... " + h.z + " < " + l.z);
        return this.max(l).min(h);
    }
    
    /**
     * Calculates the cross product between this vector and another.
     * 
     * @param o The other vector
     * @return A new vector orthogonal to both this and the other vector (o)
     */
    public vec3 cross(vec3 o){
        return new vec3(
                y * o.z - z * o.y,
                z * o.x - x * o.z,
                x * o.y - y * o.x
        );
    }
    /**
     * Calculates the dot product of this vector and another.
     * 
     * @param o The other vector.
     * @return Returns a float that is equal to ( x * o.x + y * o.y + z * o.z ) .
     */
    public float dot(vec3 o) { return x * o.x + y * o.y + z * o.z; } 
    
    public vec3 blend(vec3 o, float w) {
        //Clamp the weight
        w = Math.min(w, 1.0f);
        w = Math.max(w, 0.0f);
        
        float nx = (1 - w) * x + w * o.x;
        float ny = (1 - w) * y + w * o.y;
        float nz = (1 - w) * z + w * o.z;
        return new vec3(nx, ny, nz); 
    }
    
    //Static methods
    /**
     * Calculates the cross product given two vectors.
     * 
     * @param p The first vector.
     * @param q The second vector.
     * @return A new vector orthogonal to both p & q.
     */
    public static vec3 cross(vec3 p, vec3 q) {
        return new vec3(
                p.y * q.z - p.z * q.y,
                p.z * q.x - p.x * q.z,
                p.x * q.y - p.y * q.x
        );
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
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc=" Information Getter Methods ">
    /**
     * Calculates the average of each component.
     * 
     * @return The average as a float.
     */
    public float average() { return (x + y + z) / 3.0f; }

    public float getDist(vec3 o) { return (float) Math.sqrt(getDistSqrd(o)); }
    public float getDistSqrd(vec3 o) { return (float) (Math.pow(o.x - x, 2) + Math.pow(o.y - y, 2) + Math.pow(o.z - z, 2)); }
    
    public float length() { return (float)Math.sqrt(lengthSqrd()); }
    public float lengthSqrd() { return x*x + y*y + z*z; }
    //</editor-fold>

    
    
    public vec3 abs() { return new vec3(Math.abs(x), Math.abs(y), Math.abs(z)); }
    public vec3 negate() { return this.scale(-1.0f); }
    
    

    public vec3 normalize(){
        float length = length();
        if (length == 0) { return new vec3(); }
        return new vec3(x / length, y / length, z / length);
    }
    
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

    public java.awt.Color toAwtColor() { 
        vec3 c = clamp(0, 255);
        return new java.awt.Color( (int) c.x , (int) c.y , (int) c.z );
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

    //<editor-fold defaultstate="collapsed" desc=" String methods & constructors ">
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
    //</editor-fold>
    
    @Override
    public boolean equals(Object obj) { 
        if (!(obj instanceof vec3)) return false;
        if (this == obj) return true;
        vec3 o = (vec3) obj;
        return x == o.x && y == o.y && z == o.z; 
        
    }
    
    public final static vec3 WHITE = new vec3(255.0f);
    public final static vec3 GRAY  = new vec3(128.0f);

}
