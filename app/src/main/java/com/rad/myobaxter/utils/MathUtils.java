package com.rad.myobaxter.utils;

/**
 * Created by andrewrobinson on 28/03/2015.
 */
public class MathUtils {
    public static double mod(double m, double n){
        double result = m % n;
        return result < 0 ? result + n : result;
    }
}
