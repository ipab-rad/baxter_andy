package com.rad.rosjava.publish;

import org.ros.node.NodeMain;

public interface PublisherNode extends NodeMain {
    public void publishMessage();
}
