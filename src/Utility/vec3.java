package Utility;


public class vec3 {
    
    public float x, y, z;

    public vec3(float x, float y, float z){ this.x = x; this.y = y; this.z = z; }
    public vec3() { x = 0; y = 0; z = 0; }
    
    //Returns a vector that is the sum of this and another vector
    public vec3 add(vec3 other) { return new vec3(x + other.x, y + other.y, z + other.z); }
    //Returns a vector that is the difference of this and another vector
    public vec3 subtract(vec3 other) { return new vec3(x - other.x, y - other.y, z - other.z); }
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
}
