package com.sam_chordas.android.stockhawk.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.github.mikephil.charting.charts.LineChart;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.Quote;
import com.sam_chordas.android.stockhawk.service.StockHistoryIntentService;

import java.util.ArrayList;

/**
 * Created by kishor on 8/6/16.
 */
public class StockHistoryActivity extends AppCompatActivity {

    LineChart chart;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_graph);
        chart = (LineChart) findViewById(R.id.chart);
        chart.setDescription(getResources().getString(R.string.chart_description));
    }

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver(historicalDataReceiver,
                new IntentFilter("stock-historical-data"));
        Intent intent = new Intent(this, StockHistoryIntentService.class);
        intent.putExtra("SYMBOL", getIntent().getStringExtra("SYMBOL"));
        startService(intent);
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(historicalDataReceiver);
    }

    private BroadcastReceiver historicalDataReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            ArrayList<Quote> quotes = intent.getParcelableArrayListExtra("STOCK_HISTORY");

            Log.d(StockHistoryActivity.class.getSimpleName(), quotes.size()+"");

            for(Quote quote : quotes){
                Log.d(StockHistoryActivity.class.getSimpleName(), quote.getDate() + " " + Float.valueOf(quote.getHigh()));
            }

        }
    };
}
