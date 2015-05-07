package com.rad.myo.publish;

import com.rad.myo.data.MyoData;
import com.rad.rosjava.publish.PublisherNode;

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
        orientationPublisher = connectedNode.newPublisher(GraphName.of("myo_"+myoData.getMyoId()+"/orientation"), Vector3._TYPE);
        positionPublisher = connectedNode.newPublisher(GraphName.of("myo_"+myoData.getMyoId()+"/position"), Vector3._TYPE);
        enablePublisher = connectedNode.newPublisher(GraphName.of("myo_"+myoData.getMyoId()+"/enabled"), Bool._TYPE);
        calibratePublisher = connectedNode.newPublisher(GraphName.of("myo_"+myoData.getMyoId()+"/calibrated"), Bool._TYPE);
        gesturePublisher = connectedNode.newPublisher(GraphName.of("myo_"+myoData.getMyoId()+"/gesture"), std_msgs.String._TYPE);

        final CancellableLoop loop = new CancellableLoop() {
            @Override
            protected void loop() throws InterruptedException {
                publishMessage();
                Thread.sleep(100);
            }
        };
        connectedNode.executeCancellableLoop(loop);
    }

    @Override
    public void publishMessage(){
        MyoMessenger.sendPositionMessage(TAG, myoData.getMyoId(), myoData.getAccelerometerData(), positionPublisher);
        MyoMessenger.sendOrientationMessage(TAG, myoData.getMyoId(), myoData.getOrientationData(), orientationPublisher);
        MyoMessenger.sendBooleanMessage(TAG, myoData.getMyoId(), myoData.isEnabled(), enablePublisher);
        MyoMessenger.sendBooleanMessage(TAG, myoData.getMyoId(), myoData.isCalibrated(), calibratePublisher);
        MyoMessenger.sendMessage(TAG, myoData.getMyoId(), myoData.getGesture(), gesturePublisher);
    }

}
