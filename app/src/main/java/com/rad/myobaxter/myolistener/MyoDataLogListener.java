package com.rad.myobaxter.myolistener;

import android.graphics.Color;

import com.rad.myo.MyoRosActivity;
import com.rad.myo.myolistener.DefaultMyoListener;
import com.rad.myo.data.AccelerometerData;
import com.rad.myo.data.GyroData;
import com.rad.myo.data.OrientationData;
import com.rad.myobaxter.R;
import com.rad.myobaxter.utils.TextViewEditor;
import com.thalmic.myo.Arm;
import com.thalmic.myo.Myo;
import com.thalmic.myo.Pose;
import com.thalmic.myo.Quaternion;
import com.thalmic.myo.Vector3;
import com.thalmic.myo.XDirection;

public class MyoDataLogListener extends DefaultMyoListener {

    private TextViewEditor textViewEditor;

    public MyoDataLogListener(MyoRosActivity parentActivity){
        super(parentActivity);
        textViewEditor = new TextViewEditor(parentActivity);
    }

    @Override
    public void onConnect(Myo myo, long timestamp) {
        textViewEditor.setTextView(R.id.connection_status, R.string.myo_data_connected);
        textViewEditor.setTextColor(R.id.connection_status, Color.GREEN);
    }

    @Override
    public void onDisconnect(Myo myo, long timestamp) {
        textViewEditor.setTextView(R.id.connection_status, R.string.myo_data_disconnected);
        textViewEditor.setTextColor(R.id.connection_status, Color.RED);
    }

    @Override
    public void onArmSync(Myo myo, long timestamp, Arm arm, XDirection xDirection) {
        if(myo.getArm() == Arm.LEFT) {
            textViewEditor.setTextView(R.id.sync_status, R.string.myo_data_arm_left);
        } else {
            textViewEditor.setTextView(R.id.sync_status, R.string.myo_data_arm_right);
        }
    }

    @Override
    public void onArmUnsync(Myo myo, long timestamp) {
        textViewEditor.setTextView(R.id.sync_status, R.string.myo_data_unsynced);
    }

    @Override
    public void onUnlock(Myo myo, long timestamp) {
        textViewEditor.setTextView(R.id.locked, R.string.myo_data_unlocked);
    }

    @Override
    public void onLock(Myo myo, long timestamp) {
        textViewEditor.setTextView(R.id.locked, R.string.myo_data_locked);
    }

    @Override
    public void onPose(Myo myo, long timestamp, Pose pose) {
        toggleEnableOnHeldFingerSpreadPose(timestamp);
        switch (pose) {
            case UNKNOWN:
                textViewEditor.setTextView(R.id.poseValue, R.string.myo_data_unknown);
                break;
            case REST:
            case DOUBLE_TAP:
                int restTextId = R.string.myo_data_unsynced;
                switch (myo.getArm()) {
                    case LEFT:
                        restTextId = R.string.myo_data_arm_left;
                        break;
                    case RIGHT:
                        restTextId = R.string.myo_data_arm_right;
                        break;
                }
                textViewEditor.setTextView(R.id.poseValue, restTextId);
                break;
            case FIST:
                if(getMyoData().isEnabled()){
                    getMyoData().setGesture(getParentActivity().getString(R.string.myo_data_pose_fist));
                } else {
                    getMyoData().calibrateSensors();
                }
                textViewEditor.setTextView(R.id.poseValue, R.string.myo_data_pose_fist);
                break;
            case WAVE_IN:
                getMyoData().setGesture(getParentActivity().getString(R.string.myo_data_pose_wavein));
                textViewEditor.setTextView(R.id.poseValue, R.string.myo_data_pose_wavein);
                break;
            case WAVE_OUT:
                getMyoData().setGesture(getParentActivity().getString(R.string.myo_data_pose_waveout));
                textViewEditor.setTextView(R.id.poseValue, R.string.myo_data_pose_fingersspread);
                break;
            case FINGERS_SPREAD:
                getMyoData().setGesture(getParentActivity().getString(R.string.myo_data_pose_fingersspread));
                textViewEditor.setTextView(R.id.poseValue, R.string.myo_data_pose_fingersspread);
                setGestureStartTime(timestamp);
                break;
        }

        if (pose != Pose.UNKNOWN && pose != Pose.REST) {
            myo.notifyUserAction();
        }
    }

    @Override
    public void onOrientationData(Myo myo, long timestamp, Quaternion rotation) {
        super.onOrientationData(myo, timestamp, rotation);
        OrientationData orientationData = getMyoData().getOrientationData();
        textViewEditor.setTextView(R.id.rollValue, String.format("%.0f", Math.toDegrees(orientationData.getOffsetRoll())));
        textViewEditor.setTextView(R.id.pitchValue, String.format("%.0f", Math.toDegrees(orientationData.getOffsetPitch())));
        textViewEditor.setTextView(R.id.yawValue, String.format("%.0f", Math.toDegrees(orientationData.getOffsetYaw())));
    }

    @Override
    public void onAccelerometerData(Myo myo, long timestamp, Vector3 accel){
        super.onAccelerometerData(myo, timestamp, accel);
        AccelerometerData accelerometerData = getMyoData().getAccelerometerData();
        accelerometerData.calculateVelocityAndPositionFromAcceleration();

        textViewEditor.setTextView(R.id.accelXValue, String.format("%.3f", accelerometerData.getAcceleration().x()));
        textViewEditor.setTextView(R.id.accelYValue, String.format("%.3f", accelerometerData.getAcceleration().y()));
        textViewEditor.setTextView(R.id.accelZValue, String.format("%.3f", accelerometerData.getAcceleration().z()));

        textViewEditor.setTextView(R.id.velocityXValue, String.format("%.3f", accelerometerData.getVelocity().x()));
        textViewEditor.setTextView(R.id.velocityYValue, String.format("%.3f", accelerometerData.getVelocity().y()));
        textViewEditor.setTextView(R.id.velocityZValue, String.format("%.3f", accelerometerData.getVelocity().z()));

        textViewEditor.setTextView(R.id.positionXValue, String.format("%.3f", accelerometerData.getPosition().x()));
        textViewEditor.setTextView(R.id.positionYValue, String.format("%.3f", accelerometerData.getPosition().y()));
        textViewEditor.setTextView(R.id.positionZValue, String.format("%.3f", accelerometerData.getPosition().z()));
    }

    @Override
    public void onGyroscopeData(Myo myo, long timestamp, Vector3 gyro){
        super.onGyroscopeData(myo, timestamp, gyro);
        GyroData gyroData = getMyoData().getGyroData();
        textViewEditor.setTextView(R.id.gyroXValue, String.format("%.0f", gyroData.getGyro().x()));
        textViewEditor.setTextView(R.id.gyroYValue, String.format("%.0f", gyroData.getGyro().y()));
        textViewEditor.setTextView(R.id.gyroZValue, String.format("%.0f", gyroData.getGyro().z()));
    }
}
