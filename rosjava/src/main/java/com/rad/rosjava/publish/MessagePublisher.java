package com.rad.rosjava.publish;

import org.ros.node.topic.Publisher;

import std_msgs.Bool;
import geometry_msgs.Vector3;

public class MessagePublisher {

    public static void publishString(Publisher<std_msgs.String> publisher, String message) {
        std_msgs.String str = publisher.newMessage();
        str.setData(message);
        publisher.publish(str);
    }

    public static void publishVector3(Publisher<Vector3> publisher, double x, double y, double z) {
        Vector3 vector = publisher.newMessage();
        vector.setX(x);
        vector.setY(y);
        vector.setZ(z);
        publisher.publish(vector);
    }

    public static void publishBool(Publisher<Bool> publisher, boolean message) {
        Bool bool = publisher.newMessage();
        bool.setData(message);
        publisher.publish(bool);
    }
}
