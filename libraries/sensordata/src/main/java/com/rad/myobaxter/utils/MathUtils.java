package com.rad.myobaxter.utils;

public class MathUtils {
    public static double mod(double m, double n){
        double result = m % n;
        return result < 0 ? result + n : result;
    }
}
