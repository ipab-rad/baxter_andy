package com.rad.myobaxter.Data;

import com.thalmic.myo.Quaternion;

import lombok.Data;

/**
 * Created by andrewrobinson on 24/03/2015.
 */
@Data
public class OriginalRotation {
    private Quaternion rotation;
    private long timestamp;
    private long previousTimestamp;
    private static final OriginalRotation originalRotation = new OriginalRotation();

    public static OriginalRotation getInstance(){
        return originalRotation;
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
