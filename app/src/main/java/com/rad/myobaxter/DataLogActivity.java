package com.rad.myobaxter;

import android.os.Bundle;

import com.rad.myo.MyoRosActivity;
import com.rad.myobaxter.myolistener.MyoDataLogListener;
import com.thalmic.myo.DeviceListener;

public class DataLogActivity extends MyoRosActivity {

    public static final String TAG = "DataLogActivity";

    public DataLogActivity() {
        super("MyoBaxterDataLog");
    }

    private DeviceListener mListener = new MyoDataLogListener(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_log);
    }
}
