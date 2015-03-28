package com.rad.myobaxter.data;

import com.thalmic.myo.Quaternion;

import lombok.Data;

/**
 * Created by andrewrobinson on 24/03/2015.
 */
@Data
public class CalibratedRotation {
    private Quaternion rotation;
    private long timestamp;
    private long previousTimestamp;
    private static final CalibratedRotation calibratedRotation = new CalibratedRotation();

    public static CalibratedRotation getInstance(){
        return calibratedRotation;
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
