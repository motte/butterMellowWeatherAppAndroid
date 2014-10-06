package com.michaelotte.mlo.buttermellow;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by michael on 10/6/14.
 */
/**
 * A placeholder fragment containing a simple view.
 * fragment is a modular container
 */
public class ForecastFragment extends Fragment {

    public ForecastFragment() {
    }

    /**
     * onCreate creates the fragment and happens before the onCreateView
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.forecastfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            FetchWeatherTask weatherTask = new FetchWeatherTask();
            weatherTask.execute();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        String[] forecastArray = {
                "Today - Sunny - 88/63",
                "Tomorrow - NOOOOO stay inside - 0/-10",
                "Tuesday - Sunny - 88/65",
                "Wednesday - Warm - 76/70",
                "Thursday - Cool - 70/60",
                "Friday - Cold - 32/0",
                "Saturday - Brrrr - 0/-30",
                "Sunday - Cool - 70/60",
                "Monday - Cold - 32/0",
                "Tuesday - Brrrr - 0/-30",
        };

        List<String> weekForecast = new ArrayList<String>(
                Arrays.asList(forecastArray)
        );

        /**
         * The ArrayAdapter does all the control of the view(s)
         * This one binds a string and gets all the data associated with it.
         * It also considers the orders of the views
         * This will pass along the view information to the bound view itself
         */
        ArrayAdapter<String> forecastAdapter =
                new ArrayAdapter<String>(
                        // The current context (this fragment's parent activity that are global)
                        getActivity(),
                        R.layout.list_item_forecast,
                        R.id.list_item_forecast_textview,
                        // Forecast data in an array
                        weekForecast);

        ListView listView = (ListView) rootView.findViewById(
                R.id.listview_forecast);
        listView.setAdapter(forecastAdapter);


        return rootView;
    }

    public class FetchWeatherTask extends AsyncTask<Void, Void, Void> {

        private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();

        @Override
        protected Void doInBackground(Void... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String forecastJsonStr = null;

            try {
                String cityId = "5393052";
                String urlStr = "http://api.openweathermap.org/data/2.5/forecast/daily?&mode=json&units=metric&cnt=7&id=" + cityId;
                URL url = new URL(urlStr);

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // nothing needs to happen after this so just return null
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty. No need to parse
                    return null;
                }
                forecastJsonStr = buffer.toString();

                Log.v(LOG_TAG, "Forecast JSON String: " + forecastJsonStr);
            } catch (IOException e) {
                Log.e(LOG_TAG, "ForecastFragment 119 Error ", e);

                return null;
            } finally{
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "ForecastFragment 130 Error closing stream", e);
                    }
                }
            }
            return null;
        }
    }
}