package com.rad.myobaxter.activities;

import android.os.Bundle;

import com.rad.myo.MyoRosActivity;
import com.rad.myobaxter.R;
import com.rad.myobaxter.myolistener.MyoDataLogListener;

public class DataLogActivity extends MyoRosActivity {

    public static final String TAG = "DataLogActivity";

    public DataLogActivity() {
        super("MyoBaxterDataLog");
        setMyoListener(new MyoDataLogListener(this));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_log);
    }
}
