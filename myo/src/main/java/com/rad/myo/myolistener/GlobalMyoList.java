package com.rad.myo.myolistener;

import com.thalmic.myo.Myo;

import java.util.ArrayList;
import java.util.List;

public class GlobalMyoList {
    private static ArrayList<Myo> knownMyos;

    public static List<Myo> getKnownMyos(){
        return knownMyos;
    }

    public static void add(Myo myo){
        if(knownMyos == null){
            knownMyos = new ArrayList<Myo>();
        }
        knownMyos.add(myo);
    }

    public static int identifyMyo(Myo myo) {
        return knownMyos.indexOf(myo);
    }
}
