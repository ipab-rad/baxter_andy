package com.rad.myobaxter.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.rad.myobaxter.R;


public class TopicActivity extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic);

        setUpButton(R.id.topic_zero_button);
        setUpButton(R.id.topic_one_button);
    }

    private void setUpButton(int buttonId) {
        Button button = (Button) findViewById(buttonId);
        button.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(TopicActivity.this, DataLogActivity.class);
        Bundle b = new Bundle();
        switch(v.getId()){
            case R.id.topic_one_button:
                b.putInt("myo_id", 1);
                break;
            case R.id.topic_zero_button:
                b.putInt("myo_id", 0);
                break;
        }
        intent.putExtras(b);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}

