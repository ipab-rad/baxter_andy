package com.rad.myobaxter;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.widget.TextView;

import com.rad.myobaxter.data.AccelerometerData;
import com.rad.myobaxter.data.GyroData;
import com.rad.myobaxter.data.OrientationData;
import com.rad.myobaxter.publish.MyoPublisherNode;
import com.rad.myobaxter.utils.LogUtils;
import com.thalmic.myo.AbstractDeviceListener;
import com.thalmic.myo.Arm;
import com.thalmic.myo.DeviceListener;
import com.thalmic.myo.Myo;
import com.thalmic.myo.Pose;
import com.thalmic.myo.Quaternion;
import com.thalmic.myo.Vector3;
import com.thalmic.myo.XDirection;

import org.ros.address.InetAddressFactory;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMainExecutor;

import java.util.ArrayList;
import java.util.List;

public class DataLogActivity extends MyoActivity {

    private static final String TAG = "DataLogActivity";

    // We store each Myo object that we attach to in this list, so that we can keep track of the order we've seen
    // each Myo and give it a unique short identifier (see onAttach() and identifyMyo() below).

    private TextView mLockStateView;
    private TextView connectedTextView;
    private TextView armSyncTextView;
    private TextView rollTextView;
    private TextView pitchTextView;
    private TextView yawTextView;
    private TextView poseTextView;
    private TextView accelXTextView;
    private TextView accelYTextView;
    private TextView accelZTextView;
    private TextView velocityXTextView;
    private TextView velocityYTextView;
    private TextView velocityZTextView;
    private TextView positionXTextView;
    private TextView positionYTextView;
    private TextView positionZTextView;
    private TextView gyroXTextView;
    private TextView gyroYTextView;
    private TextView gyroZTextView;

    private long gestureStartTime;
    private List<MyoPublisherNode> myoPublisherNodeList = new ArrayList<MyoPublisherNode>();
    private NodeConfiguration nodeConfiguration;
    private NodeMainExecutor nodeMainExecutor;
    private boolean execute;

