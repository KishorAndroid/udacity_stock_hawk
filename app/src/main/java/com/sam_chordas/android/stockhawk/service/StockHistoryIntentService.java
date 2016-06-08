package com.sam_chordas.android.stockhawk.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

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


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    StringBuilder urlStringBuilder = new StringBuilder();
    private String prepareYQLQuery(String stockSymbol) throws UnsupportedEncodingException {

        urlStringBuilder.append("https://query.yahooapis.com/v1/public/yql?q=");
        urlStringBuilder.append(URLEncoder.encode("select * from yahoo.finance.historicaldata where symbol "
                + " = \'" + stockSymbol + "\' and startDate = \'2009-09-11\' and endDate = \'2010-03-10\'", "UTF-8"));
        urlStringBuilder.append("&format=json&diagnostics=true&env=store%3A%2F%2Fdatatables."
                + "org%2Falltableswithkeys&callback=");

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
