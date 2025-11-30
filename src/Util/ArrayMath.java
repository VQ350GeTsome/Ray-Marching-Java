package Util;

public class ArrayMath {

    public static String[] add(String[] a, String[] b) {
        //Make new array (k) to return
        int l = a.length + b.length;
        String[] k = new String[l];
        
        //Concatenate them and return k
        for (int i = 0; l > i; i++) k[i] = (a.length > i) ? a[i] : b[i - a.length];
        return k;
    }
    public static String[] subArray(String[] a, int s, int e) {
        if (s < 0 || e < s) return new String[] {};

        String[] result = new String[e - s];
        for (int i = s; i < e; i++) {
            result[i - s] = a[i];
        }
        return result;
    }
    
}
