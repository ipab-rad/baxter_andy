package com.rad.myobaxter.publish;

import android.util.Log;

import com.rad.myobaxter.data.AccelerometerData;
import com.rad.myobaxter.data.OrientationData;
import com.thalmic.myo.Vector3;

import org.ros.node.topic.Publisher;

import java.lang.String;

import std_msgs.Bool;

/**
 * Created by andrewrobinson on 28/03/2015.
 */
public class Messenger {
    public static void sendMessage(String tag, int myoId, String message, Publisher<std_msgs.String> publisher){
        message = myoId + ":" + message;
        Log.i(tag, message);

        // create and publish a simple string message
        try {
            std_msgs.String str = publisher.newMessage();
            str.setData(message);
            publisher.publish(str);
        } catch (ClassCastException e) {
            Log.d(tag, "Could not publish message.");
        }
    }

    public static void sendOrientationMessage(String tag, int myoId, OrientationData orientationData, Publisher<geometry_msgs.Vector3> publisher){
        Log.i(tag, myoId + ":" + orientationData.getRoll() + " " + orientationData.getPitch() + " " + orientationData.getYaw());

        // create and publish roll, pitch and yaw as a Vector3 message
        geometry_msgs.Vector3 vector = publisher.newMessage();
        vector.setX(orientationData.getRoll());
        vector.setY(orientationData.getPitch());
        vector.setZ(orientationData.getYaw());
        publisher.publish(vector);
    }

    public static void sendPositionMessage(String tag, int myoId, AccelerometerData accelerometerData, Publisher<geometry_msgs.Vector3> publisher){
        Vector3 position = accelerometerData.getPosition();
        Log.i(tag, myoId + ":" + position.x()+ " " + position.y() + " " + position.z());

        // create and publish roll, pitch and yaw as a Vector3 message
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
