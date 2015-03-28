package com.rad.myobaxter.publish;

import android.util.Log;

import org.ros.node.topic.Publisher;

import java.lang.String;

/**
 * Created by andrewrobinson on 28/03/2015.
 */
public class Messenger {
    public static void sendMessage(String tag, int myoId, String message, Publisher<std_msgs.String> publisher){
        message = myoId + ":" + message;
        Log.i(tag, message);

        // create and publish a simple string message
        std_msgs.String str = publisher.newMessage();
        str.setData(message);
        publisher.publish(str);
    }
}
