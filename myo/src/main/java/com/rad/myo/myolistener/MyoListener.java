package com.rad.myo.myolistener;

import com.rad.myo.MyoRosActivity;

public interface MyoListener {
    MyoRosActivity getActivity();
    long getGestureStartTime();
    void setGestureStartTime(long gestureStartTime);
}
