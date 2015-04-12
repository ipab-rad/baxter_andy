package com.rad.myobaxter.myolistener;

import android.graphics.Color;

import com.rad.myo.myolistener.DefaultMyoListener;
import com.rad.myo.MyoRosActivity;
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

    TextViewEditor textViewEditor;
    
    public MyoDataLogListener(MyoRosActivity myoRosActivity){
        super(myoRosActivity);
        textViewEditor = new TextViewEditor(myoRosActivity);
    }

    @Override
    public void onConnect(Myo myo, long timestamp) {
        textViewEditor.setTextView(R.id.connection_status, R.string.connected);
        textViewEditor.setTextColor(R.id.connection_status, Color.GREEN);
    }

    @Override
    public void onDisconnect(Myo myo, long timestamp) {
        textViewEditor.setTextView(R.id.connection_status, R.string.disconnected);
        textViewEditor.setTextColor(R.id.connection_status, Color.RED);
    }

    @Override
    public void onArmSync(Myo myo, long timestamp, Arm arm, XDirection xDirection) {
        if(myo.getArm() == Arm.LEFT) {
            textViewEditor.setTextView(R.id.sync_status, R.string.arm_left);
        } else {
            textViewEditor.setTextView(R.id.sync_status, R.string.arm_right);
        }
    }

    @Override
    public void onArmUnsync(Myo myo, long timestamp) {
        textViewEditor.setTextView(R.id.sync_status, R.string.unsynced);
    }

    @Override
    public void onUnlock(Myo myo, long timestamp) {
        textViewEditor.setTextView(R.id.locked, R.string.unlocked);
    }

    @Override
    public void onLock(Myo myo, long timestamp) {
        textViewEditor.setTextView(R.id.locked, R.string.locked);
    }

    @Override
    public void onPose(Myo myo, long timestamp, Pose pose) {
        toggleEnableOnHeldFingerSpreadPose(myo, timestamp);
        int myoId = getActivity().identifyMyo(myo);
        switch (pose) {
            case UNKNOWN:
                textViewEditor.setTextView(R.id.poseValue, R.string.unknown);
                break;
            case REST:
            case DOUBLE_TAP:
                int restTextId = R.string.unsynced;
                switch (myo.getArm()) {
                    case LEFT:
                        restTextId = R.string.arm_left;
                        break;
                    case RIGHT:
                        restTextId = R.string.arm_right;
                        break;
                }
                textViewEditor.setTextView(R.id.poseValue, restTextId);
                break;
            case FIST:
                if(getActivity().isEnabled()){
                    getActivity().getMyoPublisherNodeList().get(myoId).setGesture(getActivity().getString(R.string.pose_fist));
                } else {
                    getActivity().setCalibrated(true);
                    getActivity().calibrateSensors();
                    getActivity().getMyoPublisherNodeList().get(myoId).setCalibrated(getActivity().isCalibrated());
                }
                textViewEditor.setTextView(R.id.poseValue, R.string.pose_fist);
                break;
            case WAVE_IN:
                getActivity().getMyoPublisherNodeList().get(myoId).setGesture(getActivity().getString(R.string.pose_wavein));
                textViewEditor.setTextView(R.id.poseValue, R.string.pose_wavein);
                break;
            case WAVE_OUT:
                getActivity().getMyoPublisherNodeList().get(myoId).setGesture(getActivity().getString(R.string.pose_waveout));
                textViewEditor.setTextView(R.id.poseValue, R.string.pose_fingersspread);
                break;
            case FINGERS_SPREAD:
                getActivity().getMyoPublisherNodeList().get(myoId).setGesture(getActivity().getString(R.string.pose_fingersspread));
                textViewEditor.setTextView(R.id.poseValue, R.string.pose_fingersspread);
                setGestureStartTime(timestamp);
                break;
        }
//            myoPublisherNodeList.get(myoId).sendInstantMessage();

        if (pose != Pose.UNKNOWN && pose != Pose.REST) {
            myo.notifyUserAction();
        }
    }

    @Override
    public void onOrientationData(Myo myo, long timestamp, Quaternion rotation) {
        super.onOrientationData(myo, timestamp, rotation);
        OrientationData orientationData = getMyoOrientationData(myo);
        textViewEditor.setTextView(R.id.rollValue, String.format("%.0f", Math.toDegrees(orientationData.getOffsetRoll())));
        textViewEditor.setTextView(R.id.pitchValue, String.format("%.0f", Math.toDegrees(orientationData.getOffsetPitch())));
        textViewEditor.setTextView(R.id.yawValue, String.format("%.0f", Math.toDegrees(orientationData.getOffsetYaw())));
    }

    @Override
    public void onAccelerometerData(Myo myo, long timestamp, Vector3 accel){
        super.onAccelerometerData(myo, timestamp, accel);
        AccelerometerData accelerometerData = getMyoAccelerometerData(myo);
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
        GyroData gyroData = getMyoGyroData(myo);
        textViewEditor.setTextView(R.id.gyroXValue, String.format("%.0f", gyroData.getGyro().x()));
        textViewEditor.setTextView(R.id.gyroYValue, String.format("%.0f", gyroData.getGyro().y()));
        textViewEditor.setTextView(R.id.gyroZValue, String.format("%.0f", gyroData.getGyro().z()));
    }
}
