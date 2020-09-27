package com.sharai.earthquakes;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import javax.net.ssl.HttpsURLConnection;

public class DataLoader extends AsyncTaskLoader<String> {

    Context context;
    String urlStr;

    public DataLoader(Context context, String urlStr) {
        super(context);
        this.context = context;
        this.urlStr = urlStr;
    }

    @Nullable
    @Override
    public String loadInBackground() {
        MainActivity.alertTextView.setText(R.string.loading);
        URL url;
        String result = null;
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            //Create a new URL
            url = new URL(urlStr);

            //HttpsURLConnection
            urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000 /*time in milliseconds*/);
            urlConnection.setConnectTimeout(15000 /*time in milliseconds*/);
            urlConnection.connect();

            //InputStream
            inputStream = urlConnection.getInputStream();

            //Data collection
            StringBuilder output = new StringBuilder();
            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
                BufferedReader reader = new BufferedReader(inputStreamReader);
                String line = reader.readLine();
                while (line != null) {
                    output.append(line);
                    line = reader.readLine();
                }
            }

            //Assign the data collected
            result = output.toString();

        } catch (IOException e) {
            e.printStackTrace();
        } finally /*Close everything*/ {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }
}
