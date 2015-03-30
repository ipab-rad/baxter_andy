package com.rad.myobaxter.publish;

import org.ros.node.NodeMain;

public interface PublisherNode extends NodeMain {
    public void sendInstantMessage();
}
