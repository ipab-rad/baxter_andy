package com.rad.myobaxter.data;

import com.rad.myobaxter.utils.MathUtils;
import com.thalmic.myo.Myo;
import com.thalmic.myo.Quaternion;
import com.thalmic.myo.XDirection;

import lombok.Data;

/**
 * Created by andrewrobinson on 28/03/2015.
 */
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
        // Calculate Euler angles (roll, pitch, and yaw) from the quaternion.
        float originalRoll = (float) Math.toDegrees(Quaternion.roll(rotation));
        float originalPitch = (float) Math.toDegrees(Quaternion.pitch(rotation));
        float originalYaw = (float) Math.toDegrees(Quaternion.yaw(rotation));

        float calibratedRoll = (float) Math.toDegrees(Quaternion.roll(calibratedRotation));
        float calibratedPitch = (float) Math.toDegrees(Quaternion.pitch(calibratedRotation));
        float calibratedYaw = (float) Math.toDegrees(Quaternion.yaw(calibratedRotation));

        // Adjust roll and pitch for the orientation of the Myo on the arm.
        if (myo.getXDirection() == XDirection.TOWARD_ELBOW) {
            originalRoll *= -1;
            originalPitch *= -1;
            calibratedRoll *= -1;
            calibratedPitch *= -1;
        }

        // Next, we apply a rotation to the text view using the roll, pitch, and yaw.
        roll = originalRoll - calibratedRoll;
        pitch = originalPitch - calibratedPitch;
        yaw = originalYaw - calibratedYaw;

    }

    public String rotationDataAsString(){
        return getRoll() + " " + getPitch() + " " + getYaw();
    }

    public void calibrate() {
        calibratedRotation.set(rotation);
    }
}
