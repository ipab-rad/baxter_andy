package com.rad.myobaxter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.thalmic.myo.Hub;
import com.thalmic.myo.Myo;
import com.thalmic.myo.scanner.ScanActivity;

public class VibrateActivity extends Activity {

    public void shortVibrate(View view){
        Myo myo = Hub.getInstance().getConnectedDevices().get(0);
        myo.vibrate((Myo.VibrationType.SHORT));
    }

    public void mediumVibrate(View view){
        Myo myo = Hub.getInstance().getConnectedDevices().get(0);
        myo.vibrate((Myo.VibrationType.MEDIUM));
    }

    public void longVibrate(View view){
        Myo myo = Hub.getInstance().getConnectedDevices().get(0);
        myo.vibrate((Myo.VibrationType.LONG));
    }

    public void fiveTimeShortVibrate(View view){
        Myo myo = Hub.getInstance().getConnectedDevices().get(0);
        for(int i=0; i<5; i++) {
            myo.vibrate((Myo.VibrationType.SHORT));
        }
    }

    public void twentyFiveTimeShortVibrate(View view){
        Myo myo = Hub.getInstance().getConnectedDevices().get(0);
        for(int i=0; i<25; i++) {
            myo.vibrate((Myo.VibrationType.SHORT));
        }
    }

    public void fiveTimeMediumVibrate(View view){
        Myo myo = Hub.getInstance().getConnectedDevices().get(0);
        for(int i=0; i<5; i++) {
            myo.vibrate((Myo.VibrationType.MEDIUM));
        }
    }

    public void fiveTimeLongVibrate(View view){
        Myo myo = Hub.getInstance().getConnectedDevices().get(0);
        for(int i=0; i<5; i++) {
            myo.vibrate((Myo.VibrationType.LONG));
        }
    }

    public void mixtureVibrate(View view){
        Myo myo = Hub.getInstance().getConnectedDevices().get(0);
        myo.vibrate((Myo.VibrationType.SHORT));
        myo.vibrate((Myo.VibrationType.LONG));
        myo.vibrate((Myo.VibrationType.SHORT));
        myo.vibrate((Myo.VibrationType.MEDIUM));
        myo.vibrate((Myo.VibrationType.MEDIUM));
        myo.vibrate((Myo.VibrationType.LONG));
        myo.vibrate((Myo.VibrationType.SHORT));
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vibrate);

        // First, we initialize the Hub singleton with an application identifier.
        Hub hub = Hub.getInstance();
        if (!hub.init(this, getPackageName())) {
            // We can't do anything with the Myo device if the Hub can't be initialized, so exit.
            Toast.makeText(this, "Couldn't initialize Hub", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
}
