package com.rad.myobaxter.data;

import com.thalmic.myo.Vector3;

import lombok.Data;

/**
 * Created by andrewrobinson on 28/03/2015.
 */
@Data
public class GyroData {
    private Vector3 gyro;
    private static final GyroData gyroData = new GyroData();
    private final OriginalGyro originalGyro = OriginalGyro.getInstance();
    private final CalibratedGyro calibratedGyro = CalibratedGyro.getInstance();

    public static GyroData getInstance(){
        return gyroData;
    }

    public void setGyroData(long timestamp, Vector3 gyro){
        originalGyro.setTimestamp(timestamp);
        originalGyro.setGyro(gyro);
        if(getCalibratedGyro().getGyro() == null){
            getCalibratedGyro().setTimestamp(timestamp);
            getCalibratedGyro().setGyro(gyro);
        }
    }

    public void offsetGyro(){
        gyro.subtract(calibratedGyro.getGyro());
    }

}
