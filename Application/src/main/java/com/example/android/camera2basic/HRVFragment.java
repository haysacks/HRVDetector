package com.example.android.camera2basic;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.TooltipCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;

public class HRVFragment extends Fragment {
    private HeartData heartData;

    LinearLayout avnnLayout;
    LinearLayout sdnnLayout;
    LinearLayout rmssdLayout;
    LinearLayout pnn50Layout;
    TextView avnnView;
    TextView sdnnView;
    TextView rmssdView;
    TextView pnn50View;
    GraphView graphView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_hrv, container, false);

        graphView = view.findViewById(R.id.graph2);
        avnnView = view.findViewById(R.id.avnnView);
        sdnnView = view.findViewById(R.id.sdnnView);
        rmssdView = view.findViewById(R.id.rmssdView);
        pnn50View = view.findViewById(R.id.pnn50View);
        avnnLayout = view.findViewById(R.id.avnnLayout);
        sdnnLayout = view.findViewById(R.id.sdnnLayout);
        rmssdLayout = view.findViewById(R.id.rmssdLayout);
        pnn50Layout = view.findViewById(R.id.pnn50Layout);

        setTooltip();

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

    private void setTooltip() {
//        TooltipCompat.setTooltipText(avnnLayout, getContext().getString(R.string.avnn));

        avnnLayout.setOnLongClickListener(
                new View.OnLongClickListener() {
                    public boolean onLongClick(View v) {
                        Toast.makeText(v.getContext(), R.string.avnn, Toast.LENGTH_SHORT).show();
                        return true;
                    }
                }
        );

        sdnnLayout.setOnLongClickListener(
                new View.OnLongClickListener() {
                    public boolean onLongClick(View v) {
                        Toast.makeText(v.getContext(), R.string.sdnn, Toast.LENGTH_SHORT).show();
                        return true;
                    }
                }
        );

        rmssdLayout.setOnLongClickListener(
                new View.OnLongClickListener() {
                    public boolean onLongClick(View v) {
                        Toast.makeText(v.getContext(), R.string.rmssd, Toast.LENGTH_SHORT).show();
                        return true;
                    }
                }
        );

        pnn50Layout.setOnLongClickListener(
                new View.OnLongClickListener() {
                    public boolean onLongClick(View v) {
                        Toast.makeText(v.getContext(), R.string.pnn50, Toast.LENGTH_SHORT).show();
                        return true;
                    }
                }
        );
    }
}
