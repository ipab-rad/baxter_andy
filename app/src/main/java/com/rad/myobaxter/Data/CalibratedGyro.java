package com.rad.myobaxter.Data;

import com.thalmic.myo.Vector3;

import lombok.Data;

/**
 * Created by andrewrobinson on 24/03/2015.
 */
@Data
public class CalibratedGyro {
    private Vector3 gyro;
    private long timestamp;
    private long previousTimestamp;
    private static final CalibratedGyro calibratedGyro = new CalibratedGyro();

    public static CalibratedGyro getInstance(){
        return calibratedGyro;
    }

    public void setTimestamp(long timestamp){
        this.previousTimestamp = this.timestamp;
        this.timestamp = timestamp;
    }

}
