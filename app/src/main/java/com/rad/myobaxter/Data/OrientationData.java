package com.rad.myobaxter.data;

import com.rad.myobaxter.OrientationOffsetApplier;
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

        roll = Math.toDegrees(OrientationOffsetApplier.applyOffset(currentRoll, calibratedRoll))*direction;
        pitch = Math.toDegrees(OrientationOffsetApplier.applyOffset(currentPitch, calibratedPitch))*direction;
        yaw = Math.toDegrees(OrientationOffsetApplier.applyOffset(currentYaw, calibratedYaw));
    }

    public String rotationDataAsString(){
        return getRoll() + " " + getPitch() + " " + getYaw();
    }

    public void calibrate() {
        calibratedRotation.set(rotation);
    }
}
