package com.rad.myobaxter.utils;

import android.app.Activity;
import android.widget.Toast;

public class ToastMaker {
    private Activity activity;
    private Toast mToast;

    public ToastMaker(Activity activity){
        this.activity = activity;
    }

    public void showToast(String text) {
        if (mToast == null) {
            mToast = Toast.makeText(activity, text, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(text);
        }
        mToast.show();
    }
}
