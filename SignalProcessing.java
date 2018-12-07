package com.example.android.camera2basic;

import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import uk.me.berndporr.iirj.Butterworth;

public class SignalProcessing {

    public static ArrayList<Double> movingAverage(ArrayList<Double> values) {
        ArrayList<Double> newValues = new ArrayList<Double>();
        int size = values.size();

        double newValue = 0;

        for(int i = 0; i < size; ++i) {
            if(i == 0) {
                newValue = (values.get(i) + values.get(i+1) + values.get(i + 2) + values.get(i + 3) + values.get(i + 4))/5;
            }
            else if (i == 1) {
                newValue = (values.get(i-1) + values.get(i) + values.get(i+1) + values.get(i + 2) + values.get(i + 3))/5;
            }
            else if (i == size - 1) {
                newValue = (values.get(i-4) + values.get(i-3) + values.get(i-2) + values.get(i-1) + values.get(i))/5;
            }
            else if (i == size - 2){
                newValue = (values.get(i-3) + values.get(i-2) + values.get(i-1) + values.get(i) + values.get(i + 1))/5;
            }
            else {
                newValue = (values.get(i-2) + values.get(i-1) + values.get(i) + values.get(i + 2))/5;
            }
            newValues.add(newValue);
        }
        return newValues;
    }

    public static ArrayList<Double> butterworthFilter(ArrayList<Double> values) {
        ArrayList<Double> newValues = new ArrayList<Double>();
        Butterworth butterworth = new Butterworth();

        butterworth.bandStop(2, 250, 50, 5);
        for(Double value : values) {
            double newValue = butterworth.filter(value);
            newValues.add(newValue);
        }
        return newValues;
    }

    public static Map<Double, Double> getPeaks(ArrayList<Double> values, ArrayList<Double> x) {
        Map<Double, Double> maxima = new LinkedHashMap<Double, Double>();
        Map<Double, Double> minima = new LinkedHashMap<Double, Double>();

        Double maximum = null;
        Double minimum = null;
        Double maximumPos = null;
        Double minimumPos = null;

        boolean lookForMax = true;
        int size = values.size();

        for (int i = 0; i < size; ++i) {
            Double value = values.get(i);

            if (maximum == null || value > maximum) {
                maximum = value;
                maximumPos = x.get(i);
            }

            if (minimum == null || value < minimum) {
                minimum = value;
                minimumPos = x.get(i);
            }

            if (lookForMax) {
                if (value < maximum) {
                    maxima.put(maximumPos, value);
                    minimum = value;
                    minimumPos = x.get(i);
                    lookForMax = false;
                }
            } else {
                if (value > minimum) {
                    minima.put(minimumPos, value);
                    maximum = value;
                    maximumPos = x.get(i);
                    lookForMax = true;
                }
            }
        }

        return maxima;
    }

    public static Map<Double, Double> getPeaks(ArrayList<Double> values) {
        ArrayList<Double> x = new ArrayList<Double>();
        int size = values.size();
        for (int i = 0; i < values.size(); i++) {
            x.add(MainActivity.RECORDING_TIME * i / size);
        }
        return getPeaks(values, x);
    }


    public static ArrayList<Double> splineInterpolate(ArrayList<Double> values) {
        int size = values.size();
        double[] x = new double[size];
        double[] y = new double[size];

        for (int i=0; i < size; i++) {
            x[i] = MainActivity.RECORDING_TIME * i / size;
            y[i] = values.get(i);
        }

        SplineInterpolator si = new SplineInterpolator();
        PolynomialSplineFunction f = si.interpolate(x, y);
        ArrayList<Double> res = new ArrayList<Double>();

        for(double d : x) {
             res.add(f.value(d));
        }
        return res;
    }
}
