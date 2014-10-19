package com.michaelotte.mlo.buttermellow;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by michael on 10/6/14.
 */
/**
 * A placeholder fragment containing a simple view.
 * fragment is a modular container
 */
public class ForecastFragment extends Fragment {

    /**
     * Global mForecastAdapter data
     */
    private ArrayAdapter<String> mForecastAdapter;

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
            updateWeather();
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
        mForecastAdapter =
                new ArrayAdapter<String>(
                        // The current context (this fragment's parent activity that are global)
                        getActivity(),
                        R.layout.list_item_forecast,
                        R.id.list_item_forecast_textview,
                        // Forecast data in an array
                        weekForecast);

        ListView listView = (ListView) rootView.findViewById(
                R.id.listview_forecast);
        listView.setAdapter(mForecastAdapter);

        // setOnItemClickListener sets the click listener to create a new activity
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String forecast = mForecastAdapter.getItem(position);
                Toast.makeText(getActivity(), forecast, Toast.LENGTH_SHORT).show();
                // Launch the detailActivity intent
                // Intents help two disparate components of an app or outside an app to be connected
                // Here's an explicit intent to open up the DetailActivity View
                Intent detailIntent = new Intent(getActivity(), DetailActivity.class)
                        .putExtra(Intent.EXTRA_TEXT, forecast);
                startActivity(detailIntent);
            }
        });

        return rootView;
    }

    private void updateWeather() {
        FetchWeatherTask weatherTask = new FetchWeatherTask();
        String location = PreferenceManager.getDefaultSharedPreferences(getActivity())
                .getString(getString(R.string.pref_location_key),
                        getString(R.string.pref_default_location));
        String units = PreferenceManager.getDefaultSharedPreferences(getActivity())
                .getString(getString(R.string.pref_units_key), getString(R.string.pref_units_default));
        weatherTask.execute(location, units);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateWeather();
    }

    public class FetchWeatherTask extends AsyncTask<String, Void, String[]> {

        private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();

        @Override
        protected void onPostExecute(String[] result) {
            /**
             * Initialize new forecast data after data is retrieved from OWM API
             */
            if (result != null) {
                mForecastAdapter.clear();
                for (String dayForecastStr : result) {
                    mForecastAdapter.add(dayForecastStr);
                }
            }
        }

        @Override
        protected String[] doInBackground(String... params) {
            // If there's no zip code, there's nothing to look up.  Verify size of params.
            if (params.length == 0) {
                return null;
            }

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String forecastJsonStr = null;

            String format = "json";
            int numDays = 7;

            try {
                final String FORECAST_BASE_URL = "http://api.openweathermap.org/data/2.5/forecast/daily?";
                final String ID_PARAM = "id";
                final String FORMAT_PARAM = "mode";
                final String UNITS_PARAM = "units";
                final String DAYS_PARAM = "cnt";

                Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                        .appendQueryParameter(ID_PARAM, params[0])
                        .appendQueryParameter(FORMAT_PARAM, format)
                        .appendQueryParameter(UNITS_PARAM, params[1])
                        .appendQueryParameter(DAYS_PARAM, Integer.toString(numDays))
                        .build();

                URL url = new URL(builtUri.toString());

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

            try {
                WeatherDataParser WDP = new WeatherDataParser();
                return WDP.getWeatherDataFromJson(forecastJsonStr, numDays);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;
        }
    }
}