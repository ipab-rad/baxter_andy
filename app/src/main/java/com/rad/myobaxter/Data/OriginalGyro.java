package com.rad.myobaxter.Data;

import com.thalmic.myo.Vector3;

import lombok.Data;

/**
 * Created by andrewrobinson on 24/03/2015.
 */
@Data
public class OriginalGyro {
    private Vector3 gyro;
    private long timestamp;
    private long previousTimestamp;
    private static final OriginalGyro originalGyro = new OriginalGyro();

    public static OriginalGyro getInstance(){
        return originalGyro;
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
