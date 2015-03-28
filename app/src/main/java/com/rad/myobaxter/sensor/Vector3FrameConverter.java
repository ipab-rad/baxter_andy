package com.rad.myobaxter.sensor;

import com.thalmic.myo.Quaternion;
import com.thalmic.myo.Vector3;

/**
 * Created by andrewrobinson on 28/03/2015.
 */
public class Vector3FrameConverter {
    public static void convertFromBodyToInertiaFrame(Vector3 vector, Quaternion originalRotation, Quaternion calibratedRotation) {

        Quaternion vectorAsQuaternion = new Quaternion(vector.x(), vector.y(), vector.z(), 0);

        Quaternion qinv = new Quaternion(calibratedRotation);
        qinv.inverse();

        Quaternion current = new Quaternion(originalRotation);

        Quaternion diff = new Quaternion(current);
        diff.multiply(qinv);

        Quaternion diffInv = new Quaternion(diff);
        diffInv.inverse();

        Quaternion result = new Quaternion(diffInv);
        result.multiply(vectorAsQuaternion);
        result.multiply(diff);

        vector.set(new Vector3(result.x(), result.y(), result.z()));
    }
}
