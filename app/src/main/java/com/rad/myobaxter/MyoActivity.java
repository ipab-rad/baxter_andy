package com.rad.myobaxter;

import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.rad.myobaxter.data.AccelSampleData;
import com.rad.myobaxter.data.AccelerometerData;
import com.rad.myobaxter.data.GyroData;
import com.rad.myobaxter.data.OrientationData;
import com.rad.myobaxter.sensor.SensorCalibrator;

import com.thalmic.myo.DeviceListener;
import com.thalmic.myo.Hub;
import com.thalmic.myo.Myo;
import com.thalmic.myo.scanner.ScanActivity;

import org.ros.android.RosActivity;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public abstract class MyoActivity extends RosActivity {

    private static final String TAG = "MyoActivity";
    private final double ZERO_ACCEL_THRESHOLD = 0.1;
    private final double VELOCITY_DEGRADER = 0.9;
    private final int ATTACHING_COUNT = 2;

    private Toast mToast;
    private DeviceListener mListener;
    private ArrayList<Myo> mKnownMyos = new ArrayList<Myo>();

    private List<GyroData> gyroDataList = new ArrayList<GyroData>();
    private List<OrientationData> orientationDataList = new ArrayList<OrientationData>();
    private List<AccelerometerData> accelerometerDataList = new ArrayList<AccelerometerData>();
    private boolean enabled = false;
    private boolean calibrated = false;
    private String gesture = "None";

    public MyoActivity(String myoBaxter) {
        super(myoBaxter, myoBaxter);
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

        // Set the maximum number of simultaneously attached Myos to ATTACHING_COUNT.
        hub.setMyoAttachAllowance(ATTACHING_COUNT);
        Log.i(TAG, "Attaching to " + ATTACHING_COUNT + " Myo armbands.");

        // attachToAdjacentMyos() attaches to Myo devices that are physically very near to the Bluetooth radio
        // until it has attached to the provided count.
        // DeviceListeners attached to the hub will receive onAttach() events once attaching has completed.
        hub.attachToAdjacentMyos(ATTACHING_COUNT);

        // Next, register for DeviceListener callbacks.
        hub.addListener(mListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //We don't want any callbacks when the Activity is gone, so unregister the listener.
        Hub.getInstance().removeListener(mListener);

        if (isFinishing()) {
            // The Activity is finishing, so shutdown the Hub. This will disconnect from the Myo.
            Hub.getInstance().shutdown();
        }
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

    public void calibrateSensors(View view){
        //calibrate all sensors from all myos
        SensorCalibrator sensorCalibrator = new SensorCalibrator(accelerometerDataList, orientationDataList, gyroDataList);
        if(accelerometerDataList.get(0).getAccelSampleData().getSamples().size() > AccelSampleData.CALIBRATED_SAMPLE_SIZE){
            sensorCalibrator.calibrate();
            showToast(getString(R.string.reset));
        } else{
            showToast(getString(R.string.not_enough_samples));
        }
    }

    protected int identifyMyo(Myo myo) {
        return getMKnownMyos().indexOf(myo);
    }
}
