package com.example.android.camera2basic;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class HRVFragment extends Fragment {
    private HeartData heartData;

    TextView avnnView;
    TextView sdnnView;
    TextView rmssdView;
    TextView pnn50View;
    GraphView graphView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_hrv, container, false);

        graphView = (GraphView) view.findViewById(R.id.graph2);
        avnnView = (TextView) view.findViewById(R.id.avnnView);
        sdnnView = (TextView) view.findViewById(R.id.sdnnView);
        rmssdView = (TextView) view.findViewById(R.id.rmssdView);
        pnn50View = (TextView) view.findViewById(R.id.pnn50View);

        graphView.getGridLabelRenderer().setHorizontalLabelsVisible(false);
        graphView.getGridLabelRenderer().setVerticalLabelsVisible(false);

        heartData = HeartRateFragment.getHeartData();
        drawGraph(heartData.getRrInterval());

        avnnView.setText(String.format("%.3f", heartData.getAVNN()));
        sdnnView.setText(String.format("%.3f", heartData.getSDNN()));
        rmssdView.setText(String.format("%.3f", heartData.getRMSSD()));
        pnn50View.setText(String.format("%.1f", heartData.getPPN50()) + "%");

        return view;
    }

    private void drawGraph(ArrayList<Double> values) {
        int size = values.size();
        DataPoint[] dataPoints = new DataPoint[size];

        for(int i = 0; i < size; ++i) {
            dataPoints[i] = new DataPoint(i, values.get(i));
        }

        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(dataPoints);
        graphView.addSeries(series);
    }
}
