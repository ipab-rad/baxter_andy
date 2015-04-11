package com.rad.myobaxter.data;

import com.rad.myobaxter.utils.MathUtils;
import com.thalmic.myo.Myo;
import com.thalmic.myo.Quaternion;
import com.thalmic.myo.XDirection;

import lombok.Data;

@Data
public class OrientationData {
    private double roll = 0;
    private double pitch = 0;
    private double yaw = 0;
    private Quaternion rotation = new Quaternion();
    private Quaternion calibratedRotation;

    public void setOrientationData(Quaternion rotation){
        this.rotation.set(rotation);
        if(calibratedRotation == null){
            calibratedRotation = new Quaternion(rotation);
        }
    }

    public void calculateOffsetRotation(Myo myo){
        int direction = 1;
        // Adjust roll and pitch for the orientation of the Myo on the arm.
        if (myo.getXDirection() == XDirection.TOWARD_ELBOW) {
            direction = -1;
        }

        // Calculate Euler angles (roll, pitch, and yaw) from the quaternion.
        double currentRoll = Quaternion.roll(rotation);
        double calibratedRoll = Quaternion.roll(calibratedRotation);
        double currentPitch = Quaternion.pitch(rotation);
        double calibratedPitch = Quaternion.pitch(calibratedRotation);
        double currentYaw = Quaternion.yaw(rotation);
        double calibratedYaw = Quaternion.yaw(calibratedRotation);

        roll = Math.toDegrees(calibrate(currentRoll, calibratedRoll))*direction;
        pitch = Math.toDegrees(calibrate(currentPitch, calibratedPitch))*direction;
        yaw = Math.toDegrees(calibrate(currentYaw, calibratedYaw));
    }

    public double applyOffset(double current, double offset){
        double difference = current - offset;
        if(difference > Math.PI){
            return difference - 2*Math.PI;
        } else if (difference <= -Math.PI){
            return 2*Math.PI + difference;
        }
        return difference;
    }

    public double calibrate(double current, double offset){
        double difference = current + Math.PI - offset;
        return MathUtils.mod(difference, 2*Math.PI)-Math.PI;
    }

    public String rotationDataAsString(){
        return getRoll() + " " + getPitch() + " " + getYaw();
    }

    public void calibrate() {
        calibratedRotation.set(rotation);
    }
}
