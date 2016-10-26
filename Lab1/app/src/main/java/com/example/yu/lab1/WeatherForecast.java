package com.example.yu.lab1;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.net.HttpURLConnection;
import java.net.URL;

public class WeatherForecast extends AppCompatActivity {
    protected static final String ACTIVITY_NAME = "WeatherForecast";

    TextView first, second, third;
   // private GoogleApiClient client;
   ProgressBar loadingimage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(ACTIVITY_NAME, "In onCreate()");

        setContentView(R.layout.xml_parsing);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        first = (TextView)findViewById(R.id.weather_current_temperature_textview);
        second = (TextView)findViewById(R.id.weather_min_temperature_textview);
        third = (TextView)findViewById(R.id.weather_max_temperature_textview);

        loadingimage = (ProgressBar) findViewById(R.id.weather_progressbar);

        loadingimage.setMax(3);
        loadingimage.setVisibility(View.VISIBLE);
        new ForecastQuery() .execute("http://api.openweathermap.org/data/2.5/weather?q=ottawa,ca&APPID=d99666875e0e51521f0040a3d97d0f6a&mode=xml&units=metric");

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });


  //

    }

    @Override
    protected void onStart() {
        super.onStart();
        // The activity is about to become visible.
        Log.i(ACTIVITY_NAME, "In onStart()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        // The activity has become visible (it is now "resumed").
        Log.i(ACTIVITY_NAME, "In onResume()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Another activity is taking focus (this activity is about to be "paused").
        Log.i(ACTIVITY_NAME, "In onPause()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        // The activity is no longer visible (it is now "stopped")
        Log.i(ACTIVITY_NAME, "In onStop()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // The activity is about to be destroyed.
        Log.i(ACTIVITY_NAME, "onDestroy()");
    }

    private int state;
    private class ForecastQuery extends AsyncTask<String,Integer,String> {
        private String minTep, maxTep, currentTep;
        private Bitmap currentWeatherBitMap;

        protected String doInBackground(String... args) {
            state = 0;


            try{

                URL url = new URL(args[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream istream = urlConnection.getInputStream();

                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(false);
                XmlPullParser xpp = factory.newPullParser();

                xpp.setInput(istream, "UTF8");
                boolean finished = false;
                int type = XmlPullParser.START_DOCUMENT;

                while(type != XmlPullParser.END_DOCUMENT) {

                    switch (type) {
                        case XmlPullParser.START_DOCUMENT:
                            break;
                        case XmlPullParser.END_DOCUMENT:
                            finished = true;
                            break;
                        case XmlPullParser.START_TAG:
                            String name = xpp.getName();
                            if (name.equals("AMessage")) {
                                String message = xpp.getAttributeValue(null, "message");

                                publishProgress( message );
                            }
                            break;
                        case XmlPullParser.END_TAG:
                            break;
                        case XmlPullParser.TEXT:
                            break;
                    }
                    type = xpp.next(); //advances to next xml event
                }
            }
            catch(Exception e)
            {
                Log.e("XML PARSING", e.getMessage());
            }

            return null;
        }

        public void onProgressUpdate(Integer ...updateInfo)
        {
            switch(state ++)
            {
                case 0:
                    first.setText(updateInfo[0]);
                    break;
                case 1:
                    second.setText(updateInfo[0]);
                    break;
                case 2:
                    third.setText(updateInfo[0]);
                    break;
            }
            loadingimage.setProgress( state );
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingimage.setVisibility(View.INVISIBLE);
        }

    }

}

