package com.rad.myobaxter.publish;

import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Publisher;

public class GesturePublisherNode extends AbstractNodeMain implements PublisherNode {

    private static final String TAG = GesturePublisherNode.class.getSimpleName();
    private Publisher<std_msgs.String> publisher;
    private int myoId;

    public GesturePublisherNode(int id){
        myoId = id;
    }

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("GesturePublisher/MyoGestureNode");
    }

    @Override
    public void onStart(ConnectedNode connectedNode) {
        publisher = connectedNode.newPublisher(GraphName.of("gesture_myo_"+myoId), std_msgs.String._TYPE);
    }

    @Override
    public void sendInstantMessage(String message){
        Messenger.sendMessage(TAG, myoId, message, publisher);
    }
}
