package com.rad.myo.data;

import java.util.List;

public class SensorCalibrator {
    private List<GyroData> gyroDataList;
    private List<OrientationData> orientationDataList;
    private List<AccelerometerData> accelerometerDataList;

    public SensorCalibrator(List<AccelerometerData> accelerometerDataList, List<OrientationData> orientationDataList, List<GyroData> gyroDataList) {
        this.accelerometerDataList = accelerometerDataList;
        this.orientationDataList = orientationDataList;
        this.gyroDataList = gyroDataList;
    }

    public void calibrate(){
        for(AccelerometerData accelerometerData: accelerometerDataList) {
            accelerometerData.calibrate();
        }
        for(GyroData gyroData: gyroDataList) {
            gyroData.calibrate();
        }
        for(OrientationData orientationData: orientationDataList) {
            orientationData.calibrate();
        }
    }
}
