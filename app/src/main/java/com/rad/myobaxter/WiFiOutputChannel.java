package com.rad.myobaxter;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Vibrator;
import android.util.Log;

import com.thalmic.myo.Myo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

/**
 * Created by richardsmith on 26/03/15.
 */
public class WiFiOutputChannel implements SensorEventListener {


    private SensorManager mSensorManager;
    private Sensor mOrientation;
    private static long time = 0;
    private Socket socket;
    private boolean openingSocket = false;
    private PrintStream printStream;
    private BufferedReader in;

    private Vibrator v;

    private Activity activity;

    protected void init(Activity activity) {
        this.activity = activity;
        mSensorManager = (SensorManager) activity.getSystemService(Context.SENSOR_SERVICE);
        mOrientation = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        v = (Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE);
        openSocket();
    }

    private void vibrate(int on, int off, int len){

        long[] pattern = new long[len];
        pattern[0] = 0l;

        for(int i = 1; i < len - 1; i+=2){
            pattern[i] = on;
            pattern[i+1] = off;
        }

        v.vibrate(pattern, -1);
    }

    private void openSocket(){

        if(!openingSocket) {
            openingSocket = true; // Primitive Synchronisation
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        socket = new Socket("192.168.1.168", 4448);
                        printStream = new PrintStream(socket.getOutputStream());
                        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        Log.v("Hack", "Socket opened");
                    } catch (Exception e) {
                        Log.v("Hack", "Error", e);
                        System.exit(0);
                    }
                }
            });

            thread.start();
        }
    }

    public void sendGesture(Myo myo, String gesture){
        Log.v("Hack", "************* Myo: " + myo.getName() + " " + gesture);
        pingSocket(myo.getName() + ":gesture " + gesture);
    }

    public void sendCommand(Myo myo, String command){
        Log.v("Hack", "************* Myo: command" + myo.getName() + " " + command);
        pingSocket(myo.getName() + ":" + command);
    }

    public void pingSocket(final String message){

        // Rate limit
        long elapsed = System.currentTimeMillis() - time;
        if(!message.contains("gesture") && elapsed < 100){
            return;
        }
        time = System.currentTimeMillis();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    printStream.println(message);
                    printStream.flush();
                    Log.v("Hack", "run >" + message + "<");
                    String fromServer;
                    String fromUser;

                    while ((fromServer = in.readLine()) != null) {
                        Log.v("Hack", "Server: " + fromServer);

//                        if("Vibrate".equals(fromServer)){
//                            vibrate(6, 100, 20);
//                        }
                        if (fromServer.equals("Bye."))
                            break;
                    }
                }
                catch(Exception e){
                    Log.v("Warning", "Could not send to server");
                }
            }
        });

        thread.start();

    }

    protected void onResumeOfService() {
//        super.onResume();
        openSocket();
        mSensorManager.registerListener(this, mOrientation, SensorManager.SENSOR_DELAY_NORMAL);
    }

    protected void onPauseOfService(){
//        super.onPause();
        try {
            if(socket.isConnected()) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do something here if sensor accuracy changes.
        // You must implement this callback in your code.
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float azimuth_angle = event.values[0];
        float pitch_angle = event.values[1];
        float roll_angle = event.values[2];
        // Do something with these orientation angles.
        String message = azimuth_angle + " " + pitch_angle + " " + roll_angle;

        long elapsed = System.currentTimeMillis() - time;
        if(elapsed > 100){
            time = System.currentTimeMillis();
            Log.v("azimuth_angle", "" + message);
//            pingSocket(message);
        }
    }

}
