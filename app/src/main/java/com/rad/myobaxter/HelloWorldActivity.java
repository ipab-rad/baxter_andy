package com.rad.myobaxter;

import android.os.Bundle;

import com.rad.myo.MyoRosActivity;
import com.rad.myo.publish.SimplePublisherNode;
import com.rad.myobaxter.myolistener.MyoHelloWorldListener;

import org.ros.address.InetAddressFactory;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMain;
import org.ros.node.NodeMainExecutor;

public class HelloWorldActivity extends MyoRosActivity {
    public static final String TAG = "HelloWorldActivity";

    public HelloWorldActivity() {
        super("MyoBaxterHellowWorld");
        setMyoListener(new MyoHelloWorldListener(this));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hello_world);
    }

    @Override
    protected void init(NodeMainExecutor nodeMainExecutor) {
        NodeMain node = new SimplePublisherNode(0);

        NodeConfiguration nodeConfiguration = NodeConfiguration.newPublic(InetAddressFactory.newNonLoopback().getHostAddress());
        nodeConfiguration.setMasterUri(getMasterUri());

        nodeMainExecutor.execute(node, nodeConfiguration);
    }
}
