package com.rad.myobaxter;

import android.content.Intent;
import android.widget.Button;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.robolectric.Robolectric.shadowOf;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = "app/src/main/AndroidManifest.xml", emulateSdk = 18)
public class MenuActivityTest {

    private MenuActivity menuActivity;

    @Before
    public void setUp() throws Exception {
        menuActivity = Robolectric.setupActivity(MenuActivity.class);
    }

    @Test
    public void dataLogButtonTapDisplaysDataLogActivity(){
        Button button = (Button) menuActivity.findViewById(R.id.data_log_button);
        assertThat(button.getText()).isEqualTo(menuActivity.getString(R.string.data_log));
        Robolectric.clickOn(button);
        Intent expectedIntent = new Intent(menuActivity, DataLogActivity.class);
        assertThat(shadowOf(menuActivity).getNextStartedActivity()).isEqualTo(expectedIntent);
    }

    @Test
    public void helloWorldButtonTapDisplaysHelloWorldActivity(){
        Button button = (Button) menuActivity.findViewById(R.id.hello_world_button);
        assertThat(button.getText()).isEqualTo(menuActivity.getString(R.string.hello_world));
        Robolectric.clickOn(button);
        Intent expectedIntent = new Intent(menuActivity, HelloWorldActivity.class);
        assertThat(shadowOf(menuActivity).getNextStartedActivity()).isEqualTo(expectedIntent);
    }

    @Test
    public void vibrateDemoButtonTapDisplaysVibrateDemoActivity(){
        Button button = (Button) menuActivity.findViewById(R.id.vibrate_demo_button);
        assertThat(button.getText()).isEqualTo(menuActivity.getString(R.string.vibrate_demo));
        Robolectric.clickOn(button);
        Intent expectedIntent = new Intent(menuActivity, VibrateActivity.class);
        assertThat(shadowOf(menuActivity).getNextStartedActivity()).isEqualTo(expectedIntent);
    }
}
