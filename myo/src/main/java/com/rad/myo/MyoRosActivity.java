package com.rad.myo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.rad.myo.data.MyoData;
import com.rad.myo.myolistener.DefaultMyoListener;
import com.rad.myo.publish.MyoPublisherNode;
import com.thalmic.myo.DeviceListener;
import com.thalmic.myo.Hub;
import com.thalmic.myo.scanner.ScanActivity;

import org.ros.address.InetAddressFactory;
import org.ros.android.RosActivity;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMain;
import org.ros.node.NodeMainExecutor;

public abstract class MyoRosActivity extends RosActivity {

    public static final String TAG = "MyoActivity";
    private final int ATTACHING_COUNT = 1;

    private MyoData myoData;
    private MyoPublisherNode myoPublisherNode;
    private DeviceListener myoListener;

    public MyoRosActivity(String activityIdentifier) {
        super(activityIdentifier, activityIdentifier);
        //Use below if we want to by pass MasterURIChooser and hardcode
        //super(activityIdentifier, activityIdentifier, URI.create("http://localhost:11311"));
        myoData = new MyoData();
        myoPublisherNode = new MyoPublisherNode(myoData);
        myoListener = new DefaultMyoListener(this);
    }

    public void setMyoListener(DeviceListener myoListener){
        this.myoListener = myoListener;
    }

    public MyoData getMyoData(){
        return myoData;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeHub(this.myoListener);
    }

    protected void initializeHub(DeviceListener myoListener) {
        this.myoListener = myoListener;
        // First, we initialize the Hub singleton with an application identifier.
        Hub hub = Hub.getInstance();
        if (!hub.init(this, getPackageName())) {
            // We can't do anything with the Myo device if the Hub can't be initialized, so exit.
            Toast.makeText(this, "Couldn't initialize Hub", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        hub.setLockingPolicy(Hub.LockingPolicy.NONE);

        //TODO the number of Myos that can be attached should not be hard coded
        //TODO perhaps when this changed to a fragment it makes sense to only have one Myo per fragment
        // Set the maximum number of simultaneously attached Myos to ATTACHING_COUNT.
        hub.setMyoAttachAllowance(ATTACHING_COUNT);
        Log.i(TAG, "Attaching to " + ATTACHING_COUNT + " Myo armbands.");

        // attaches to Myo devices that are physically very near to the Bluetooth radio
        // until it has attached to the provided count.
        // DeviceListeners attached to the hub will receive onAttach() events once attaching has completed.
        hub.attachToAdjacentMyos(ATTACHING_COUNT);

        // Next, register for DeviceListener callbacks.
        hub.addListener(myoListener);
    }

    @Override
    protected void onDestroy() {
        //We don't want any callbacks when the Activity is gone, so unregister the listener.
        Hub.getInstance().removeListener(myoListener);

        if (isFinishing()) {
            // The Activity is finishing, so shutdown the Hub. This will disconnect from the Myo.
            Hub.getInstance().shutdown();
        }
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (R.id.action_scan == id) {
            onScanActionSelected();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onScanActionSelected() {
        // Launch the ScanActivity to scan for Myos to connect to.
        Intent intent = new Intent(this, ScanActivity.class);
        startActivity(intent);
    }

    public void calibrateSensors(View view){
        myoData.calibrateSensors();
    }

    public void toggleEnable(View view){
        myoData.toggleEnable();
    }

    public void sendGripperSignal(View view){
        myoData.sendGripperSignal();
    }

    @Override
    protected void init(NodeMainExecutor nodeMainExecutor) {
        initNode(nodeMainExecutor, myoPublisherNode);
    }

    public void initNode(NodeMainExecutor nodeMainExecutor, NodeMain node) {
        NodeConfiguration nodeConfiguration = NodeConfiguration.newPublic(InetAddressFactory.newNonLoopback().getHostAddress());
        nodeConfiguration.setMasterUri(getMasterUri());
        nodeMainExecutor.execute(node, nodeConfiguration);
    }
}
