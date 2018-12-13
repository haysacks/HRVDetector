package com.example.android.camera2basic;

import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class HeartData {
    private ArrayList<Double> values;
    private LinkedHashMap<Double, Double> peaks;
    private ArrayList<Double> rrInterval = new ArrayList<Double>();

    private double bpm;
    private double avnn;
    private double sdnn;
    private double rmssd;
    private double ppn50;

    public HeartData(ArrayList<Double> values) {
        this.values = values;
        this.peaks = (LinkedHashMap<Double, Double>) SignalProcessing.getPeaks(values, true);
        initBPM();
        initRR();
        initAVNN();
        initSDNN();
        initRMSSD();
        initPPN50();
    }

    private void initBPM() {
        bpm = SignalProcessing.getBPM(values);
//        bpm = (peaks.size() * 60.0 / (MainActivity.RECORDING_TIME - MainActivity.CUTOFF_TIME));
    }

    private void initRR() {
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
    private void initAVNN() {
        double sum = 0;
        for(double d : rrInterval) {
            sum += d;
        }
        avnn = sum / (rrInterval.size());
    }

    // sd of NN-interval
    private void initSDNN() {
        double sum = 0;
        double standardDeviation = 0.0;
        double mean = getAVNN();

        for(double d: rrInterval) {
            standardDeviation += Math.pow(d - mean, 2);
        }

        sdnn = Math.sqrt(standardDeviation/rrInterval.size());
    }

    // rMSSD = square root of the mean of the squares of differences between adjacent NN intervals
    private void initRMSSD() {
        double sum = 0;
        int size = rrInterval.size();

        for(int i = 1; i < size; ++i) {
            double diff = rrInterval.get(i) - rrInterval.get(i-1);
            sum += diff * diff;
        }

        rmssd = Math.sqrt(sum / (rrInterval.size()-1));
    }

    // pNN50 = % of differences between adjacent NN intervals that are greater than 50 ms
    private void initPPN50() {
        int count = 0;
        int size = rrInterval.size();

        for(int i = 1; i < size; ++i) {
            double diff = rrInterval.get(i) - rrInterval.get(i-1);
            if(diff > 0.05) {
                ++count;
            }
        }

        ppn50 = (((double)count / (rrInterval.size() - 1)) * 100);
    }

    public double getBPM() {
        return bpm;
    }

    // average of NN-interval
    public double getAVNN() {
        return avnn;
    }

    // sd of NN-interval
    public double getSDNN() {
        return sdnn;
    }

    // rMSSD = square root of the mean of the squares of differences between adjacent NN intervals
    public double getRMSSD() {
        return rmssd;
    }

    // pNN50 = % of differences between adjacent NN intervals that are greater than 50 ms
    public double getPPN50() {
        return ppn50;
    }

    public ArrayList<Double> getRrInterval() {
        ArrayList<Double> rrClone = new ArrayList<Double>();
        for(Double d : rrInterval) {
            rrClone.add(d);
        }
        return rrClone;
    }
}
