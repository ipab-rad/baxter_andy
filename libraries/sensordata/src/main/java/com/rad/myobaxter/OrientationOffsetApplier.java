package com.rad.myobaxter;

import com.rad.myobaxter.utils.MathUtils;

import lombok.Data;

@Data
public class OrientationOffsetApplier {
    public static double applyOffset(double current, double offset){
        double difference = current + Math.PI - offset;
        return MathUtils.mod(difference, 2*Math.PI)-Math.PI;
    }

    public static double applyOffsetSimpleVersion(double current, double offset){
        double difference = current - offset;
        if(difference >= Math.PI){
            return difference - 2*Math.PI;
        } else if (difference < -Math.PI){
            return 2*Math.PI + difference;
        }
        return difference;
    }
}
