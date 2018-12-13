package com.example.android.camera2basic;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class HistoryResultActivity extends AppCompatActivity {
    TextView dateTimeView;
    TextView bpmView;
    TextView avnnView;
    TextView sdnnView;
    TextView rmssdView;
    TextView ppn50View;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_result);

        Intent receivedIntent = getIntent();
        String dateTime = receivedIntent.getStringExtra("DateTime");
        Double bpm = receivedIntent.getDoubleExtra("BPM", 0);
        Double avnn = receivedIntent.getDoubleExtra("AVNN", 0);
        Double sdnn = receivedIntent.getDoubleExtra("SDNN", 0);
        Double rmssd = receivedIntent.getDoubleExtra("RMSSD", 0);
        Double ppn50 = receivedIntent.getDoubleExtra("PPN50", 0);

        dateTimeView = (TextView) findViewById(R.id.dateTimeView);
        bpmView = (TextView) findViewById(R.id.bpm);
        avnnView = (TextView) findViewById(R.id.avnnView);
        sdnnView = (TextView) findViewById(R.id.sdnnView);
        rmssdView = (TextView) findViewById(R.id.rmssdView);
        ppn50View = (TextView) findViewById(R.id.pnn50View);

        dateTimeView.setText(dateTime);
        bpmView.setText(String.format("%.1f", bpm));
        avnnView.setText(String.format("%.3f", avnn));
        sdnnView.setText(String.format("%.3f", sdnn));
        rmssdView.setText(String.format("%.3f", rmssd));
        ppn50View.setText(String.format("%.1f", ppn50) + "%");
    }
}
