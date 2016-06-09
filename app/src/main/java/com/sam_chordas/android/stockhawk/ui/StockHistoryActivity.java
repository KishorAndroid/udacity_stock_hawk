package com.sam_chordas.android.stockhawk.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.db.chart.Tools;
import com.db.chart.model.LineSet;
import com.db.chart.model.Point;
import com.db.chart.view.AxisController;
import com.db.chart.view.ChartView;
import com.db.chart.view.LineChartView;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.Quote;
import com.sam_chordas.android.stockhawk.service.StockHistoryIntentService;

import java.util.ArrayList;

/**
 * Created by kishor on 8/6/16.
 */
public class StockHistoryActivity extends AppCompatActivity {

    LineSet dataset;
    private String[] labels;
    private float[] values;

    LineChartView chartView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_graph);

        chartView = (LineChartView) findViewById(R.id.linechart);
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

            labels = new String[quotes.size()];
            values = new float[quotes.size()];
            dataset = new LineSet();

            Log.d(StockHistoryActivity.class.getSimpleName(), quotes.size()+"");

            for(Quote quote : quotes){
                Log.d(StockHistoryActivity.class.getSimpleName(), quote.getDate() + " " + Float.valueOf(quote.getHigh()));
                dataset.addPoint(quote.getDate(), Float.valueOf(quote.getHigh()));
            }

            chartView.setBackgroundColor(Color.WHITE);

            chartView.addData(dataset);
            chartView.show();

        }
    };
}
