package com.rad.myo.data;

import com.rad.myo.frame.Vector3FrameConverter;
import com.thalmic.myo.Vector3;

import java.util.List;

import lombok.Data;

@Data
public class AccelerometerData {
    private static final double ZERO_ACCEL_THRESHOLD = 0.1;
    private static final double VELOCITY_DEGRADER = 0.9;
    private final OrientationData orientationData;
    private Vector3 acceleration = new Vector3();
    private Vector3 velocity = new Vector3();
    private Vector3 position = new Vector3();
    private final double g = 9.80665;
    private Vector3 calibratedAcceleration;
    private AccelSampleData accelSampleData;

    public AccelerometerData(OrientationData orientationData){
        this.orientationData = orientationData;
        accelSampleData = new AccelSampleData();
    }

    public void setAccelerometerDataToZero(){
        setAccelerometerData(new Vector3(), new Vector3(), new Vector3());
    }

    public void setAccelerometerData(Vector3 acceleration, Vector3 velocity, Vector3 position){
        this.acceleration = acceleration;
        this.velocity = velocity;
        this.position = position;
    }

    public void setAccelerometerData(Vector3 accel, long timestamp){
        Vector3FrameConverter.convertFromBodyToInertiaFrame(accel, orientationData);
        if(calibratedAcceleration == null){
            calibratedAcceleration = new Vector3(accel);
        }
        accelSampleData.addSample(accel, timestamp);
    }

    public void calculateVelocityAndPositionFromAcceleration(){
        List<Vector3Sample> movingAverage = accelSampleData.getMovingAverage();
        if(movingAverage.size() > 2) {
            Vector3Sample latestMovingAverage = movingAverage.get(movingAverage.size() - 1);
            double timePeriod = (double) accelSampleData.milliSecondsBetweenCurrentAndLastSample()/1000.0;
            Vector3 accel = new Vector3(latestMovingAverage.getValue());
            accel.subtract(calibratedAcceleration);
            accel.multiply(g);
            calculatePositionAndVelocity(accel, timePeriod);
        }
    }

    private void calculatePositionAndVelocity(Vector3 accel, double timePeriod){
        double accelerationX = Math.abs(accel.x()) < ZERO_ACCEL_THRESHOLD ? 0 : accel.x();
        double accelerationY = Math.abs(accel.y()) < ZERO_ACCEL_THRESHOLD ? 0 : accel.y();
        double accelerationZ = Math.abs(accel.z()) < ZERO_ACCEL_THRESHOLD ? 0 : accel.z();
        acceleration = new Vector3(accelerationX, accelerationY, accelerationZ);

        double vx = velocity.x() + timePeriod * accelerationX;
        double velocityX = accelerationX == 0 ? vx * VELOCITY_DEGRADER : vx;
        double vy = velocity.y() + timePeriod * accelerationY;
        double velocityY = accelerationY == 0 ? vy * VELOCITY_DEGRADER : vy;
        double vz = velocity.z() + timePeriod * accelerationZ;
        double velocityZ = accelerationZ == 0 ? vz * VELOCITY_DEGRADER : vz;
        velocity = new Vector3(velocityX, velocityY, velocityZ);

        double positionX = position.x() + ((timePeriod * velocity.x()));
        double positionY = position.y() + ((timePeriod * velocity.y()));
        double positionZ = position.z() + ((timePeriod * velocity.z()));
        position = new Vector3(positionX, positionY, positionZ);
    }

    public String positionDataAsString(){
        return position.x() + " " + position.y() + " " + position.z();
    }

    public void calibrate() {
        List<Vector3Sample> samples = accelSampleData.getSamples();
        Vector3 calibratedAccel = new Vector3();
        for (int i = samples.size() - AccelSampleData.REQUIRED_SAMPLE_SIZE_TO_CALIBRATE; i < samples.size(); i++) {
            calibratedAccel.add(samples.get(i).getValue());
        }
        calibratedAccel.divide(AccelSampleData.REQUIRED_SAMPLE_SIZE_TO_CALIBRATE);
        calibratedAcceleration.set(calibratedAccel);
        velocity.set(new Vector3());
        position.set(new Vector3());
    }

    public boolean haveEnoughSamplesBeenCollectedToCalibrate() {
        return accelSampleData.getSamples().size() > AccelSampleData.REQUIRED_SAMPLE_SIZE_TO_CALIBRATE;
    }
}
