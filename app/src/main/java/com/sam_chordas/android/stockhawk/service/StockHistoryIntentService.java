package com.sam_chordas.android.stockhawk.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.sam_chordas.android.stockhawk.data.Quote;
import com.sam_chordas.android.stockhawk.rest.Utils;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by kishor on 6/6/16.
 */
public class StockHistoryIntentService extends IntentService {

    public StockHistoryIntentService() {
        super(StockHistoryIntentService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        try {
            String historicalData = fetchData(prepareYQLQuery(intent.getStringExtra("SYMBOL")));

            //TODO Remove
            Log.d(StockHistoryIntentService.class.getSimpleName(), "SYMBOL " + intent.getStringExtra("SYMBOL"));
            Log.d(StockHistoryIntentService.class.getSimpleName(), historicalData);

            ArrayList<Quote> quotes = Utils.jsonToQuoteArrayList(historicalData);

            Intent newIntent = new Intent("stock-historical-data");
            newIntent.putParcelableArrayListExtra("STOCK_HISTORY", quotes);
            LocalBroadcastManager.getInstance(this).sendBroadcast(newIntent);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    StringBuilder urlStringBuilder = new StringBuilder();
    private String prepareYQLQuery(String stockSymbol) throws UnsupportedEncodingException {

        Date currentDate = Calendar.getInstance().getTime();
        java.text.SimpleDateFormat simpleDateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String formattedEndDate = simpleDateFormat.format(currentDate);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -1);
        String formattedStartDate = simpleDateFormat.format(cal.getTime());


        urlStringBuilder.append("https://query.yahooapis.com/v1/public/yql?q=");
        urlStringBuilder.append(URLEncoder.encode("select * from yahoo.finance.historicaldata where symbol "
                + " = \'" + stockSymbol + "\' and startDate = \'"+formattedStartDate+"\' and endDate = \'"+formattedEndDate+"\'", "UTF-8"));
        urlStringBuilder.append("&format=json&env=store%3A%2F%2Fdatatables."
                + "org%2Falltableswithkeys&callback=");

        Log.d(StockHistoryIntentService.class.getSimpleName(), urlStringBuilder.toString());

        return urlStringBuilder.toString();
    }

    private OkHttpClient client = new OkHttpClient();
    String fetchData(String url) throws IOException {

        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }
}
