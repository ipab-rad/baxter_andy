package com.rad.myo.publish;

import com.rad.myo.data.MyoData;

import org.ros.concurrent.CancellableLoop;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Publisher;

import geometry_msgs.Vector3;
import std_msgs.Bool;

public class MyoPublisherNode extends AbstractNodeMain implements PublisherNode {

    private static final String TAG = MyoPublisherNode.class.getSimpleName();
    private final MyoData myoData;

    private Publisher<Vector3> orientationPublisher;
    private Publisher<Vector3> positionPublisher;
    private Publisher<Bool> enablePublisher;
    private Publisher<Bool> calibratePublisher;
    private Publisher<std_msgs.String> gesturePublisher;

    public MyoPublisherNode(MyoData myoData) {
        this.myoData = myoData;
    }

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("MyoPublisherNode");
    }

    @Override
    public void onStart(ConnectedNode connectedNode) {
        orientationPublisher = connectedNode.newPublisher(GraphName.of("orientation_myo_"+myoData.getMyoId()), Vector3._TYPE);
        positionPublisher = connectedNode.newPublisher(GraphName.of("position_myo_"+myoData.getMyoId()), Vector3._TYPE);
        enablePublisher = connectedNode.newPublisher(GraphName.of("enabled_myo_"+myoData.getMyoId()), Bool._TYPE);
        calibratePublisher = connectedNode.newPublisher(GraphName.of("calibrated_myo_"+myoData.getMyoId()), Bool._TYPE);
        gesturePublisher = connectedNode.newPublisher(GraphName.of("calibrated_myo_"+myoData.getMyoId()), std_msgs.String._TYPE);

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
        Messenger.sendPositionMessage(TAG, myoData.getMyoId(), myoData.getAccelerometerData(), positionPublisher);
        Messenger.sendOrientationMessage(TAG, myoData.getMyoId(), myoData.getOrientationData(), orientationPublisher);
        Messenger.sendBooleanMessage(TAG, myoData.getMyoId(), myoData.isEnabled(), enablePublisher);
        Messenger.sendBooleanMessage(TAG, myoData.getMyoId(), myoData.isCalibrated(), calibratePublisher);
        Messenger.sendMessage(TAG, myoData.getMyoId(), myoData.getGesture(), gesturePublisher);
    }

}
