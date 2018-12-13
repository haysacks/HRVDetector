package com.example.android.camera2basic;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    public static final double RECORDING_TIME = 15.0;
    public static final double CUTOFF_TIME = 5.0;
    private static MyDBHandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);

        dbHandler = new MyDBHandler(getApplicationContext());
    }

    public void detectHeartRate(View view) {
        Intent intent = new Intent(getApplicationContext(), InstructionActivity.class);
        startActivityForResult(intent, 3);
    }

    public void viewHistory(View view) {
        Intent intent = new Intent(getApplicationContext(), HistoryActivity.class);
        startActivity(intent);
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

    public static MyDBHandler getDbHandler() {
        return dbHandler;
    }
}
