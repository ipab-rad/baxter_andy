package com.rad.math.angles;

import static com.rad.math.Math.mod;
import static java.lang.Math.PI;

public class AngleOffsetter {
    public static double applyOffset(double current, double offset){
        double difference = current + PI - offset;
        return mod(difference, 2 * PI)- PI;
    }

    public static double applyOffsetSimpleVersion(double current, double offset){
        double difference = current - offset;
        if(difference >= PI){
            return difference - 2* PI;
        } else if (difference < -PI){
            return 2* PI + difference;
        }
        return difference;
    }
}
