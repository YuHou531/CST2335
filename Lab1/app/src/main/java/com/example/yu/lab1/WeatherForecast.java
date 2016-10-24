package com.example.yu.lab1;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

public class WeatherForecast extends AppCompatActivity {

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    ProgressBar loadingimage = (ProgressBar) findViewById(R.id.progressBar2);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_forecast);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        loadingimage.setVisibility(View.VISIBLE);

    }

    class ForecastQuery extends AsyncTask<String,Integer,String> {

         String minTep,maxTep, currentTep;
         Bitmap currentWeather;

        protected String doInBackground(String... args) {
            // code where data is processing
            return null;
        }


        protected Integer onPostExecute(Integer result) {
            loadingimage.setVisibility(View.INVISIBLE);
            super.onPostExecute(result);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingimage.setVisibility(View.VISIBLE);
        }

    }

}

