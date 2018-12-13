package com.example.android.camera2basic;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class HistoryActivity extends AppCompatActivity {
    ListView mListView;
    MyDBHandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        mListView = (ListView) findViewById(R.id.listView);
        dbHandler = new MyDBHandler(this);
        ArrayList<String> dates = dbHandler.getAllDate();

        if(dates != null)
            populateListView(dates);
    }

    private void populateListView(ArrayList<String> dates) {
        ListAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, dates);
        mListView.setAdapter(adapter);

        //set an onItemClickListener to the ListView
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String date = adapterView.getItemAtPosition(i).toString();
                ArrayList<Object> data = dbHandler.findHandler(date); //get the id associated with that name

                Intent historyResult = new Intent(HistoryActivity.this, HistoryResultActivity.class);
                historyResult.putExtra("DateTime", (String) data.get(0));
                historyResult.putExtra("BPM", (Double) data.get(1));
                historyResult.putExtra("AVNN", (Double) data.get(2));
                historyResult.putExtra("SDNN", (Double) data.get(3));
                historyResult.putExtra("RMSSD", (Double) data.get(4));
                historyResult.putExtra("PPN50", (Double) data.get(5));
                startActivity(historyResult);
            }
        });
    }
}
