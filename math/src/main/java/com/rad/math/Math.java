package com.rad.math;

public class Math {
    public static double mod(double m, double n){
        double result = m % n;
        return result < 0 ? result + n : result;
    }
}
