package com.rad.myobaxter.data;

import com.thalmic.myo.Myo;
import com.thalmic.myo.Quaternion;
import com.thalmic.myo.XDirection;

import lombok.Data;

/**
 * Created by andrewrobinson on 28/03/2015.
 */
@Data
public class OrientationData {
    private float roll = 0;
    private float pitch = 0;
    private float yaw = 0;
    private OriginalRotation originalRotation = OriginalRotation.getInstance();
    private CalibratedRotation calibratedRotation = CalibratedRotation.getInstance();
    private static OrientationData orientationData = new OrientationData();
    public static OrientationData getInstance(){
        return orientationData;
    }

    public void setOrientationData(long timestamp, Quaternion rotation){
        originalRotation.setTimestamp(timestamp);
        getOriginalRotation().setRotation(rotation);
        if(calibratedRotation.getRotation() == null){
            calibratedRotation.setTimestamp(timestamp);
            calibratedRotation.setRotation(rotation);
        }
    }

    public void calculateOffsetRotation(Myo myo){
        // Calculate Euler angles (roll, pitch, and yaw) from the quaternion.
        float originalRoll = (float) Math.toDegrees(Quaternion.roll(originalRotation.getRotation()));
        float originalPitch = (float) Math.toDegrees(Quaternion.pitch(originalRotation.getRotation()));
        float originalYaw = (float) Math.toDegrees(Quaternion.yaw(originalRotation.getRotation()));

        float calibratedRoll = (float) Math.toDegrees(Quaternion.roll(calibratedRotation.getRotation()));
        float calibratedPitch = (float) Math.toDegrees(Quaternion.pitch(calibratedRotation.getRotation()));
        float calibratedYaw = (float) Math.toDegrees(Quaternion.yaw(calibratedRotation.getRotation()));

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
}
