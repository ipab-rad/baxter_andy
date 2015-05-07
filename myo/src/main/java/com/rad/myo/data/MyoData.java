package com.rad.myo.data;

import lombok.Data;

@Data
public class MyoData {
    private final String GRIPPER_GESTURE = "Fist";
    private int myoId = 0;
    private GyroData gyroData;
    private OrientationData orientationData;
    private AccelerometerData accelerometerData;
    private boolean enabled;
    private boolean calibrated;
    private String gesture;

    public MyoData(){
        gyroData = new GyroData();
        orientationData = new OrientationData();
        accelerometerData = new AccelerometerData(orientationData);
        enabled = false;
        calibrated = false;
        gesture = "None";
    }

    public void toggleEnable() {
        enabled = !enabled;
        //TODO send only enable info
        //TODO setup event bus so that a) change in enable sends message of enable and b) activity shows Toast
//        if(isEnabled()) {
//            showToast(getString(R.string.enable));
//        } else {
//            showToast(getString(R.string.disable));
//        }
    }

    public void calibrateSensors(){
        //TODO setup event bus so that a) change in calibrate sends message of calibrate and b) activity shows Toast
        if(haveEnoughSampleBeenCollectedToCalibrate()){
            calibrate();
            calibrated = true;
//            showToast(getString(R.string.reset));
        } else{
//            showToast(getString(R.string.not_enough_samples));
        }
    }

    private boolean haveEnoughSampleBeenCollectedToCalibrate() {
        return accelerometerData.haveEnoughSamplesBeenCollectedToCalibrate();
    }

    private void calibrate(){
        accelerometerData.calibrate();
        gyroData.calibrate();
        orientationData.calibrate();
    }

    public void sendGripperSignal() {
        setGesture(GRIPPER_GESTURE);
    }
}
