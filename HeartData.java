package com.example.android.camera2basic;

import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class HeartData {
    private LinkedHashMap<Double, Double> peaks;
    private ArrayList<Double> rrInterval = new ArrayList<Double>();

    public HeartData(Map<Double, Double> peaks) {
        this.peaks = (LinkedHashMap<Double, Double>) peaks;
        initRR();
    }

    public double getBPM() {
        return (peaks.size() * 60.0 / MainActivity.RECORDING_TIME);
    }

    public void initRR() {
        int i = 0;
        Double prevPeak = null;
        for(Map.Entry<Double, Double> peak : peaks.entrySet()) {
            if (i > 0) {
                rrInterval.add(peak.getKey() - prevPeak);
            }
            prevPeak = peak.getKey();
            ++i;
        }
    }

    // average of NN-interval
    public double getAVNN() {
        double sum = 0;
        for(double d : rrInterval) {
            sum += d;
        }
        return sum / (rrInterval.size());
    }

    // sd of NN-interval
    public double getSDNN() {
        double sum = 0;
        double standardDeviation = 0.0;
        double mean = getAVNN();

        for(double d: rrInterval) {
            standardDeviation += Math.pow(d - mean, 2);
        }

        return Math.sqrt(standardDeviation/rrInterval.size());
    }

    // rMSSD = square root of the mean of the squares of differences between adjacent NN intervals
    public double getRMSSD() {
        double sum = 0;
        int size = rrInterval.size();

        for(int i = 1; i < size; ++i) {
            double diff = rrInterval.get(i) - rrInterval.get(i-1);
            sum += diff * diff;
        }

        return Math.sqrt(sum / (rrInterval.size()-1));
    }

    // pNN50 = % of differences between adjacent NN intervals that are greater than 50 ms
    public double getPPN50() {
        int count = 0;
        int size = rrInterval.size();

        for(int i = 1; i < size; ++i) {
            double diff = rrInterval.get(i) - rrInterval.get(i-1);
            if(diff > 0.05) {
                ++count;
            }
        }

        return (((double)count / (rrInterval.size() - 1)) * 100);
    }

    public ArrayList<Double> getRrInterval() {
        ArrayList<Double> rrClone = new ArrayList<Double>();
        for(Double d : rrInterval) {
            rrClone.add(d);
        }
        return rrClone;
    }
}
