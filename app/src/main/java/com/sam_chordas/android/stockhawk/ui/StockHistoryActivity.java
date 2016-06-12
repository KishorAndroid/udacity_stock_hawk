package com.sam_chordas.android.stockhawk.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.Quote;
import com.sam_chordas.android.stockhawk.service.StockHistoryIntentService;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by kishor on 8/6/16.
 */
public class StockHistoryActivity extends AppCompatActivity {

    LineChart chart;
    ContentLoadingProgressBar loadingProgressBar;
    ArrayList<Quote> histoicalData = new ArrayList<>();
    Bundle savedInstanceState;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_graph);

        chart = (LineChart) findViewById(R.id.chart);
        chart.setDescription(getResources().getString(R.string.chart_description));

        loadingProgressBar = (ContentLoadingProgressBar) findViewById(R.id.content_loading);

        this.savedInstanceState = savedInstanceState;
    }

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver(historicalDataReceiver,
                new IntentFilter("stock-historical-data"));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        if(isConnected) {
            if(savedInstanceState != null && !savedInstanceState.getParcelableArrayList("HISTORICAL_DATA").isEmpty()){
                histoicalData = savedInstanceState.getParcelableArrayList("HISTORICAL_DATA");
                new PopulateChartView().execute(histoicalData);
            }else {
                Intent intent = new Intent(this, StockHistoryIntentService.class);
                intent.putExtra("SYMBOL", getIntent().getStringExtra("SYMBOL"));
                startService(intent);
            }
        }
        else {
            networkToast();
        }
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

            if(quotes != null && quotes.size() > 0) {

                histoicalData = quotes;

                new PopulateChartView().execute(quotes);

            }
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showChart(LineData lineData){
        loadingProgressBar.setVisibility(View.INVISIBLE);
        chart.setVisibility(View.VISIBLE);
        chart.setData(lineData);
        chart.invalidate();
    }

    private class PopulateChartView extends AsyncTask<ArrayList<Quote>, Void, LineData>{

        @Override
        protected LineData doInBackground(ArrayList<Quote>... arrayLists) {
            ArrayList<Quote> quotes = arrayLists[0];

            Collections.reverse(quotes);

            ArrayList<Entry> highValues = new ArrayList<>();
            ArrayList<Entry> lowValues = new ArrayList<>();
            ArrayList<Entry> openValues = new ArrayList<>();
            ArrayList<Entry> closeValues = new ArrayList<>();

            ArrayList<String> xVals = new ArrayList<String>();

            Log.d(StockHistoryActivity.class.getSimpleName(), quotes.size() + "");

            for (int i = 0; i < quotes.size(); i++) {
                Entry highEntry = new Entry(Float.valueOf(quotes.get(i).getHigh()), i);
                highValues.add(highEntry);

                Entry lowEntry = new Entry(Float.valueOf(quotes.get(i).getLow()), i);
                lowValues.add(lowEntry);

                Entry openEntry = new Entry(Float.valueOf(quotes.get(i).getOpen()), i);
                openValues.add(openEntry);

                Entry closeEntry = new Entry(Float.valueOf(quotes.get(i).getClose()), i);
                closeValues.add(closeEntry);

                xVals.add(quotes.get(i).getDate());
            }

            LineDataSet setHighValues = new LineDataSet(highValues, getResources().getString(R.string.chart_legend_high));
            setHighValues.setAxisDependency(YAxis.AxisDependency.LEFT);
            setHighValues.setColor(Color.RED);

            LineDataSet setLowValues = new LineDataSet(lowValues, getResources().getString(R.string.chart_legend_low));
            setLowValues.setAxisDependency(YAxis.AxisDependency.LEFT);
            setLowValues.setColor(Color.parseColor("#3232FF"));

            LineDataSet setOpenValues = new LineDataSet(openValues, getResources().getString(R.string.chart_legend_open));
            setOpenValues.setAxisDependency(YAxis.AxisDependency.LEFT);
            setOpenValues.setColor(Color.parseColor("#4C4CFF"));

            LineDataSet setCloseValues = new LineDataSet(closeValues, getResources().getString(R.string.chart_legend_close));
            setCloseValues.setAxisDependency(YAxis.AxisDependency.LEFT);
            setCloseValues.setColor(Color.parseColor("#6666FF"));

            ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
            dataSets.add(setHighValues);
            dataSets.add(setLowValues);
            dataSets.add(setOpenValues);
            dataSets.add(setCloseValues);

            LineData data = new LineData(xVals, dataSets);

            return data;
        }

        @Override
        protected void onPostExecute(LineData lineData) {
            super.onPostExecute(lineData);
            showChart(lineData);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("HISTORICAL_DATA", histoicalData);
    }

    public void networkToast(){
        Toast.makeText(this, getString(R.string.network_toast), Toast.LENGTH_SHORT).show();
    }
}