package com.example.yu.lab1;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class WeatherForecast extends AppCompatActivity {
    protected static final String ACTIVITY_NAME = "WeatherForecast";
    String minTep, maxTep, currentTep;
    TextView first, second, third;
    ProgressBar loadingImageBar;
    ImageView weatherView;
    Bitmap currentWeatherBitMap;
    String iconName;
    private int state;

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

        new ForecastQuery().execute("http://api.openweathermap.org/data/2.5/weather?q=ottawa,ca&APPID=d99666875e0e51521f0040a3d97d0f6a&mode=xml&units=metric");
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
        Log.i(ACTIVITY_NAME, "In onDestroy()");
    }

    public boolean fileExistance(String fname) {
        File file = getBaseContext().getFileStreamPath(fname);
        return file.exists();
    }

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
                int type = XmlPullParser.START_DOCUMENT;

                while (type != XmlPullParser.END_DOCUMENT) {

                    switch (type) {
                        case XmlPullParser.START_DOCUMENT:
                            break;
                        case XmlPullParser.END_DOCUMENT:
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
                    loadingImageBar.setProgress(updateInfo[0]);
                    break;
                case 1:
                    loadingImageBar.setProgress(updateInfo[0]);
                    break;
                case 2:
                    loadingImageBar.setProgress(updateInfo[0]);
                    break;
            }

            if (iconName != null) {
                String imageURL = "http://openweathermap.org/img/w/" + iconName + ".png";
                String fileName = iconName + ".png";
                boolean exist = fileExistance(fileName);
                if (exist) {
                    Log.i(ACTIVITY_NAME, fileName + " exists and no need to download again!");
                    FileInputStream fis = null;
                    File file = getBaseContext().getFileStreamPath(fileName);
                    try {
                        fis = new FileInputStream(file);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        currentWeatherBitMap = null;
                    }
                    currentWeatherBitMap = BitmapFactory.decodeStream(fis);
                } else {
                    Log.i(ACTIVITY_NAME, fileName + " does not exist and need to download!");
                    new DownloadBitmap().execute(imageURL);
                }

                if (currentWeatherBitMap == null) {
                    new DownloadBitmap().execute(imageURL);
                }
            }
        }

        public void onPostExecute(String result) {
            first.setText("current temperature " + currentTep + "°C");
            second.setText("min temperature " + minTep + "°C");
            third.setText("max temperature " + maxTep + "°C");
            if (currentWeatherBitMap != null) {
                weatherView.setImageBitmap(currentWeatherBitMap);
            }
            loadingImageBar.setVisibility(View.INVISIBLE);
        }
    }

    private class DownloadBitmap extends AsyncTask<String, Integer, String> {

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
                        FileOutputStream fos = openFileOutput(iconName + ".png", Context.MODE_PRIVATE);
                        ByteArrayOutputStream outstream = new ByteArrayOutputStream();
                        currentWeatherBitMap.compress(Bitmap.CompressFormat.PNG, 80, outstream);
                        byte[] byteArray = outstream.toByteArray();
                        fos.write(byteArray);
                        fos.close();
                        publishProgress(100);
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

        public void onProgressUpdate(Integer... updateInfo) {
            loadingImageBar.setProgress(updateInfo[0]);
        }

        public void onPostExecute(String result) {
            weatherView.setImageBitmap(currentWeatherBitMap);
        }
    }
}

