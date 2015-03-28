package com.rad.myobaxter.data;

import com.rad.myobaxter.sensor.Vector3FrameConverter;
import com.rad.myobaxter.sensor.Vector3Sample;
import com.thalmic.myo.Vector3;

import java.util.List;

import lombok.Data;

/**
 * Created by andrewrobinson on 28/03/2015.
 */
@Data
public class AccelerometerData {
    private static final double ZERO_ACCEL_THRESHOLD = 0.1;
    private static final double VELOCITY_DEGRADER = 0.9;
    private Vector3 acceleration;
    private Vector3 velocity;
    private Vector3 position;
    private final double g = 9.80665;
    private static final AccelerometerData accelerometerData = new AccelerometerData();
    private final OriginalAccel originalAccel = OriginalAccel.getInstance();
    private final CalibratedAccel calibratedAccel = CalibratedAccel.getInstance();

    public static AccelerometerData getInstance(){
        return accelerometerData;
    }

    public void setAccelerometerDataToZero(){
        setAccelerometerData(new Vector3(), new Vector3(), new Vector3());
    }

    public void setAccelerometerData(Vector3 acceleration, Vector3 velocity, Vector3 position){
        this.acceleration = acceleration;
        this.velocity = velocity;
        this.position = position;
    }

    public void setAccelerometerData(long timestamp, Vector3 accel){
        originalAccel.setTimestamp(timestamp);
        originalAccel.setAccel(accel);
        Vector3FrameConverter.convertFromBodyToInertiaFrame(accel, OriginalRotation.getInstance().getRotation(), CalibratedRotation.getInstance().getRotation());
        if(calibratedAccel.getAccel() == null){
            calibratedAccel.setTimestamp(timestamp);
            calibratedAccel.setAccel(accel);
        }
    }

    public void calculateVelocityAndPositionFromAcceleration(AccelSampleData accelSampleData){
        List<Vector3Sample> movingAverage = accelSampleData.getMovingAverage();
        if(movingAverage.size() > 2) {
            Vector3Sample latestMovingAverage = movingAverage.get(movingAverage.size() - 1);
            double timePeriod = accelSampleData.milliSecondsBetweenCurrentAndLastSample();
            Vector3 accel = new Vector3(latestMovingAverage.getValue());
            accel.subtract(calibratedAccel.getAccel());
            accel.multiply(g);
            calculatePositionAndVelocity(accel, timePeriod);
        }
    }

    private void calculatePositionAndVelocity(Vector3 accel, double timePeriod){
        double accelerationX = Math.abs(accel.x()) < ZERO_ACCEL_THRESHOLD ? 0 : accel.x();
        double accelerationY = Math.abs(accel.y()) < ZERO_ACCEL_THRESHOLD ? 0 : accel.y();
        double accelerationZ = Math.abs(accel.z()) < ZERO_ACCEL_THRESHOLD ? 0 : accel.z();

        double vx = velocity.x() + timePeriod * accelerationX;
        double velocityX = accelerationX == 0 ? vx * VELOCITY_DEGRADER : vx;
        double vy = velocity.y() + timePeriod * accelerationY;
        double velocityY = accelerationY == 0 ? vy * VELOCITY_DEGRADER : vy;
        double vz = velocity.z() + timePeriod * accelerationZ;
        double velocityZ = accelerationZ == 0 ? vz * VELOCITY_DEGRADER : vz;

        double positionX = position.x() + ((timePeriod * velocity.x()));
        double positionY = position.y() + ((timePeriod * velocity.y()));
        double positionZ = position.z() + ((timePeriod * velocity.z()));

        this.acceleration = new Vector3(accelerationX, accelerationY, accelerationZ);
        this.velocity = new Vector3(velocityX, velocityY, velocityZ);
        this.position = new Vector3(positionX, positionY, positionZ);
    }

    public String positionDataAsString(){
        return position.x() + " " + position.y() + " " + position.z();
    }
}
