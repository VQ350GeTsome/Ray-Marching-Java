package Util;

/**
 * Small class that's analogous to a integer reference
 * so I can pass by reference instead of pass by value
 * 
 * @author Harrison
 */
public class IntRef {
    /**
     * The integer being stored / kept track of
     */
    public int i = 0;
    /**
     * @param i What to intialize the integer at
     */
    public IntRef(int i) { this.i = i; }
}
