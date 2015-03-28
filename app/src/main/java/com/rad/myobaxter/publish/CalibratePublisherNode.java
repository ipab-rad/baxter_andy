package com.rad.myobaxter.publish;

import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Publisher;

import java.lang.String;

public class CalibratePublisherNode extends AbstractNodeMain implements PublisherNode {

    private static final String TAG = CalibratePublisherNode.class.getSimpleName();
    private Publisher<std_msgs.String> publisher;
    private int myoId;

    public CalibratePublisherNode(int id){
        myoId = id;
    }

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("CalibratePublisher/MyoCalibrateNode");
    }

    @Override
    public void onStart(ConnectedNode connectedNode) {
        publisher = connectedNode.newPublisher(GraphName.of("calibrate_myo_"+myoId), std_msgs.String._TYPE);
    }

    @Override
    public void sendInstantMessage(String message){
        Messenger.sendMessage(TAG, myoId, message, publisher);
    }
}
