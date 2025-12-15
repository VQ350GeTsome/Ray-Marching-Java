package Util;

/**
 * Small class that's analogous to a integer reference
 * so I can pass by reference instead of pass by value
 * 
 * @author Harrison Davis
 */
public class IntRef {
    /**
     * The integer being stored / kept track of.
     */
    public int i;
    /**
     * Explicit constructor.
     * 
     * @param i What to intialize the internal integer at.
     */
    public IntRef(int i) { this.i = i; }
    /**
     * Default constructor. The internal integer will
     * be set to 0.
     */
    public IntRef() { this.i = 0; }
}
