package com.rad.myobaxter.sensor;

import com.rad.myobaxter.data.AccelSampleData;
import com.rad.myobaxter.data.AccelerometerData;
import com.rad.myobaxter.data.CalibratedAccel;
import com.rad.myobaxter.data.CalibratedGyro;
import com.rad.myobaxter.data.CalibratedRotation;
import com.rad.myobaxter.data.OriginalGyro;
import com.rad.myobaxter.data.OriginalRotation;
import com.thalmic.myo.Vector3;

import java.util.List;

/**
 * Created by andrewrobinson on 28/03/2015.
 */
public class SensorCalibrator {
    public static boolean calibrate(){
        List<Vector3Sample> samples = AccelSampleData.getInstance().getSamples();
        if(samples.size() > AccelSampleData.CALIBRATED_SAMPLE_SIZE) {
            calibrateAcceleration(samples);
            calibrateGyro();
            calibrateRotation();
            AccelerometerData.getInstance().setAccelerometerDataToZero();
            return true;
        } else {
            return false;
        }
    }

    private static void calibrateRotation() {
        CalibratedRotation.getInstance().setRotation(OriginalRotation.getInstance().getRotation());
    }

    private static void calibrateGyro() {
        CalibratedGyro.getInstance().setGyro(OriginalGyro.getInstance().getGyro());
    }

    private static void calibrateAcceleration(List<Vector3Sample> samples) {
        Vector3 calibratedAccel = new Vector3();
        for (int i = samples.size() - AccelSampleData.CALIBRATED_SAMPLE_SIZE; i < samples.size(); i++) {
            calibratedAccel.add(samples.get(i).getValue());
        }
        calibratedAccel.divide(AccelSampleData.CALIBRATED_SAMPLE_SIZE);
        CalibratedAccel.getInstance().setAccel(calibratedAccel);
    }
}
