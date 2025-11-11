package Utility;

public class ArrayMath {

    public static String[] add(String[] a, String[] b) {
        //Make new array (k) to return
        int l = a.length + b.length;
        String[] k = new String[l];
        
        //Concatenate them and return k
        for (int i = 0; l > i; i++) k[i] = (a.length > i) ? a[i] : b[i - a.length];
        return k;
    }
    
}
