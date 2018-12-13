package com.example.android.camera2basic;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;

public class HistoryActivity extends AppCompatActivity {
    ListView mListView;
    ArrayAdapter<String> adapter;
    MyDBHandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setContentView(R.layout.activity_history);

        mListView = (ListView) findViewById(R.id.listView);
        registerForContextMenu(mListView);

        dbHandler = new MyDBHandler(this);
        ArrayList<String> dates = dbHandler.getAllDate();

        if(dates != null)
            populateListView(dates);
    }

    private void populateListView(ArrayList<String> dates) {
        Collections.reverse(dates);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, dates);
        mListView.setAdapter(adapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String date = adapterView.getItemAtPosition(i).toString();
                viewItem(date);
            }
        });
    }

    private void viewItem(String date) {
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

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.list_menu, menu);
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        String date = mListView.getItemAtPosition(info.position).toString();
        switch (item.getItemId()) {
            case R.id.view:
                viewItem(date);
                break;
            case R.id.delete:
                dbHandler.deleteHandler(date);
                adapter.remove(adapter.getItem(info.position));
                adapter.notifyDataSetChanged();
                break;
        }
        return true;
    }
}
