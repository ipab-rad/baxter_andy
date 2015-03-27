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
import com.thalmic.myo.scanner.ScanActivity;

public class MenuActivity extends Activity {

    public void helloWorld(View view){
        Intent intent = new Intent(this, HelloWorldActivity.class);
        startActivity(intent);
    }

    public void dataLog(View view){
        Intent intent = new Intent(this, DataLogActivity.class);
        startActivity(intent);
    }

    public void vibrateDemo(View view){
        Intent intent = new Intent(this, VibrateActivity.class);
        startActivity(intent);
    }

//    public void  expectOneMyo(View view){
//        expectMyo(1);
//    }
//
//    public void  expectTwoMyo(View view){
//        expectMyo(2);
//    }
//
//    private void expectMyo(int numberOfMyos){
//        setAttachingCount(numberOfMyos);
//        initializeHub(getMListener());
//        showToast("Number of myos enabled: " + getAttachingCount());
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

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
