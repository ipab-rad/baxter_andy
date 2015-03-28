package com.rad.myobaxter.sensor;

import com.thalmic.myo.Vector3;

import lombok.Data;

/**
 * Created by andrewrobinson on 25/03/2015.
 */
@Data
public class Vector3Sample {
    private long timestamp;
    private Vector3 value;

    public Vector3Sample(Vector3 vector, long timestamp) {
        this.value = vector;
        this.timestamp = timestamp;
    }
}
