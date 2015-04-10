package com.rad.myobaxter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class MenuActivity extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        setUpButton(R.id.hello_world_button);
        setUpButton(R.id.data_log_button);
        setUpButton(R.id.vibrate_demo_button);
    }

    private void setUpButton(int buttonId) {
        Button button = (Button) findViewById(buttonId);
        button.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.hello_world_button:
                startActivity(new Intent(MenuActivity.this, HelloWorldActivity.class));
            case R.id.data_log_button:
                startActivity(new Intent(MenuActivity.this, DataLogActivity.class));
            case R.id.vibrate_demo_button:
                startActivity(new Intent(MenuActivity.this, VibrateActivity.class));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
