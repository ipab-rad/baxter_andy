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
        Bundle b = getIntent().getExtras();

        this.getMyoData().setMyoId(b.getInt("myo_id")); // Edit to change the topic name
        setContentView(R.layout.activity_data_log);
    }
}
