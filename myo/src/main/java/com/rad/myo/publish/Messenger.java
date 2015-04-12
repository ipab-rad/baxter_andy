package com.rad.myo.publish;

import android.util.Log;

import com.rad.myo.data.AccelerometerData;
import com.rad.myo.data.OrientationData;
import com.thalmic.myo.Vector3;

import org.ros.node.topic.Publisher;

import java.lang.String;

import std_msgs.Bool;

public class Messenger {
    public static void sendMessage(String tag, int myoId, String message, Publisher<std_msgs.String> publisher){
        message = myoId + ":" + message;
        Log.i(tag, message);

        try {
            std_msgs.String str = publisher.newMessage();
            str.setData(message);
            publisher.publish(str);
        } catch (ClassCastException e) {
            Log.d(tag, "Could not publish message.");
        }
    }

    public static void sendOrientationMessage(String tag, int myoId, OrientationData orientationData, Publisher<geometry_msgs.Vector3> publisher){
        //TODO do we need to convert to degrees here?
        double offsetRoll = Math.toDegrees(orientationData.getOffsetRoll());
        double offsetPitch = Math.toDegrees(orientationData.getOffsetPitch());
        double offsetYaw = Math.toDegrees(orientationData.getOffsetYaw());
        Log.i(tag, myoId + ":" + offsetRoll + " " + offsetPitch + " " + offsetYaw);

        geometry_msgs.Vector3 vector = publisher.newMessage();
        vector.setX(offsetRoll);
        vector.setY(offsetPitch);
        vector.setZ(offsetYaw);
        publisher.publish(vector);
    }

    public static void sendPositionMessage(String tag, int myoId, AccelerometerData accelerometerData, Publisher<geometry_msgs.Vector3> publisher){
        Vector3 position = accelerometerData.getPosition();
        Log.i(tag, myoId + ":" + position.x()+ " " + position.y() + " " + position.z());

        geometry_msgs.Vector3 vector = publisher.newMessage();
        vector.setX(position.x()    );
        vector.setY(position.y());
        vector.setZ(position.z());
        publisher.publish(vector);
    }

    public static void sendBooleanMessage(String tag, int myoId, boolean message, Publisher<Bool> publisher) {
        Log.i(tag, myoId + ":" + message);
        Bool bool = publisher.newMessage();
        bool.setData(message);
        publisher.publish(bool);
    }
}