    public DataLogActivity() {
        super("MyoBaxterDataLog");
    }
    // Classes that inherit from AbstractDeviceListener can be used to receive events from Myo devices.
    // If you do not override an event, the default behavior is to do nothing.
    private DeviceListener mListener = new AbstractDeviceListener() {

        @Override
        public void onAttach(Myo myo, long timestamp) {
            // The object for a Myo is unique - in other words, it's safe to compare two Myo references to
            // see if they're referring to the same Myo.
            // Add the Myo object to our list of known Myo devices. This list is used to implement identifyMyo() below so
            // that we can give each Myo a nice short identifier.
            getMKnownMyos().add(myo);
            initMyo(myo);
        }

        // onConnect() is called whenever a Myo has been connected.
        @Override
        public void onConnect(Myo myo, long timestamp) {
            connectedTextView.setText(R.string.connected);
            connectedTextView.setTextColor(Color.GREEN);
        }

        // onDisconnect() is called whenever a Myo has been disconnected.
        @Override
        public void onDisconnect(Myo myo, long timestamp) {
            connectedTextView.setText(R.string.disconnected);
            connectedTextView.setTextColor(Color.RED);
        }

        // onArmSync() is called whenever Myo has recognized a Sync Gesture after someone has put it on their
        // arm. This lets Myo know which arm it's on and which way it's facing.
        @Override
        public void onArmSync(Myo myo, long timestamp, Arm arm, XDirection xDirection) {
            armSyncTextView.setText(myo.getArm() == Arm.LEFT ? R.string.arm_left : R.string.arm_right);
        }

        // onArmUnsync() is called whenever Myo has detected that it was moved from a stable position on a person's arm after
        // it recognized the arm. Typically this happens when someone takes Myo off of their arm, but it can also happen
        // when Myo is moved around on the arm.
        @Override
        public void onArmUnsync(Myo myo, long timestamp) {
            armSyncTextView.setText(R.string.unsynced);
        }

        // onUnlock() is called whenever a synced Myo has been unlocked. Under the standard locking
        // policy, that means poses will now be delivered to the listener.
        @Override
        public void onUnlock(Myo myo, long timestamp) {
            mLockStateView.setText(R.string.unlocked);
        }

        // onLock() is called whenever a synced Myo has been locked. Under the standard locking
        // policy, that means poses will no longer be delivered to the listener.
        @Override
        public void onLock(Myo myo, long timestamp) {
            mLockStateView.setText(R.string.locked);
        }

        // onPose() is called whenever a Myo provides a new pose.
        @Override
        public void onPose(Myo myo, long timestamp, Pose pose) {
            toggleEnableOnHeldFingerSpreadPose(myo, timestamp);
            // Handle the cases of the Pose enumeration, and change the text of the text view
            // based on the pose we receive.
            int myoId = identifyMyo(myo);
            switch (pose) {
                case UNKNOWN:
                    poseTextView.setText(getString(R.string.unknown));
                    break;
                case REST:
                case DOUBLE_TAP:
                    int restTextId = R.string.hello_world;
                    switch (myo.getArm()) {
                        case LEFT:
                            restTextId = R.string.arm_left;
                            break;
                        case RIGHT:
                            restTextId = R.string.arm_right;
                            break;
                    }
                    poseTextView.setText(getString(restTextId));
                    break;
                case FIST:
                    if(isEnabled()){
                        myoPublisherNodeList.get(myoId).setGesture(getString(R.string.pose_fist));
                    } else {
                        setCalibrated(true);
                        calibrateSensors(getCurrentFocus());
                        myoPublisherNodeList.get(myoId).setCalibrated(isCalibrated());
                    }
                    poseTextView.setText(getString(R.string.pose_fist));
                    break;
                case WAVE_IN:
                    myoPublisherNodeList.get(myoId).setGesture(getString(R.string.pose_wavein));
                    poseTextView.setText(getString(R.string.pose_wavein));
                    break;
                case WAVE_OUT:
                    myoPublisherNodeList.get(myoId).setGesture(getString(R.string.pose_waveout));
                    poseTextView.setText(getString(R.string.pose_waveout));
                    break;
                case FINGERS_SPREAD:
                    myoPublisherNodeList.get(myoId).setGesture(getString(R.string.pose_fingersspread));
                    poseTextView.setText(getString(R.string.pose_fingersspread));
                    gestureStartTime = timestamp;
                    break;
            }
//            myoPublisherNodeList.get(myoId).sendInstantMessage();

            if (pose != Pose.UNKNOWN && pose != Pose.REST) {
                // Notify the Myo that the pose has resulted in an action, in this case changing
                // the text on the screen. The Myo will vibrate.
                myo.notifyUserAction();
            }
        }

        // onOrientationData() is called whenever a Myo provides its current orientation,
        // represented as a quaternion.
        @Override
        public void onOrientationData(Myo myo, long timestamp, Quaternion rotation) {
            int myoId = identifyMyo(myo);
            OrientationData orientationData = getOrientationDataList().get(myoId);
            orientationData.setOrientationData(rotation);
            orientationData.calculateOffsetRotation(myo);
            rollTextView.setText(String.format("%.0f", orientationData.getRoll()));
            pitchTextView.setText(String.format("%.0f", orientationData.getPitch()));
            yawTextView.setText(String.format("%.0f", orientationData.getYaw()));
        }

        //TODO turn off if not using
        @Override
        public void onAccelerometerData(Myo myo, long timestamp, Vector3 accel){

            int myoId = identifyMyo(myo);
            AccelerometerData accelerometerData = getAccelerometerDataList().get(myoId);
            accelerometerData.setAccelerometerData(accel, timestamp);
            accelerometerData.calculateVelocityAndPositionFromAcceleration();
            LogUtils.logAccelerometerData(TAG, timestamp, accelerometerData);

            accelXTextView.setText(String.format("%.3f", accelerometerData.getAcceleration().x()));
            accelYTextView.setText(String.format("%.3f", accelerometerData.getAcceleration().y()));
            accelZTextView.setText(String.format("%.3f", accelerometerData.getAcceleration().z()));

            velocityXTextView.setText(String.format("%.3f", accelerometerData.getVelocity().x()));
            velocityYTextView.setText(String.format("%.3f", accelerometerData.getVelocity().y()));
            velocityZTextView.setText(String.format("%.3f", accelerometerData.getVelocity().z()));

            positionXTextView.setText(String.format("%.3f", accelerometerData.getPosition().x()));
            positionYTextView.setText(String.format("%.3f", accelerometerData.getPosition().y()));
            positionZTextView.setText(String.format("%.3f", accelerometerData.getPosition().z()));
        }

        @Override
        public void onGyroscopeData(Myo myo, long timestamp, Vector3 gyro){
            int myoId = identifyMyo(myo);
            GyroData gyroData = getGyroDataList().get(myoId);
            gyroData.setGyroData(gyro);
            gyroData.offsetGyro();
            gyroXTextView.setText(String.format("%.0f", gyroData.getGyro().x()));
            gyroYTextView.setText(String.format("%.0f", gyroData.getGyro().y()));
            gyroZTextView.setText(String.format("%.0f", gyroData.getGyro().z()));
        }
    };

