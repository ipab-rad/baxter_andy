package com.rad.myobaxter;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.rad.myobaxter.Data.AccelSampleData;
import com.rad.myobaxter.Data.CalibratedAccel;
import com.rad.myobaxter.Data.CalibratedGyro;
import com.rad.myobaxter.Data.CalibratedRotation;
import com.rad.myobaxter.Data.OriginalAccel;
import com.rad.myobaxter.Data.OriginalGyro;
import com.rad.myobaxter.Data.OriginalRotation;
import com.rad.myobaxter.Data.Vector3Sample;
import com.thalmic.myo.DeviceListener;
import com.thalmic.myo.Hub;
import com.thalmic.myo.Myo;
import com.thalmic.myo.Quaternion;
import com.thalmic.myo.Vector3;
import com.thalmic.myo.XDirection;
import com.thalmic.myo.scanner.ScanActivity;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import lombok.Data;

@Data
public abstract class MyoActivity extends Activity {

    private static final String TAG = "MyoActivity";

    private Toast mToast;

    private TextView titleTextView;

    private Vector3 accel = new Vector3();
    private Vector3 velocity = new Vector3();
    private Vector3 position = new Vector3();

    private OriginalAccel originalAccel = OriginalAccel.getInstance();
    private OriginalGyro originalGyro = OriginalGyro.getInstance();
    private OriginalRotation originalRotation = OriginalRotation.getInstance();
    private CalibratedAccel calibratedAccel = CalibratedAccel.getInstance();
    private CalibratedGyro calibratedGyro = CalibratedGyro.getInstance();
    private CalibratedRotation calibratedRotation = CalibratedRotation.getInstance();
    private AccelSampleData accelSampleData = new AccelSampleData();

    private float rollValue;
    private float pitchValue;
    private float yawValue;

    private int count = 0;
    private final double ZERO_ACCEL_THRESHOLD = 0.1;
    private final double VELOCITY_DEGRADER = 0.9;

