package com.rad.myo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.rad.myo.data.AccelerometerData;
import com.rad.myo.data.GyroData;
import com.rad.myo.data.OrientationData;

import com.rad.myo.data.SensorCalibrator;
import com.rad.myo.myolistener.DefaultMyoListener;
import com.rad.myo.publish.MyoPublisherNode;
import com.thalmic.myo.DeviceListener;
import com.thalmic.myo.Hub;
import com.thalmic.myo.Myo;
import com.thalmic.myo.scanner.ScanActivity;

import org.ros.address.InetAddressFactory;
import org.ros.android.RosActivity;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMainExecutor;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public abstract class MyoRosActivity extends RosActivity {

    public static final String TAG = "MyoActivity";
    private final int ATTACHING_COUNT = 2;

    private Toast mToast;
    private DeviceListener mListener = new DefaultMyoListener(this);
    private ArrayList<Myo> mKnownMyos = new ArrayList<Myo>();

    private List<GyroData> gyroDataList = new ArrayList<GyroData>();
    private List<OrientationData> orientationDataList = new ArrayList<OrientationData>();
    private List<AccelerometerData> accelerometerDataList = new ArrayList<AccelerometerData>();
    private final SensorCalibrator sensorCalibrator = new SensorCalibrator(accelerometerDataList, orientationDataList, gyroDataList);

    private boolean enabled = false;
    private boolean calibrated = false;
    private String gesture = "None";

    private List<MyoPublisherNode> myoPublisherNodeList = new ArrayList<MyoPublisherNode>();
    private NodeConfiguration nodeConfiguration;
    private NodeMainExecutor nodeMainExecutor;
    private boolean execute;

    public MyoRosActivity(String myoBaxter) {
        super(myoBaxter, myoBaxter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeHub(this.mListener);
    }

    protected void initializeHub(DeviceListener mListener) {
        this.mListener = mListener;
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
        hub.addListener(mListener);
    }

    @Override
    protected void onDestroy() {
        //We don't want any callbacks when the Activity is gone, so unregister the listener.
        Hub.getInstance().removeListener(mListener);

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

    protected void showToast(String text) {
        Log.w(TAG, text);
        if (mToast == null) {
            mToast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(text);
        }
        mToast.show();
    }

    public void calibrateSensors(){
        calibrateSensors(this.getCurrentFocus());
    }

    public void calibrateSensors(View view){
        //calibrate all sensors from all myos
        if(haveEnoughSampleBeenCollectedToCalibrate()){
            sensorCalibrator.calibrate();
            showToast(getString(R.string.reset));
        } else{
            showToast(getString(R.string.not_enough_samples));
        }
    }

    private boolean haveEnoughSampleBeenCollectedToCalibrate() {
        return accelerometerDataList.get(0).haveEnoughSamplesBeenCollectedToCalibrate();
    }

    public int identifyMyo(Myo myo) {
        return getMKnownMyos().indexOf(myo);
    }

    @Override
    protected void init(NodeMainExecutor nodeMainExecutor) {
        NodeConfiguration nodeConfiguration = NodeConfiguration.newPublic(InetAddressFactory.newNonLoopback().getHostAddress());
        nodeConfiguration.setMasterUri(getMasterUri());
        setNodeConfiguration(nodeConfiguration);
        setNodeMainExecutor(nodeMainExecutor);
        notifyExecute();
    }

    public synchronized void notifyExecute() {
        setExecute(true);
        notifyAll();
    }

    public void toggleEnable(Myo myo) {
        setEnabled(!isEnabled());
        getMyoPublisherNodeList().get(identifyMyo(myo)).setEnabled(isEnabled());
        //TODO send only enable info
        getMyoPublisherNodeList().get(identifyMyo(myo)).sendInstantMessage();
        if(isEnabled()) {
            showToast(getString(R.string.enable));
        } else {
            showToast(getString(R.string.disable));
        }
    }
}
