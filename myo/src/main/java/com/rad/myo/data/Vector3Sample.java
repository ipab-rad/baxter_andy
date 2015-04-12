package com.rad.myo.data;

import com.thalmic.myo.Vector3;

import lombok.Data;

@Data
public class Vector3Sample {
    private long timestamp;
    private Vector3 value;

    public Vector3Sample(Vector3 vector, long timestamp) {
        this.value = vector;
        this.timestamp = timestamp;
    }
}
