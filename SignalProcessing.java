package com.example.android.camera2basic;

import android.util.Log;

import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;
import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;

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

    public static ArrayList<Double> signalProcess(ArrayList<Double> values) {
        double frameRate = values.size() / MainActivity.RECORDING_TIME;

        values = butterworthFilter(values, frameRate);
        values = splineInterpolate(values);
//        values = fftTransform(values);
//        values = slidingWindowTransform(values, frameRate);
//        values = movingAverage(values);

        return values;
    }

    public static ArrayList<Double> butterworthFilter(ArrayList<Double> values, double frameRate) {
        ArrayList<Double> newValues = new ArrayList<Double>();
        Butterworth butterworth = new Butterworth();

        double initialValue = frameRate * MainActivity.CUTOFF_TIME;
        Log.d("DDD", Double.toString(frameRate) + " " + Double.toString(initialValue) + " " + Integer.toString(values.size()));

        double bpmLow = 40 / 60;
        double bpmHigh = 230 / 60;

        butterworth.bandPass(2, frameRate * 2, (bpmLow + bpmHigh) / 2, (bpmHigh - bpmLow) / 2);

        for(Double value : values) {
            double newValue = butterworth.filter(value);
            newValues.add(newValue);
        }

        newValues = new ArrayList<Double>(newValues.subList((int)Math.ceil(initialValue), newValues.size()));

        return newValues;
    }

    public static ArrayList<Double> slidingWindowTransform(ArrayList<Double> values, double frameRate) {
        int windowSize = values.size() / 2;
        int windowInterval = (int) (0.5 * frameRate);
        int steps = values.size() / windowInterval;

        ArrayList<Double> newValues = new ArrayList<Double>();

        for (int i = 0; i < steps; ++i) {
            int initialIndex = i * windowInterval;
            int finalIndex = Math.min(initialIndex + windowSize, values.size());
            ArrayList<Double> subValues = new ArrayList<Double>(values.subList(initialIndex, finalIndex));
            subValues = fftTransform(subValues);
            newValues.addAll(subValues);
        }

        return newValues;
    }

    public static ArrayList<Double> fftTransform(ArrayList<Double> values) {
        int initialSize = values.size();
        HanningWindow hw = new HanningWindow();

        int size = closestPowerOfTwo(initialSize);
        double[] newValues = new double[size];

        for(int i = 0; i < size; ++i) {
            if(i < initialSize) {
                newValues[i] = values.get(i) * hw.value(i, size);
            }
            else {
                newValues[i] = 0.0;
            }
        }

        ArrayList<Double> magValues = new ArrayList<Double>();
        FastFourierTransformer transformer = new FastFourierTransformer(DftNormalization.STANDARD);

        Complex[] complexResults = transformer.transform(newValues, TransformType.FORWARD);

        for(Complex c : complexResults) {
            magValues.add(c.getReal() * c.getReal() + c.getImaginary() * c.getImaginary());
        }

        return new ArrayList<Double>(magValues);
    }

    private static int closestPowerOfTwo(int size) {
        int power = 1;
        while(power < size) {
            power <<= 1;
        }
        return power;
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

        for (int i=0; i < size; ++i) {
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
