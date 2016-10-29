package com.example.yu.lab1;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class WeatherForecast extends AppCompatActivity {
    protected static final String ACTIVITY_NAME = "WeatherForecast";
    String minTep, maxTep, currentTep;
    TextView first, second, third;
    ProgressBar loadingImageBar;
    ImageView weatherView;
    Bitmap currentWeatherBitMap;
    String iconName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(ACTIVITY_NAME, "In onCreate()");
        setContentView(R.layout.content_weather_forecast);

        first = (TextView) findViewById(R.id.weather_current_temperature_textview);
        second = (TextView) findViewById(R.id.weather_min_temperature_textview);
        third = (TextView) findViewById(R.id.weather_max_temperature_textview);

        loadingImageBar = (ProgressBar) findViewById(R.id.weather_progressbar);
        loadingImageBar.setMax(3);
        loadingImageBar.setVisibility(View.VISIBLE);

        weatherView = (ImageView) findViewById(R.id.weather_image_view);

        //when back to main and call this AsyncTask again, not quite work
        new ForecastQuery().execute("http://api.openweathermap.org/data/2.5/weather?q=ottawa,ca&APPID=d99666875e0e51521f0040a3d97d0f6a&mode=xml&units=metric");
    }

    private int state;

    private class ForecastQuery extends AsyncTask<String, Integer, String> {
        
        public String doInBackground(String... args) {
            state = 0;

            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL(args[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream istream = urlConnection.getInputStream();

                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(false);
                XmlPullParser xpp = factory.newPullParser();

                xpp.setInput(istream, "UTF8");
                boolean finished = false;
                int type = XmlPullParser.START_DOCUMENT;

                while (type != XmlPullParser.END_DOCUMENT) {

                    switch (type) {
                        case XmlPullParser.START_DOCUMENT:
                            break;
                        case XmlPullParser.END_DOCUMENT:
                            finished = true;
                            break;
                        case XmlPullParser.START_TAG:
                            String name = xpp.getName();
                            if (name.equals("temperature")) {
                                currentTep = xpp.getAttributeValue(null, "value");
                                publishProgress(25);
                                minTep = xpp.getAttributeValue(null, "min");
                                publishProgress(50);
                                maxTep = xpp.getAttributeValue(null, "max");
                                publishProgress(75);
                            }
                            if (name.equals("weather")) {
                                iconName = xpp.getAttributeValue(null, "icon");
                            }
                            break;
                        case XmlPullParser.END_TAG:
                            break;
                        case XmlPullParser.TEXT:
                            break;
                    }
                    type = xpp.next(); //advances to next xml event
                }
            } catch (Exception e) {
                Log.e("XML PARSING", e.getMessage());
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }

            return null;
        }

        public void onProgressUpdate(Integer... updateInfo) {
            loadingImageBar.setVisibility(View.VISIBLE);

            switch (state++) {
                case 0:
                    //first.setText("current temperature " + currentTep + "°C");
                    loadingImageBar.setProgress(updateInfo[0]);
                    break;
                case 1:
                    //second.setText("min temperature " + minTep + "°C");
                    loadingImageBar.setProgress(updateInfo[0]);
                    break;
                case 2:
                    //third.setText("max temperature " + maxTep+ "°C");
                    loadingImageBar.setProgress(updateInfo[0]);
                    break;
            }

            if(iconName != null) {
                String imageURL = "http://openweathermap.org/img/w/" + iconName + ".png";
                while (currentWeatherBitMap == null) {
                    new DownloadBitmap().execute(imageURL);
                }
            }
        }

        public void onPostExecute(String result) {
            first.setText("current temperature " + currentTep + "°C");
            second.setText("min temperature " + minTep + "°C");
            third.setText("max temperature " + maxTep + "°C");
            weatherView.setImageBitmap(currentWeatherBitMap);
            loadingImageBar.setVisibility(View.INVISIBLE);
        }
    }

    private class DownloadBitmap extends AsyncTask<String, Void, String> {

        public String doInBackground(String... args) {
            HttpURLConnection connection = null;
            try {
                URL url = new URL(args[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                int responseCode = connection.getResponseCode();
                if (responseCode == 200) {
                    currentWeatherBitMap = BitmapFactory.decodeStream(connection.getInputStream());
                    try {
                        FileOutputStream outputStream = openFileOutput( iconName + ".png", Context.MODE_PRIVATE);
                        currentWeatherBitMap.compress(Bitmap.CompressFormat.PNG, 80, outputStream);
                        outputStream.flush();
                        outputStream.close();
                    } catch (Exception e) {
                        Log.i(ACTIVITY_NAME, "DownloadBitmap doInBackground with Exception " + e.getMessage());
                    }
                } else {
                    return null;
                }
                return null;
            } catch (Exception e) {
                Log.i(ACTIVITY_NAME, "DownloadBitmap doInBackground with Exception " + e.getMessage());
                return null;
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }

        public void onProgressUpdate(Void... args) {
            //Nothing to do
        }

        public void onPostExecute(String result) {
            //Nothing to do
        }
    }
}

