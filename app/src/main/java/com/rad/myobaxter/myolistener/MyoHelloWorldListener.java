package com.rad.myobaxter.myolistener;

import android.graphics.Color;

import com.rad.myo.myolistener.DefaultMyoListener;
import com.rad.myo.MyoRosActivity;
import com.rad.myo.data.OrientationData;
import com.rad.myobaxter.R;
import com.rad.myobaxter.utils.TextViewEditor;
import com.thalmic.myo.Arm;
import com.thalmic.myo.Myo;
import com.thalmic.myo.Pose;
import com.thalmic.myo.Quaternion;
import com.thalmic.myo.XDirection;

public class MyoHelloWorldListener extends DefaultMyoListener {

    TextViewEditor textViewEditor;

    public MyoHelloWorldListener(MyoRosActivity myoRosActivity){
        super(myoRosActivity);
        textViewEditor = new TextViewEditor(myoRosActivity);
    }

    @Override
    public void onConnect(Myo myo, long timestamp) {
        // Set the text color of the text view to cyan when a Myo connects.
        textViewEditor.setTextColor(R.id.text, Color.CYAN);
    }

    @Override
    public void onDisconnect(Myo myo, long timestamp) {
        // Set the text color of the text view to red when a Myo disconnects.
        textViewEditor.setTextColor(R.id.text, Color.RED);
    }

    @Override
    public void onArmSync(Myo myo, long timestamp, Arm arm, XDirection xDirection) {
        if(myo.getArm() == Arm.LEFT) {
            textViewEditor.setTextView(R.id.text, R.string.arm_left);
        } else {
            textViewEditor.setTextView(R.id.text, R.string.arm_right);
        }
    }

    @Override
    public void onArmUnsync(Myo myo, long timestamp) {
        textViewEditor.setTextView(R.id.text, R.string.hello_world);
    }

    @Override
    public void onUnlock(Myo myo, long timestamp) {
        textViewEditor.setTextView(R.id.lock_state, R.string.unlocked);
    }

    @Override
    public void onLock(Myo myo, long timestamp) {
        textViewEditor.setTextView(R.id.lock_state, R.string.locked);
    }

    @Override
    public void onPose(Myo myo, long timestamp, Pose pose) {
        // Handle the cases of the Pose enumeration, and change the text of the text view
        // based on the pose we receive.
        switch (pose) {
            case UNKNOWN:
                textViewEditor.setTextView(R.id.text, R.string.hello_world);
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
                textViewEditor.setTextView(R.id.text, restTextId);
                break;
            case FIST:
                getActivity().calibrateSensors();
                textViewEditor.setTextView(R.id.text, R.string.pose_fist);
                break;
            case WAVE_IN:
                textViewEditor.setTextView(R.id.text, R.string.pose_wavein);
                break;
            case WAVE_OUT:
                textViewEditor.setTextView(R.id.text, R.string.pose_waveout);
                break;
            case FINGERS_SPREAD:
                textViewEditor.setTextView(R.id.text, R.string.pose_fingersspread);
                break;
        }

        myo.unlock(Myo.UnlockType.HOLD);

        if (pose != Pose.UNKNOWN && pose != Pose.REST) {
            // Notify the Myo that the pose has resulted in an action, in this case changing
            // the text on the screen. The Myo will vibrate.
            myo.notifyUserAction();
        }
    }

    @Override
    public void onOrientationData(Myo myo, long timestamp, Quaternion rotation) {
        super.onOrientationData(myo, timestamp, rotation);

        // Next, we apply a rotation to the text view using the offsetRoll, offsetPitch, and offsetYaw.
        OrientationData orientationData = getMyoOrientationData(myo);
        float offsetRoll = (float) Math.toDegrees(orientationData.getOffsetRoll());
        float offsetPitch = (float) Math.toDegrees(orientationData.getOffsetPitch());
        float offsetYaw = (float) Math.toDegrees(orientationData.getOffsetYaw());
        textViewEditor.setTextRotation(R.id.text, offsetRoll, offsetPitch, offsetYaw);
    }
}
