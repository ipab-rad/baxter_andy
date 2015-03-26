package com.rad.myobaxter;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import com.thalmic.myo.AbstractDeviceListener;
import com.thalmic.myo.Arm;
import com.thalmic.myo.DeviceListener;
import com.thalmic.myo.Myo;
import com.thalmic.myo.Pose;
import com.thalmic.myo.Quaternion;
import com.thalmic.myo.Vector3;
import com.thalmic.myo.XDirection;

public class HelloWorldActivity extends MyoActivity {
    private static final String TAG = "HelloWorldActivity";

    private TextView mLockStateView;
    private TextView mTextView;

    // Classes that inherit from AbstractDeviceListener can be used to receive events from Myo devices.
    // If you do not override an event, the default behavior is to do nothing.
    private DeviceListener mListener = new AbstractDeviceListener() {

        // onConnect() is called whenever a Myo has been connected.
        @Override
        public void onConnect(Myo myo, long timestamp) {
            // Set the text color of the text view to cyan when a Myo connects.
            mTextView.setTextColor(Color.CYAN);
        }

        // onDisconnect() is called whenever a Myo has been disconnected.
        @Override
        public void onDisconnect(Myo myo, long timestamp) {
            // Set the text color of the text view to red when a Myo disconnects.
            mTextView.setTextColor(Color.RED);
        }

        // onArmSync() is called whenever Myo has recognized a Sync Gesture after someone has put it on their
        // arm. This lets Myo know which arm it's on and which way it's facing.
        @Override
        public void onArmSync(Myo myo, long timestamp, Arm arm, XDirection xDirection) {
            mTextView.setText(myo.getArm() == Arm.LEFT ? R.string.arm_left : R.string.arm_right);
        }

        // onArmUnsync() is called whenever Myo has detected that it was moved from a stable position on a person's arm after
        // it recognized the arm. Typically this happens when someone takes Myo off of their arm, but it can also happen
        // when Myo is moved around on the arm.
        @Override
        public void onArmUnsync(Myo myo, long timestamp) {
            mTextView.setText(R.string.hello_world);
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
            // Next, we apply a rotation to the text view using the roll, pitch, and yaw.
            mTextView.setRotation(getRollValue());
            mTextView.setRotationX(getPitchValue());
            mTextView.setRotationY(getYawValue());
        }

        // onPose() is called whenever a Myo provides a new pose.
        @Override
        public void onPose(Myo myo, long timestamp, Pose pose) {
            // Handle the cases of the Pose enumeration, and change the text of the text view
            // based on the pose we receive.
            switch (pose) {
                case UNKNOWN:
                    mTextView.setText(getString(R.string.hello_world));
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
                    mTextView.setText(getString(restTextId));
                    break;
                case FIST:
                    getCalibratedAccel().setAccel(getOriginalAccel().getAccel());
                    getCalibratedGyro().setGyro(getOriginalGyro().getGyro());
                    getCalibratedRotation().setRotation(getOriginalRotation().getRotation());
                    showToast(getString(R.string.reset));
                    mTextView.setText(getString(R.string.pose_fist));
                    break;
                case WAVE_IN:
                    mTextView.setText(getString(R.string.pose_wavein));
                    break;
                case WAVE_OUT:
                    mTextView.setText(getString(R.string.pose_waveout));
                    break;
                case FINGERS_SPREAD:
                    mTextView.setText(getString(R.string.pose_fingersspread));
                    break;
            }

            myo.unlock(Myo.UnlockType.HOLD);

            if (pose != Pose.UNKNOWN && pose != Pose.REST) {
//                // Tell the Myo to stay unlocked until told otherwise. We do that here so you can
//                // hold the poses without the Myo becoming locked.
//                myo.unlock(Myo.UnlockType.HOLD);
//
//                // Notify the Myo that the pose has resulted in an action, in this case changing
//                // the text on the screen. The Myo will vibrate.
                myo.notifyUserAction();
            } else {
//                // Tell the Myo to stay unlocked only for a short period. This allows the Myo to
//                // stay unlocked while poses are being performed, but lock after inactivity.
//                myo.unlock(Myo.UnlockType.TIMED);
            }
        }

        @Override
        public void onAccelerometerData(Myo myo, long timestamp, Vector3 accel){
            getOriginalAccel().setAccel(accel);
        }

        @Override
        public void onGyroscopeData(Myo myo, long timestamp, Vector3 gyro){
            getOriginalGyro().setGyro(gyro);
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hello_world);

        mLockStateView = (TextView) findViewById(R.id.lock_state);
        mTextView = (TextView) findViewById(R.id.text);
        initializeHub(mListener);
    }
}
