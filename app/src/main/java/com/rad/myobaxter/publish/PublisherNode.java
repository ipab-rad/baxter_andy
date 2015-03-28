package com.rad.myobaxter.publish;

import com.thalmic.myo.Myo;

import org.ros.node.NodeMain;

public interface PublisherNode extends NodeMain {
    public void sendInstantMessage(String message);
}
