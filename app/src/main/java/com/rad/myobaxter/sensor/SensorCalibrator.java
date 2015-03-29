package com.rad.myobaxter.sensor;

import com.rad.myobaxter.data.AccelerometerData;
import com.rad.myobaxter.data.GyroData;
import com.rad.myobaxter.data.OrientationData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by andrewrobinson on 28/03/2015.
 */
public class SensorCalibrator {
    private List<GyroData> gyroDataList = new ArrayList<GyroData>();
    private List<OrientationData> orientationDataList = new ArrayList<OrientationData>();
    private List<AccelerometerData> accelerometerDataList = new ArrayList<AccelerometerData>();

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
