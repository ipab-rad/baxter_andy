package com.rad.myobaxter.publish;

import com.rad.myobaxter.data.AccelerometerData;

import org.ros.concurrent.CancellableLoop;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Publisher;

import java.lang.String;

public class PositionPublisherNode extends AbstractNodeMain implements PublisherNode {

    private static final String TAG = PositionPublisherNode.class.getSimpleName();
    private final AccelerometerData accelerometerData;
    private Publisher<std_msgs.String> publisher;
    private int myoId;

    public PositionPublisherNode(int id, AccelerometerData accelerometerData){
        myoId = id;
        this.accelerometerData = accelerometerData;
    }

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("PositionPublisher/MyoPositionNode");
    }

    @Override
    public void onStart(ConnectedNode connectedNode) {
        publisher = connectedNode.newPublisher(GraphName.of("position_myo_"+myoId), std_msgs.String._TYPE);

        final CancellableLoop loop = new CancellableLoop() {
            @Override
            protected void loop() throws InterruptedException {
                sendInstantMessage(accelerometerData.positionDataAsString());
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
