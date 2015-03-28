package com.rad.myobaxter.publish;

import org.ros.concurrent.CancellableLoop;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Publisher;

import java.lang.String;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SimplePublisherNode extends AbstractNodeMain implements PublisherNode {

    private static final String TAG = SimplePublisherNode.class.getSimpleName();
    private Publisher<std_msgs.String> publisher;
    private int myoId;

    public SimplePublisherNode(int id){
        myoId = id;
    }

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("SimplePublisher/TimeLoopNode");
    }

    @Override
    public void onStart(ConnectedNode connectedNode) {
        publisher = connectedNode.newPublisher(GraphName.of("time"), std_msgs.String._TYPE);

        final CancellableLoop loop = new CancellableLoop() {
            @Override
            protected void loop() throws InterruptedException {
                String time = new SimpleDateFormat("HH:mm:ss").format(new Date());
                sendInstantMessage(time);
                Thread.sleep(1000);
            }
        };
        connectedNode.executeCancellableLoop(loop);
    }

    @Override
    public void sendInstantMessage(String message){
        Messenger.sendMessage(TAG, myoId, message, publisher);
    }

}
