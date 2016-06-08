package com.sam_chordas.android.stockhawk.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.service.StockHistoryIntentService;

/**
 * Created by kishor on 8/6/16.
 */
public class StockHistoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_graph);
        Intent intent = new Intent(this, StockHistoryIntentService.class);
        intent.putExtra("SYMBOL", getIntent().getStringExtra("SYMBOL"));
        startService(intent);
    }
}
