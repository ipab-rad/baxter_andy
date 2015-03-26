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
import java.text.DecimalFormat;
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
    }

    protected void calculateVelocityAndPositionFromAcceleration(){
        count+=1;
//        if(count > AccelSampleData.SAMPLE_SIZE) {
//            count = 0;

        List<Vector3Sample> movingAverage = accelSampleData.getSamples();
        if(movingAverage.size() > 2) {
            Vector3Sample latestMovingAverage = movingAverage.get(movingAverage.size() - 1);
//        double timePeriod = (double) getOriginalAccel().timeDifference()/1000.0;

//            double timePeriod = (double) (latestMovingAverage.getTimestamp() - accelSampleData.getSampleRangeStartTime()) / 1000.0;
            long previousMovingAvgTimestamp = movingAverage.get(movingAverage.size() - 2).getTimestamp();
            double timePeriod = (double) (latestMovingAverage.getTimestamp() - previousMovingAvgTimestamp);
            Log.i(TAG, "Time Period: " + timePeriod);
            Log.i(TAG, "Timestamp: " + latestMovingAverage.getTimestamp());
            this.accel.set(latestMovingAverage.getValue());

            Vector3 calAccel = new Vector3();
            calAccel.set(calibratedAccel.getAccel());

            double g = 9.80665;
            this.accel.multiply(g);
            calAccel.multiply(g);

            this.accel.subtract(calAccel);


//        Log.i(TAG, "Time Period: " + String.valueOf(timePeriod));
//        Log.i(TAG, "Timestamp Previous: " + String.valueOf(getOriginalAccel().getPreviousTimestamp()));
//        Log.i(TAG, "Timestamp Now: " + String.valueOf(getOriginalAccel().getTimestamp()));

            //TODO create filtering window instead of rounding i.e. accel < 0.5  ===  accel = 0
            double velocityX = velocity.x() + timePeriod * round(accel.x(), 9);
//            double velocityX = timePeriod * round(accel.x(), 0);
            double velocityY = velocity.y() + timePeriod * round(accel.y(), 9);
//            double velocityY = timePeriod * round(accel.y(), 0);
            double velocityZ = velocity.z() + timePeriod * round(accel.z(), 9);
//            double velocityZ = timePeriod * round(accel.z(), 0);
            this.velocity = new Vector3(velocityX, velocityY, velocityZ);

            double positionX = position.x() + ((timePeriod * velocity.x()) / 1000000.0);
            double positionY = position.y() + ((timePeriod * velocity.y()) / 1000000.0);
            double positionZ = position.z() + ((timePeriod * velocity.z()) / 1000000.0);
            this.position = new Vector3(positionX, positionY, positionZ);
            Log.i(TAG, "logdata accelX: " + String.format("%.6f", accel.x()));
            Log.i(TAG, "logdata accelY: " + String.format("%.6f", accel.y()));
            Log.i(TAG, "logdata accelZ: " + String.format("%.6f", accel.z()));
            Log.i(TAG, "logdata velocityX: " + String.format("%.6f", velocity.x()));
            Log.i(TAG, "logdata velocityY: " + String.format("%.6f", velocity.y()));
            Log.i(TAG, "logdata velocityZ: " + String.format("%.6f", velocity.z()));
            Log.i(TAG, "logdata positionX: " + String.format("%.6f", position.x()));
            Log.i(TAG, "logdata positionY: " + String.format("%.6f", position.y()));
            Log.i(TAG, "logdata positionZ: " + String.format("%.6f", position.z()));
        }
//        }
    }

    public void resetSensors(View view){
        resetSensors();
    }

    //TODO change this to use average of sampled data for accelerometer.
    protected void resetSensors() {
        getCalibratedAccel().setAccel(getOriginalAccel().getAccel());
        getCalibratedGyro().setGyro(getOriginalGyro().getGyro());
        getCalibratedRotation().setRotation(getOriginalRotation().getRotation());
        position = new Vector3();
        velocity = new Vector3();
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
