package com.rad.myobaxter.publish;

import com.rad.myobaxter.data.AccelerometerData;
import com.rad.myobaxter.data.OrientationData;

import org.ros.concurrent.CancellableLoop;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Publisher;

import geometry_msgs.Vector3;
import geometry_msgs.Twist;
import lombok.Setter;
import std_msgs.Bool;

public class MyoPublisherNode extends AbstractNodeMain implements PublisherNode {

    private static final String TAG = MyoPublisherNode.class.getSimpleName();
    private final AccelerometerData accelerometerData;
    private final OrientationData orientationData;

    @Setter
    private boolean enabled;
    @Setter
    private boolean calibrated;
    @Setter
    private String gesture;

    private Publisher<Vector3> orientationPublisher;
    private Publisher<Vector3> positionPublisher;
    private Publisher<Bool> enablePublisher;
    private Publisher<Bool> calibratePublisher;
    private Publisher<std_msgs.String> gesturePublisher;
    private int myoId;

    public MyoPublisherNode(int id, AccelerometerData accelerometerData, OrientationData orientationData){
        myoId = id;
        this.accelerometerData = accelerometerData;
        this.orientationData = orientationData;
        this.enabled = false;
        this.calibrated = false;
        this.gesture = "None";
    }

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("MyoPublisherNode");
    }

    @Override
    public void onStart(ConnectedNode connectedNode) {
        orientationPublisher = connectedNode.newPublisher(GraphName.of("orientation_myo_"+myoId), Vector3._TYPE);
        positionPublisher = connectedNode.newPublisher(GraphName.of("position_myo_"+myoId), Vector3._TYPE);
        enablePublisher = connectedNode.newPublisher(GraphName.of("enabled_myo_"+myoId), Bool._TYPE);
        calibratePublisher = connectedNode.newPublisher(GraphName.of("calibrated_myo_"+myoId), Bool._TYPE);
        gesturePublisher = connectedNode.newPublisher(GraphName.of("calibrated_myo_"+myoId), std_msgs.String._TYPE);

        final CancellableLoop loop = new CancellableLoop() {
            @Override
            protected void loop() throws InterruptedException {
                sendInstantMessage();
                Thread.sleep(100);
            }
        };
        connectedNode.executeCancellableLoop(loop);
    }

    @Override
    public void sendInstantMessage(){
        Messenger.sendPositionMessage(TAG, myoId, accelerometerData, positionPublisher);
        Messenger.sendOrientationMessage(TAG, myoId, orientationData, orientationPublisher);
        Messenger.sendBooleanMessage(TAG, myoId, enabled, enablePublisher);
        Messenger.sendBooleanMessage(TAG, myoId, calibrated, calibratePublisher);
        Messenger.sendMessage(TAG, myoId, gesture, gesturePublisher);
    }

}
