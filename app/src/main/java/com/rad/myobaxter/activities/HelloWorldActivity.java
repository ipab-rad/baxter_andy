package com.rad.myobaxter.activities;

import android.os.Bundle;

import com.rad.myo.MyoRosActivity;
import com.rad.myobaxter.R;
import com.rad.myobaxter.myolistener.MyoHelloWorldListener;
import com.rad.rosjava.publish.SimplePublisherNode;

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
        initNode(nodeMainExecutor, new SimplePublisherNode());
    }
}
