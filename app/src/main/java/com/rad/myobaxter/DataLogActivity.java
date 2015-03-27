package com.rad.myobaxter;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import com.rad.myobaxter.Data.AccelSampleData;
import com.thalmic.myo.AbstractDeviceListener;
import com.thalmic.myo.Arm;
import com.thalmic.myo.DeviceListener;
import com.thalmic.myo.Myo;
import com.thalmic.myo.Pose;
import com.thalmic.myo.Quaternion;
import com.thalmic.myo.Vector3;
import com.thalmic.myo.XDirection;

public class DataLogActivity extends MyoActivity {

    private static final String TAG = "DataLogActivity";

    private TextView titleTextView;
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

    // Classes that inherit from AbstractDeviceListener can be used to receive events from Myo devices.
    // If you do not override an event, the default behavior is to do nothing.
    private DeviceListener mListener = new AbstractDeviceListener() {

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

        // onOrientationData() is called whenever a Myo provides its current orientation,
        // represented as a quaternion.
        @Override
        public void onOrientationData(Myo myo, long timestamp, Quaternion rotation) {
            calculateOffsetRotation(myo, timestamp, rotation);
            rollTextView.setText(String.format("%.0f", getRollValue()));
            pitchTextView.setText(String.format("%.0f", getPitchValue()));
            yawTextView.setText(String.format("%.0f", getYawValue()));
        }

        // onPose() is called whenever a Myo provides a new pose.
        @Override
        public void onPose(Myo myo, long timestamp, Pose pose) {
            // Handle the cases of the Pose enumeration, and change the text of the text view
            // based on the pose we receive.
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
                    resetSensors();
                    showToast(getString(R.string.reset));
                    poseTextView.setText(getString(R.string.pose_fist));
                    break;
                case WAVE_IN:
                    poseTextView.setText(getString(R.string.pose_wavein));
                    break;
                case WAVE_OUT:
                    poseTextView.setText(getString(R.string.pose_waveout));
                    break;
                case FINGERS_SPREAD:
                    poseTextView.setText(getString(R.string.pose_fingersspread));
                    break;
            }

            //TODO move elsewhere
            // Tell the Myo to stay unlocked until told otherwise. We do that here so you can
            // hold the poses without the Myo becoming locked.
            myo.unlock(Myo.UnlockType.HOLD);

            if (pose != Pose.UNKNOWN && pose != Pose.REST) {
                // Notify the Myo that the pose has resulted in an action, in this case changing
                // the text on the screen. The Myo will vibrate.
                myo.notifyUserAction();
            }
        }

        @Override
        public void onAccelerometerData(Myo myo, long timestamp, Vector3 accel){
            getOriginalAccel().setTimestamp(timestamp);
            getOriginalAccel().setAccel(accel);
            AccelSampleData accelSampleData = getAccelSampleData();
            accelSampleData.addSample(accel, timestamp);
            if(getCalibratedAccel().getAccel() == null){
                getCalibratedAccel().setTimestamp(timestamp);
                getCalibratedAccel().setAccel(accel);
            }
            calculateVelocityAndPositionFromAcceleration();
            accelXTextView.setText(String.format("%.3f", getAccel().x()));
            accelYTextView.setText(String.format("%.3f", getAccel().y()));
            accelZTextView.setText(String.format("%.3f", getAccel().z()));

            velocityXTextView.setText(String.format("%.3f", getVelocity().x()));
            velocityYTextView.setText(String.format("%.3f", getVelocity().y()));
            velocityZTextView.setText(String.format("%.3f", getVelocity().z()));

            positionXTextView.setText(String.format("%.3f", getPosition().x()));
            positionYTextView.setText(String.format("%.3f", getPosition().y()));
            positionZTextView.setText(String.format("%.3f", getPosition().z()));

        }

        @Override
        public void onGyroscopeData(Myo myo, long timestamp, Vector3 gyro){
            getOriginalGyro().setTimestamp(timestamp);
            getOriginalGyro().setGyro(gyro);
            if(getCalibratedGyro().getGyro() == null){
                getCalibratedGyro().setTimestamp(timestamp);
                getCalibratedGyro().setGyro(gyro);
            }
            gyro.subtract(getCalibratedGyro().getGyro());
            gyroXTextView.setText(String.format("%.0f", gyro.x()));
            gyroYTextView.setText(String.format("%.0f", gyro.y()));
            gyroZTextView.setText(String.format("%.0f", gyro.z()));
        }
    };

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
}
