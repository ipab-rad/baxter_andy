package com.rad.myo.data;

import com.thalmic.myo.Vector3;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class AccelSampleData {
    public static int SAMPLE_SIZE = 10;
    private long startTime;
    private long sampleRangeStartTime;
    public static int REQUIRED_SAMPLE_SIZE_TO_CALIBRATE = 200;
    private List<Vector3Sample> samples;
    private List<Vector3Sample> movingAverage;

    public AccelSampleData(){
        samples = new ArrayList<Vector3Sample>();
        movingAverage = new ArrayList<Vector3Sample>();
    }

    public void addSample(Vector3 accel, long timestamp) {
        if(startTime == 0){
            startTime = timestamp;
            sampleRangeStartTime = timestamp;
        }
        Vector3Sample sample = new Vector3Sample(accel, timestamp);
        samples.add(sample);
        addNewMovingAverage(accel, timestamp);
        if(samples.size() > SAMPLE_SIZE){
            sampleRangeStartTime = samples.get(samples.size() - SAMPLE_SIZE).getTimestamp();
        }
        if(samples.size() > REQUIRED_SAMPLE_SIZE_TO_CALIBRATE + 1){
            samples.remove(0);
            movingAverage.remove(0);
        }
    }

    private void addNewMovingAverage(Vector3 accel, long timestamp) {
        int movingAvgSize = movingAverage.size();
        if(movingAvgSize >= SAMPLE_SIZE){
            subtractFirstSampleOfRangeAndAddNewSampleToMovingAverage(accel, timestamp, movingAvgSize);
        } else if(movingAvgSize == 0){
            addVectorAsFirstMovingAverage(accel, timestamp);
        } else {
            addNewSampleToMovingAverage(accel, timestamp, movingAvgSize);
        }
    }

    private void addNewSampleToMovingAverage(Vector3 accel, long timestamp, int movingAvgSize) {
        Vector3 newAverage = new Vector3();
        Vector3 newSample = new Vector3();
        newAverage.set(movingAverage.get(movingAvgSize - 1).getValue());
        newAverage.multiply(movingAvgSize);
        newSample.set(accel);
        newAverage.add(newSample);
        newAverage.divide(movingAvgSize + 1);

        movingAverage.add(new Vector3Sample(newAverage, timestamp));
    }

    private void subtractFirstSampleOfRangeAndAddNewSampleToMovingAverage(Vector3 accel, long timestamp, int movingAvgSize) {
        Vector3 newAverage = new Vector3();
        Vector3 newSample = new Vector3();
        newAverage.set(movingAverage.get(movingAvgSize - 1).getValue());
        Vector3 firstAverageOfRange = new Vector3();
        firstAverageOfRange.set(samples.get(movingAvgSize - SAMPLE_SIZE).getValue());
        firstAverageOfRange.divide(SAMPLE_SIZE);
        newSample.set(accel);
        newSample.divide(SAMPLE_SIZE);
        newAverage.subtract(firstAverageOfRange);
        newAverage.add(newSample);
        movingAverage.add(new Vector3Sample(newAverage, timestamp));
    }

    private void addVectorAsFirstMovingAverage(Vector3 accel, long timestamp) {
        Vector3Sample avgSample;
        avgSample = new Vector3Sample(accel, timestamp);
        movingAverage.add(avgSample);
    }


    public long milliSecondsBetweenCurrentAndLastSample() {
        Vector3Sample latestMovingAverage = movingAverage.get(movingAverage.size() - 1);
        Vector3Sample previousMovingAverage = movingAverage.get(movingAverage.size() - 2);
        return latestMovingAverage.getTimestamp() - previousMovingAverage.getTimestamp();
    }
}
