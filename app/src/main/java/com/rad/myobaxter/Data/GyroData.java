package com.rad.myobaxter.data;

import com.thalmic.myo.Vector3;

import lombok.Data;

/**
 * Created by andrewrobinson on 28/03/2015.
 */
@Data
public class GyroData {
    private Vector3 gyro = new Vector3();
    private Vector3 calibratedGyro;

    public void setGyroData(Vector3 gyro){
        this.gyro = gyro;
        if(calibratedGyro == null){
            calibratedGyro = new Vector3(gyro);
        }
    }

    public void offsetGyro(){
        gyro.subtract(calibratedGyro);
    }

    public void calibrate() {
        calibratedGyro.set(gyro);
    }
}