    protected void initializeHub(DeviceListener mListener) {
        // First, we initialize the Hub singleton with an application identifier.
        Hub hub = Hub.getInstance();
        if (!hub.init(this, getPackageName())) {
            // We can't do anything with the Myo device if the Hub can't be initialized, so exit.
            Toast.makeText(this, "Couldn't initialize Hub", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Next, register for DeviceListener callbacks.
        hub.addListener(mListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // We don't want any callbacks when the Activity is gone, so unregister the listener.
//        Hub.getInstance().removeListener(mListener);
//
//        if (isFinishing()) {
//            // The Activity is finishing, so shutdown the Hub. This will disconnect from the Myo.
//            Hub.getInstance().shutdown();
//        }
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

    protected void calculateOffsetRotation(Myo myo, long timestamp, Quaternion rotation){
        originalRotation.setTimestamp(timestamp);
        originalRotation.setRotation(rotation);
        if(calibratedRotation.getRotation() == null){
            calibratedRotation.setTimestamp(timestamp);
            calibratedRotation.setRotation(rotation);
        }
        // Calculate Euler angles (roll, pitch, and yaw) from the quaternion.
        float originalRoll = (float) Math.toDegrees(Quaternion.roll(originalRotation.getRotation()));
        float originalPitch = (float) Math.toDegrees(Quaternion.pitch(originalRotation.getRotation()));
        float originalYaw = (float) Math.toDegrees(Quaternion.yaw(originalRotation.getRotation()));

        float calibratedRoll = (float) Math.toDegrees(Quaternion.roll(calibratedRotation.getRotation()));
        float calibratedPitch = (float) Math.toDegrees(Quaternion.pitch(calibratedRotation.getRotation()));
        float calibratedYaw = (float) Math.toDegrees(Quaternion.yaw(calibratedRotation.getRotation()));

        // Adjust roll and pitch for the orientation of the Myo on the arm.
        if (myo.getXDirection() == XDirection.TOWARD_ELBOW) {
            originalRoll *= -1;
            originalPitch *= -1;
            calibratedRoll *= -1;
            calibratedPitch *= -1;
        }

        // Next, we apply a rotation to the text view using the roll, pitch, and yaw.
        rollValue = originalRoll - calibratedRoll;
        pitchValue = originalPitch - calibratedPitch;
        yawValue = originalYaw - calibratedYaw;
        Log.d(TAG, "orientation: roll: " + rollValue);
        Log.d(TAG, "orientation: pitch: " + pitchValue);
        Log.d(TAG, "orientation: yaw: " + yawValue);
    }

    protected void calculateVelocityAndPositionFromAcceleration(){

        List<Vector3Sample> movingAverage = accelSampleData.getMovingAverage();
        if(movingAverage.size() > 2) {
            Vector3Sample latestMovingAverage = movingAverage.get(movingAverage.size() - 1);
            Vector3Sample previousMovingAverage = movingAverage.get(movingAverage.size() - 2);

//            Vector3 differenceVector = new Vector3();
//            differenceVector.set(latestMovingAverage.getValue());
//            differenceVector.subtract(previousMovingAverage.getValue());
//            double threshold = 0.0005;
//            if(differenceVector.x() > threshold || differenceVector.y() > threshold || differenceVector.z() > threshold) {

            long previousMovingAvgTimestamp = previousMovingAverage.getTimestamp();
            double timePeriod = (double) (latestMovingAverage.getTimestamp() - previousMovingAvgTimestamp) / 1000.0;

            Vector3 accel = new Vector3();
            accel.set(latestMovingAverage.getValue());
//
            accel.subtract(getCalibratedAccel().getAccel());
            double g = 9.80665;
            accel.multiply(g);


            double accelX = Math.abs(accel.x()) < ZERO_ACCEL_THRESHOLD ? 0 : accel.x();
//                double accelX = accel.x();
            double accelY = Math.abs(accel.y()) < ZERO_ACCEL_THRESHOLD ? 0 : accel.y();
//                double accelY = accel.y();
            double accelZ = Math.abs(accel.z()) < ZERO_ACCEL_THRESHOLD ? 0 : accel.z();
//                double accelZ = accel.z();
            this.accel.set(new Vector3(accelX, accelY, accelZ));
            double vx = velocity.x() + timePeriod * round(accelX, 9);
            double velocityX = accelX == 0 ? vx* VELOCITY_DEGRADER : vx;
//                double velocityX = velocity.x() + timePeriod * round(accelX, 9);
//            double velocityX = timePeriod * round(accel.x(), 0);
            double vy = velocity.y() + timePeriod * round(accelY, 9);
            double velocityY = accelY == 0 ? vy* VELOCITY_DEGRADER : vy;
//                double velocityY = velocity.y() + timePeriod * round(accelY, 9);
//            double velocityY = timePeriod * round(accel.y(), 0);
            double vz = velocity.z() + timePeriod * round(accelZ, 9);
            double velocityZ = accelZ == 0 ? vz* VELOCITY_DEGRADER : vz;
//                double velocityZ = velocity.z() + timePeriod * round(accelZ, 9);
//            double velocityZ = timePeriod * round(accel.z(), 0);
                this.velocity = new Vector3(velocityX, velocityY, velocityZ);

                double positionX = position.x() + ((timePeriod * velocity.x()));
                double positionY = position.y() + ((timePeriod * velocity.y()));
                double positionZ = position.z() + ((timePeriod * velocity.z()));
                this.position = new Vector3(positionX, positionY, positionZ);
//            }
            Log.d(TAG, "logdata timestamp: " + String.valueOf(latestMovingAverage.getTimestamp()));
            Log.d(TAG, "logdata accelX: " + String.format("%.6f", accel.x()));
            Log.d(TAG, "logdata accelY: " + String.format("%.6f", accel.y()));
            Log.d(TAG, "logdata accelZ: " + String.format("%.6f", accel.z()));
            Log.d(TAG, "logdata velocityX: " + String.format("%.6f", velocity.x()));
            Log.d(TAG, "logdata velocityY: " + String.format("%.6f", velocity.y()));
            Log.d(TAG, "logdata velocityZ: " + String.format("%.6f", velocity.z()));
            Log.d(TAG, "logdata positionX: " + String.format("%.6f", position.x()));
            Log.d(TAG, "logdata positionY: " + String.format("%.6f", position.y()));
            Log.d(TAG, "logdata positionZ: " + String.format("%.6f", position.z()));
        }
    }

    public void resetSensors(View view){
        resetSensors();
    }

    protected void resetSensors() {
        List<Vector3Sample> movingAvg = accelSampleData.getMovingAverage();
        List<Vector3Sample> samples = accelSampleData.getSamples();
        int calibrated_sample_size = AccelSampleData.CALIBRATED_SAMPLE_SIZE;
        if(samples.size() > calibrated_sample_size) {
            Vector3 calibratedAccel = new Vector3();
            for (int i = samples.size() - calibrated_sample_size; i < samples.size(); i++) {
                calibratedAccel.add(samples.get(i).getValue());
            }
            calibratedAccel.divide(calibrated_sample_size);

            getCalibratedAccel().setAccel(calibratedAccel);
            getCalibratedGyro().setGyro(getOriginalGyro().getGyro());
            getCalibratedRotation().setRotation(getOriginalRotation().getRotation());
            accel = new Vector3();
            position = new Vector3();
            velocity = new Vector3();
            showToast("Sensors calibrated");
        } else {
            showToast("Not enough samples to calibrate");
        }
    }

    protected void convertFromBodyToInertiaFrame(Vector3 accel) {
        Quaternion q = new Quaternion();
        Quaternion currentQ = new Quaternion();
        Quaternion currentQInv = new Quaternion();
        Quaternion diff = new Quaternion();
        Quaternion diffInv = new Quaternion();
        Quaternion qinv = new Quaternion();
        Quaternion result = new Quaternion();

        q.set(getCalibratedRotation().getRotation());
        qinv.set(getCalibratedRotation().getRotation());
        qinv.inverse();

        currentQ.set(getOriginalRotation().getRotation());
        currentQInv.set(getOriginalRotation().getRotation());
        currentQInv.inverse();

        diff.set(currentQ);
        diff.multiply(qinv);
        diffInv.set(diff);
        diffInv.inverse();
        Quaternion v = new Quaternion(accel.x(), accel.y(), accel.z(), 0);
        result.set(diffInv);
        result.multiply(v);
        result.multiply(diff);
        accel.set(new Vector3(result.x(), result.y(), result.z()));
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
