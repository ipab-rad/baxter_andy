package com.rad.myobaxter.utils;

import android.util.Log;

import com.rad.myobaxter.data.AccelerometerData;
import com.rad.myobaxter.data.OrientationData;

public class LogUtils {
    public static void logAccelerometerData(String tag, long timestamp, AccelerometerData accelerometerData) {
        Log.d(tag, "logdata timestamp: " + String.valueOf(timestamp));
        Log.d(tag, "logdata accelX: " + String.format("%.6f", accelerometerData.getAcceleration().x()));
        Log.d(tag, "logdata accelY: " + String.format("%.6f", accelerometerData.getAcceleration().y()));
        Log.d(tag, "logdata accelZ: " + String.format("%.6f", accelerometerData.getAcceleration().z()));
        Log.d(tag, "logdata velocityX: " + String.format("%.6f", accelerometerData.getVelocity().x()));
        Log.d(tag, "logdata velocityY: " + String.format("%.6f", accelerometerData.getVelocity().y()));
        Log.d(tag, "logdata velocityZ: " + String.format("%.6f", accelerometerData.getVelocity().z()));
        Log.d(tag, "logdata positionX: " + String.format("%.6f", accelerometerData.getPosition().x()));
        Log.d(tag, "logdata positionY: " + String.format("%.6f", accelerometerData.getPosition().y()));
        Log.d(tag, "logdata positionZ: " + String.format("%.6f", accelerometerData.getPosition().z()));
    }

    public static void logOrientationData(String tag, long timestamp, OrientationData orientationData){
        Log.d(tag, "orientation: roll: " + orientationData.getRoll());
        Log.d(tag, "orientation: pitch: " + orientationData.getPitch());
        Log.d(tag, "orientation: yaw: " + orientationData.getYaw());
    }
}
