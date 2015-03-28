package com.rad.myobaxter.data;

import com.thalmic.myo.Vector3;

import lombok.Data;

/**
 * Created by andrewrobinson on 24/03/2015.
 */
@Data
public class OriginalAccel {
    private Vector3 accel;
    private long timestamp;
    private long previousTimestamp;
    private static final OriginalAccel originalAccel = new OriginalAccel();

    public static OriginalAccel getInstance(){
        return originalAccel;
    }

    public void setTimestamp(long timestamp){
        if(this.timestamp == 0){
            this.previousTimestamp = timestamp;
        } else {
            this.previousTimestamp = this.timestamp;
        }
        this.timestamp = timestamp;
    }

    public long timeDifference() {
        return timestamp-previousTimestamp;
    }
}
