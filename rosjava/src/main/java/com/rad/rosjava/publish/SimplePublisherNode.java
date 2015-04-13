package com.rad.rosjava.publish;

import org.ros.concurrent.CancellableLoop;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Publisher;

import java.lang.String;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SimplePublisherNode extends AbstractNodeMain implements PublisherNode {

    private Publisher<std_msgs.String> publisher;
    private String time;

    public SimplePublisherNode(){
    }

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("SimplePublisherNode");
    }

    @Override
    public void onStart(ConnectedNode connectedNode) {
        publisher = connectedNode.newPublisher(GraphName.of("time"), std_msgs.String._TYPE);

        final CancellableLoop loop = new CancellableLoop() {
            @Override
            protected void loop() throws InterruptedException {
                time = new SimpleDateFormat("HH:mm:ss").format(new Date());
                publishMessage();
                Thread.sleep(1000);
            }
        };
        connectedNode.executeCancellableLoop(loop);
    }

    @Override
    public void publishMessage(){
        MessagePublisher.publishString(publisher, time);
    }

}
