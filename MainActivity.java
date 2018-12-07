package com.example.android.camera2basic;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    public static final double RECORDING_TIME = 10.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);
    }

    public void detectHeartRate(View view) {
        Intent intent = new Intent(getApplicationContext(), InstructionActivity.class);
        startActivityForResult(intent, 3);
    }

    public void startDetectHeartRate() {
        Intent intent = new Intent(getApplicationContext(), CameraActivity.class);
        startActivityForResult(intent, 1);
    }

    public void calculateHeartRate() {
        Intent intent = new Intent(getApplicationContext(), ResultsActivity.class);
        startActivityForResult(intent, 2);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                calculateHeartRate();
            }
        }

        if(requestCode == 3) {
            if(resultCode == RESULT_OK) {
                startDetectHeartRate();
            }
        }
    }
}
