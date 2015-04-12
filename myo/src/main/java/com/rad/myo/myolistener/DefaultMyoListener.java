package com.rad.myo.myolistener;

import com.rad.myo.MyoRosActivity;
import com.rad.myo.data.MyoData;
import com.thalmic.myo.AbstractDeviceListener;
import com.thalmic.myo.Arm;
import com.thalmic.myo.Myo;
import com.thalmic.myo.Pose;
import com.thalmic.myo.Quaternion;
import com.thalmic.myo.Vector3;
import com.thalmic.myo.XDirection;

import lombok.Getter;
import lombok.Setter;

@Getter
public class DefaultMyoListener extends AbstractDeviceListener {

    private static final int GESTURE_HOLD_THRESHOLD = 1500;

    private final MyoData myoData;
    private final MyoRosActivity parentActivity;
    //TODO think about if this is the right place for gestureStartTime
    @Setter
    private long gestureStartTime;

    public DefaultMyoListener(MyoRosActivity parentActivity){
        this.parentActivity = parentActivity;
        this.myoData = parentActivity.getMyoData();
    }

    @Override
    public void onAttach(Myo myo, long timestamp) {
        // The object for a Myo is unique - in other words, it's safe to compare two Myo references to
        // see if they're referring to the same Myo.
        // Add the Myo object to our list of known Myo devices. This list is used to implement identifyMyo() so
        // that we can give each Myo a nice short identifier.
        GlobalMyoList.add(myo);
    }

    // onConnect() is called whenever a Myo has been connected.
    @Override
    public void onConnect(Myo myo, long timestamp) {
    }

    // onDisconnect() is called whenever a Myo has been disconnected.
    @Override
    public void onDisconnect(Myo myo, long timestamp) {
    }

    // onArmSync() is called whenever Myo has recognized a Sync Gesture after someone has put it on their
    // arm. This lets Myo know which arm it's on and which way it's facing.
    @Override
    public void onArmSync(Myo myo, long timestamp, Arm arm, XDirection xDirection) {
    }

    // onArmUnsync() is called whenever Myo has detected that it was moved from a stable position on a person's arm after
    // it recognized the arm. Typically this happens when someone takes Myo off of their arm, but it can also happen
    // when Myo is moved around on the arm.
    @Override
    public void onArmUnsync(Myo myo, long timestamp) {
    }

    // onUnlock() is called whenever a synced Myo has been unlocked. Under the standard locking
    // policy, that means poses will now be delivered to the listener.
    @Override
    public void onUnlock(Myo myo, long timestamp) {
    }

    // onLock() is called whenever a synced Myo has been locked. Under the standard locking
    // policy, that means poses will no longer be delivered to the listener.
    @Override
    public void onLock(Myo myo, long timestamp) {
    }

    // onPose() is called whenever a Myo provides a new pose.
    @Override
    public void onPose(Myo myo, long timestamp, Pose pose) {
        // Handle the cases of the Pose enumeration, and change the text of the text view
        // based on the pose we receive.
        switch (pose) {
            case UNKNOWN:
                break;
            case REST:
            case DOUBLE_TAP:
                switch (myo.getArm()) {
                    case LEFT:
                        break;
                    case RIGHT:
                        break;
                }
                break;
            case FIST:
                break;
            case WAVE_IN:
                break;
            case WAVE_OUT:
                break;
            case FINGERS_SPREAD:
                break;
        }

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
        myoData.getOrientationData().setOrientationData(rotation);
        myoData.getOrientationData().calculateOffsetRotation(myo);
    }

    @Override
    public void onAccelerometerData(Myo myo, long timestamp, Vector3 accel){
        myoData.getAccelerometerData().setAccelerometerData(accel, timestamp);
    }

    @Override
    public void onGyroscopeData(Myo myo, long timestamp, Vector3 gyro){
        myoData.getGyroData().setGyroData(gyro);
        myoData.getGyroData().offsetGyro();
    }

    public void toggleEnableOnHeldFingerSpreadPose(Myo myo, long timestamp) {
        if(isTimerInProgress()){
            if(!timerLessThanThreshold(timestamp, GESTURE_HOLD_THRESHOLD)) {
                myoData.toggleEnable();
            }
            resetTimer();
        }
    }

    private boolean isTimerInProgress(){
        return gestureStartTime != 0;
    }

    private boolean timerLessThanThreshold(long timestamp, int threshold) {
        return timestamp - gestureStartTime < threshold;
    }

    public void resetTimer() {
        gestureStartTime = 0;
    }
}
