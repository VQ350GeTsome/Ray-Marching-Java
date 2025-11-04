package Utility;

public class vec3 {
    
    public float x, y, z;

    /**
     * Default constructor. { 0.0 , 0.0 , 0.0 }
     */
    public vec3()                           { x = 0.0f; y = 0.0f; z = 0.0f; }
    /**
     * Constructor with just x component { x, 0.0, 0.0 } .
     * @param x The x component.
     */
    public vec3(float x)                    { this.x = x; y = 0.0f; z = 0.0f; }
    /**
     * Constructor with only x & y components { x , y , 0.0 } .
     * @param x The x component.
     * @param y The y component.
     */
    public vec3(float x, float y)           { this.x = x; this.y = y; z = 0.0f; }
    /**
     * Full constructor with x , y , & z components { x , y , z } .
     * @param x The x component.
     * @param y The y component.
     * @param z The z component.
     */
    public vec3(float x, float y, float z)  { this.x = x; this.y = y; this.z = z; }
    public vec3(vec3 copy) {
        x = copy.x;
        y = copy.y;
        z = copy.z;
    }
 
    //Returns a vector that is the sum of this and another vector
    public vec3 add(vec3 other) { return new vec3(x + other.x, y + other.y, z + other.z); }
    //Returns a vector that is the difference of this and another vector
    public vec3 subtract(vec3 other) { return new vec3(x - other.x, y - other.y, z - other.z); }
    public vec3 subtract(float f) {
        return new vec3(
                x - f,
                y - f,
                z - f
        );
    }
    //Returns a vector where each element was multiplied by the input
    public vec3 multiply(float p) { return new vec3(x * p, y * p, z * p); } 
    //Returns a vector such that each elemtent is moduloed
    public vec3 modulo(float m) { return new vec3(x % m, y % m, z % m); }
    //Returns the distance between two vectors
    public float getDist(vec3 other) { return (float)Math.sqrt(Math.pow(other.x - x, 2) + Math.pow(other.y - y, 2) + Math.pow(other.z - z, 2)); }
    //Returns the absolute value of this vector
    public vec3 abs() { return new vec3(Math.abs(x), Math.abs(y), Math.abs(z)); }
    //Returns the length of a vector from 0, 0, 0
    public float length() { return (float)Math.sqrt(x * x + y * y + z * z); }
    public float lengthSqr() { return x*x + y*y + z*z; }
    //Returns a vector that is the max of each element from this vector and another
    public vec3 max(vec3 other) { return new vec3(Math.max(this.x, other.x), Math.max(this.y, other.y), Math.max(this.z, other.z)); }
    //Normalizes the vector
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
    public float dot(vec3 other) { return x * other.x + y * other.y + z * other.z; } 
    
    @Override
    public String toString() {
        return "{ " + x + " | " + y + " | " + z + " }";
    }
    public String[] toStringArray() {
        return new String[] { ""+x, ""+y, ""+z };
    }
    /**
     * Turns the .toString() method back into a vec3 object
     * @param vector A String in the form { x | y | z }
     */
    public vec3(String vector) {
        vector = vector.trim().substring(2, vector.length() - 2);
        String[] componenets = vector.split("\\|");
        
        if (componenets.length != 3) {
            throw new IllegalArgumentException("Invalid vec3 format: " + vector);
        }
                
        // Parse and assign
        this.x = Float.parseFloat(componenets[0].trim());
        this.y = Float.parseFloat(componenets[1].trim());
        this.z = Float.parseFloat(componenets[2].trim());
    }
    /**
     * Turns the .toStringArray method back into a vec3 object
     * @param components A length 3 array of Strings that are floats
     */
    public vec3(String[] components) {
        this.x = Float.parseFloat(components[0].trim());
        this.y = Float.parseFloat(components[1].trim());
        this.z = Float.parseFloat(components[2].trim());
    }
    
    public vec2 xz() {
        return new vec2(x, z);
    }
}
