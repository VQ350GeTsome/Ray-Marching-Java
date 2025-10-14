package utilities;


public class Vector3 {
    
    public double x, y, z;

    public Vector3(double x, double y, double z){ this.x = x; this.y = y; this.z = z; }
    public Vector3() { x = 0; y = 0; z = 0; }
   
    public double getX() { return x; }
    public double getY() { return y; }
    public double getZ() { return z; }
    
    
    public void setXYZ(double x, double y, double z) { this.x = x; this.y = y; this.z = z; }
    public void setX(double x) {this.x = x; }
    public void setY(double y) {this.y = y; }
    public void setZ(double z) {this.z = z; }
    
    //Returns a vector that is the sum of this and another vector
    public Vector3 add(Vector3 other) { return new Vector3(x + other.getX(), y + other.getY(), z + other.getZ()); }
    //Returns a vector that is the difference of this and another vector
    public Vector3 subtract(Vector3 other) { return new Vector3(x - other.getX(), y - other.getY(), z - other.getZ()); }
    //Returns a vector where each element was multiplied by the input
    public Vector3 multiply(Double p) { return new Vector3(x * p, y * p, z * p); } 
    //Returns a vector such that each elemtent is moduloed
    public Vector3 modulo(Double m) { return new Vector3(x % m, y % m, z % m); }
    //Returns the distance between two vectors
    public double getDist(Vector3 other) { return Math.sqrt(Math.pow(other.getX() - x, 2) + Math.pow(other.getY() - y, 2) + Math.pow(other.getZ() - z, 2)); }
    //Returns the absolute value of this vector
    public Vector3 abs() { return new Vector3(Math.abs(x), Math.abs(y), Math.abs(z)); }
    //Returns the length of a vector from 0, 0, 0
    public double length() { return Math.sqrt(x * x + y * y + z * z); }
    //Returns a vector that is the max of each element from this vector and another
    public Vector3 max(Vector3 other) { return new Vector3(Math.max(this.x, other.getX()), Math.max(this.y, other.getY()), Math.max(this.z, other.getZ())); }
    //Normalizes the vector
    public Vector3 normalize(){
        double length = length();
        if (length == 0) { return new Vector3(); }
        return new Vector3(x / length, y / length, z / length);
    }
    public Vector3 cross(Vector3 other){
        return new Vector3(y * other.getZ() - z * other.getY(),
        z * other.getX() - x * other.getZ(),
        x * other.getY() - y * other.getX());
    }
    public double dot(Vector3 other) { return x * other.getX() + y * other.getY() + z * other.getZ(); }
    public static Vector3 rotate(Vector3 p, Vector3 axis, double angleRad) {
        Vector3 k = axis.normalize(); // Ensure axis is normalized
        Vector3 term1 = p.multiply(Math.cos(angleRad));
        Vector3 term2 = k.cross(p).multiply(Math.sin(angleRad));
        Vector3 term3 = k.multiply(k.dot(p) * (1 - Math.cos(angleRad)));
        return term1.add(term2).add(term3).normalize(); // Re-normalize for safety
    }
    
}
