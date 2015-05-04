package com.rad.myobaxter;

import android.content.Intent;
import android.widget.Button;

import com.rad.myobaxter.activities.DataLogActivity;
import com.rad.myobaxter.activities.HelloWorldActivity;
import com.rad.myobaxter.activities.MenuActivity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.robolectric.Robolectric.*;
import static org.robolectric.Shadows.shadowOf;
import static org.robolectric.shadows.ShadowView.clickOn;


@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, emulateSdk = 21)
public class MenuActivityTest {

    private MenuActivity menuActivity;

    @Before
    public void setUp() throws Exception {
        menuActivity = setupActivity(MenuActivity.class);
    }

    @Test
    public void dataLogButtonTapDisplaysDataLogActivity(){
        Button button = (Button) menuActivity.findViewById(R.id.data_log_button);
        assertThat(button.getText()).isEqualTo(menuActivity.getString(R.string.title_data_log));
        clickOn(button);
        Intent expectedIntent = new Intent(menuActivity, DataLogActivity.class);
        assertThat(shadowOf(menuActivity).getNextStartedActivity()).isEqualTo(expectedIntent);
    }

    @Test
    public void helloWorldButtonTapDisplaysHelloWorldActivity(){
        Button button = (Button) menuActivity.findViewById(R.id.hello_world_button);
        assertThat(button.getText()).isEqualTo(menuActivity.getString(R.string.title_hello_world));
        clickOn(button);
        Intent expectedIntent = new Intent(menuActivity, HelloWorldActivity.class);
        assertThat(shadowOf(menuActivity).getNextStartedActivity()).isEqualTo(expectedIntent);
    }
}
