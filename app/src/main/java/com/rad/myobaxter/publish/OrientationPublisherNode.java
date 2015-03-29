package com.rad.myobaxter.publish;

import com.rad.myobaxter.data.OrientationData;

import org.ros.concurrent.CancellableLoop;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Publisher;

import java.lang.String;

public class OrientationPublisherNode extends AbstractNodeMain implements PublisherNode {

    private static final String TAG = OrientationPublisherNode.class.getSimpleName();
    private final OrientationData orientationData;
    private Publisher<std_msgs.String> publisher;
    private int myoId;

    public OrientationPublisherNode(int id, OrientationData orientationData){
        myoId = id;
        this.orientationData = orientationData;
    }

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("OrientationPublisher/MyoOrientationNode");
    }

    @Override
    public void onStart(ConnectedNode connectedNode) {
        publisher = connectedNode.newPublisher(GraphName.of("orientation_myo_"+myoId), std_msgs.String._TYPE);

        final CancellableLoop loop = new CancellableLoop() {
            @Override
            protected void loop() throws InterruptedException {
                sendInstantMessage(orientationData.rotationDataAsString());
                Thread.sleep(100);
            }
        };
        connectedNode.executeCancellableLoop(loop);
    }

    @Override
    public void sendInstantMessage(String message){
        Messenger.sendMessage(TAG, myoId, message, publisher);
    }

}
