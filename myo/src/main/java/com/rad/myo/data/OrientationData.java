package com.rad.myo.data;

import com.rad.math.angles.AngleOffsetter;
import com.thalmic.myo.Myo;
import com.thalmic.myo.Quaternion;
import com.thalmic.myo.XDirection;

import lombok.Data;

@Data
public class OrientationData {
    private double offsetRoll = 0;
    private double offsetPitch = 0;
    private double offsetYaw = 0;
    private Quaternion originalRotation = new Quaternion();
    private Quaternion calibratedRotation;

    public void setOrientationData(Quaternion rotation){
        this.originalRotation.set(rotation);
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
        double currentRoll = Quaternion.roll(originalRotation)*direction;
        double calibratedRoll = Quaternion.roll(calibratedRotation)*direction;
        double currentPitch = Quaternion.pitch(originalRotation)*direction;
        double calibratedPitch = Quaternion.pitch(calibratedRotation)*direction;
        double currentYaw = Quaternion.yaw(originalRotation);
        double calibratedYaw = Quaternion.yaw(calibratedRotation);

        offsetRoll = AngleOffsetter.applyOffset(currentRoll, calibratedRoll);
        offsetPitch = AngleOffsetter.applyOffset(currentPitch, calibratedPitch);
        offsetYaw = AngleOffsetter.applyOffset(currentYaw, calibratedYaw);
    }

    public void calibrate() {
        calibratedRotation.set(originalRotation);
    }
}
