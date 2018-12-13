package com.example.android.camera2basic;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;

public class HeartRateFragment extends Fragment {
    private static HeartData heartData;

    TextView bpm;
    GraphView graphView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_heart_rate, container, false);

        bpm = view.findViewById(R.id.bpm);
        graphView = view.findViewById(R.id.graph);

        graphView.getGridLabelRenderer().setHorizontalLabelsVisible(false);
        graphView.getGridLabelRenderer().setVerticalLabelsVisible(false);

        ArrayList<Double> values = getValues();

        bpm.setText(Double.toString(heartData.getBPM()));
        drawGraph(values);

        return view;
    }

    public ArrayList<Double> getValues() {
        ArrayList<Integer> colorValues = CameraFragment.getColorValues();
        ArrayList<Double> hueValues = new ArrayList<Double>();

        for(int i : colorValues) {
            float[] hsv = new float[3];
            Color.RGBToHSV(Color.red(i), Color.green(i), Color.blue(i), hsv);
            hueValues.add((double)hsv[0]);
        }

        hueValues = SignalProcessing.signalProcess(hueValues);

        for(double d : hueValues) {
            System.out.println(d);
        }

        heartData = new HeartData(SignalProcessing.getPeaks(hueValues));

        return hueValues;
    }

    private void drawGraph(ArrayList<Double> values) {
        int size = values.size();
        DataPoint[] dataPoints = new DataPoint[size];

        for(int i = 0; i < size; ++i) {
            dataPoints[i] = new DataPoint(MainActivity.RECORDING_TIME * i / size, values.get(i));
        }

        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(dataPoints);
        graphView.addSeries(series);
    }

    public static HeartData getHeartData() {
        return heartData;
    }
}
