package com.rad.myobaxter.utils;

import android.app.Activity;
import android.widget.TextView;

public class TextViewEditor {

    Activity activity;

    public TextViewEditor(Activity activity){
        this.activity = activity;
    }

    public void setTextView(int viewId, int stringId){
        getTextView(viewId).setText(stringId);
    }

    public void setTextView(int viewId, String text){
        getTextView(viewId).setText(text);
    }

    private TextView getTextView(int viewId){
        return (TextView) activity.findViewById(viewId);
    }

    public void setTextColor(int viewId, int colorId){
        getTextView(viewId).setTextColor(colorId);
    }

    public void setTextRotation(int viewId, float roll, float pitch, float yaw){
        TextView textView = getTextView(viewId);
        textView.setRotation(roll);
        textView.setRotationX(pitch);
        textView.setRotationY(yaw);
    }
}
