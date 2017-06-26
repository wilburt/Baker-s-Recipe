package com.jadebyte.bakersrecipe.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.jadebyte.bakersrecipe.R;
import com.jadebyte.bakersrecipe.adapters.TestAdapter;

import java.util.ArrayList;
import java.util.List;

public class TestActivity extends AppCompatActivity {
    private static final int DATASET_COUNT = 50;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        // Create a RecyclerView, a LayoutManager, a data Adapter and wire everything up.
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.test_recyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);

        List<String> dataSet = new ArrayList<>(DATASET_COUNT);
        for (int i = 0; i < DATASET_COUNT; i++) {
            dataSet.add(getString(R.string.test_item_text) + i);
        }
        TestAdapter adapter = new TestAdapter(dataSet, getApplicationContext());
        recyclerView.setAdapter(adapter);
    }
}