    private void initMyo(Myo myo) {
        OrientationData orientationData = new OrientationData();
        getAccelerometerDataList().add(new AccelerometerData(orientationData));
        getOrientationDataList().add(orientationData);
        getGyroDataList().add(new GyroData());

        int myoId = identifyMyo(myo);
        // Now that we've added it to our list, get our short ID for it and print it out.
        Log.i(TAG, "Attached to " + myo.getMacAddress() + ", now known as Myo " + myoId + ".");

        myoPublisherNodeList.add(new MyoPublisherNode(myoId, getAccelerometerDataList().get(myoId), getOrientationDataList().get(myoId)));

        guardedExecute(myoId);


    }

    private void executePublisherNode(int myoId) {
        if(nodeMainExecutor != null) {
            nodeMainExecutor.execute(myoPublisherNodeList.get(myoId), nodeConfiguration);
        }
    }

    public synchronized void guardedExecute(int myoId) {
        // This guard only loops once for each special event, which may not
        // be the event we're waiting for.
        while(!execute) {
            try {
                wait();
            } catch (InterruptedException e) {}
        }
        executePublisherNode(myoId);
    }

    public synchronized void notifyExecute() {
        execute = true;
        notifyAll();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_log);

        mLockStateView = (TextView) findViewById(R.id.locked);
        connectedTextView = (TextView) findViewById(R.id.connection_status);
        armSyncTextView = (TextView) findViewById(R.id.sync_status);
        rollTextView = (TextView) findViewById(R.id.rollValue);
        pitchTextView = (TextView) findViewById(R.id.pitchValue);
        yawTextView = (TextView) findViewById(R.id.yawValue);
        poseTextView = (TextView) findViewById(R.id.poseValue);
        accelXTextView = (TextView) findViewById(R.id.accelXValue);
        accelYTextView = (TextView) findViewById(R.id.accelYValue);
        accelZTextView = (TextView) findViewById(R.id.accelZValue);
        velocityXTextView = (TextView) findViewById(R.id.velocityXValue);
        velocityYTextView = (TextView) findViewById(R.id.velocityYValue);
        velocityZTextView = (TextView) findViewById(R.id.velocityZValue);
        positionXTextView = (TextView) findViewById(R.id.positionXValue);
        positionYTextView = (TextView) findViewById(R.id.positionYValue);
        positionZTextView = (TextView) findViewById(R.id.positionZValue);
        gyroXTextView = (TextView) findViewById(R.id.gyroXValue);
        gyroYTextView = (TextView) findViewById(R.id.gyroYValue);
        gyroZTextView = (TextView) findViewById(R.id.gyroZValue);

        initializeHub(mListener);
    }

    @Override
    protected void init(NodeMainExecutor nodeMainExecutor) {
        NodeConfiguration nodeConfiguration = NodeConfiguration.newPublic(InetAddressFactory.newNonLoopback().getHostAddress());
        nodeConfiguration.setMasterUri(getMasterUri());
        this.nodeConfiguration = nodeConfiguration;
        this.nodeMainExecutor = nodeMainExecutor;
        notifyExecute();
    }

    private void toggleEnableOnHeldFingerSpreadPose(Myo myo, long timestamp) {
        if(isTimerInProgress()){
            if(timerLessThanThreshold(timestamp, 1500)) {
                resetTimer();
            } else {
                toggleEnable(myo);
            }
        }
    }

    private boolean isTimerInProgress(){
        return gestureStartTime != 0;
    }

    private boolean timerLessThanThreshold(long timestamp, int i) {
        return timestamp - gestureStartTime < 3000;
    }

    private void resetTimer() {
        gestureStartTime = 0;
    }

    private void toggleEnable(Myo myo) {
        setEnabled(!isEnabled());
        myoPublisherNodeList.get(identifyMyo(myo)).setEnabled(isEnabled());
        //TODO send only enable info
        myoPublisherNodeList.get(identifyMyo(myo)).sendInstantMessage();
        if(isEnabled()) {
            showToast(getString(R.string.enable));
        } else {
            showToast(getString(R.string.disable));
        }
        resetTimer();
    }
}
