package com.rad.myobaxter.data;

import com.thalmic.myo.Vector3;

import lombok.Data;

/**
 * Created by andrewrobinson on 24/03/2015.
 */
@Data
public class CalibratedAccel {
    private Vector3 accel;
    private long timestamp;
    private long previousTimestamp;
    private static final CalibratedAccel calibratedAccel = new CalibratedAccel();

    public static CalibratedAccel getInstance(){
        return calibratedAccel;
    }

    public void setTimestamp(long timestamp){
        if(this.timestamp == 0){
            this.previousTimestamp = timestamp;
        } else {
            this.previousTimestamp = this.timestamp;
        }
        this.timestamp = timestamp;
    }

}
