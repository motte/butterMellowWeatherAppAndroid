package com.michaelotte.mlo.buttermellow;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
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
import android.widget.TextView;
import android.widget.Toast;

import com.michaelotte.mlo.buttermellow.data.WeatherContract;

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
 * Encapsulates fetching the forecast and displaying it as a {@link ListView} layout
 *
 * A placeholder fragment containing a simple view.
 * fragment is a modular container
 */
public class ForecastFragment extends Fragment implements LoaderCallbacks<Cursor> {

    /**
     * Global mForecastAdapter data
     */
    private ForecastAdapter mForecastAdapter;

    private String mLocation;

    // Each loader has an id so that multiple loaders can be running at once
    private static final int FORECAST_LOADER = 0;

    private final String LOG_TAG = ForecastFragment.class.getSimpleName();

    // Specify the columns we need
    private static final String[] FORECAST_COLUMNS = {
            WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
            WeatherContract.WeatherEntry.COLUMN_DATETEXT,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING
    };

    // These indices are tied to FORECAST_COLUMNS.  If FORECAST_COLUMNS changes, these must change
    public static final int COL_WEATHER_ID = 0;
    public static final int COL_WEATHER_DATE = 1;
    public static final int COL_WEATHER_DESC = 2;
    public static final int COL_WEATHER_MAX_TEMP = 3;
    public static final int COL_WEATHER_MIN_TEMP = 4;
    public static final int COL_LOCATION_SETTING = 5;

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
        Log.d(LOG_TAG, "onCreate");SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        // Save the latLon for the map menu option
        SharedPreferences.Editor editor = sharedPrefs.edit();
        // This should be the current lat and lon for the currently set location
        editor.putString("latLon", "");
        editor.apply();
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
        mForecastAdapter = new ForecastAdapter(getActivity(), null, 0);

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        ListView listView = (ListView) rootView.findViewById(R.id.listview_forecast);
        listView.setAdapter(mForecastAdapter);

        // setOnItemClickListener sets the click listener to create a new activity
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Cursor cursor = mForecastAdapter.getCursor();
                if (cursor != null && cursor.moveToPosition(position)) {
                    Intent intent = new Intent(getActivity(), DetailActivity.class)
                            .putExtra(DetailActivity.DATE_KEY, cursor.getString(COL_WEATHER_DATE));
                    startActivity(intent);
                }
            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // notice I use the loader id (FORECAST_LOADER)
        getLoaderManager().initLoader(FORECAST_LOADER, null, this);
        // loaders are initialized in onActivityCreated because their lifecycle is bound to the activity, not the fragment
        super.onActivityCreated(savedInstanceState);
    }

    private void updateWeather() {
        String location = Utility.getPreferredLocation(getActivity());
        new FetchWeatherTask(getActivity()).execute(location);

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        // Save the latLon for the map menu option
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString("latLon", "Santa Cruz, CA");
        editor.apply();
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(LOG_TAG, "onStart");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(LOG_TAG, "onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(LOG_TAG, "onStop");
    }

    @Override
    public void onResume() {
        // check if our weather location has changed
        super.onResume();
        if (mLocation != null & !Utility.getPreferredLocation(getActivity()).equals(mLocation)) {
            // updateWeather() does not work here because our URI has not changed
            // so we need to actually reset our loader -> use LoaderManager
            getLoaderManager().restartLoader(FORECAST_LOADER, null, this);
        }
        Log.d(LOG_TAG, "onResume");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(LOG_TAG, "onDestroy");
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // This is called when a new Loader needs to be created.  This fragment only uses one loader,
        // so we don't care about checking the id

        // to only show current and future dates, get the String representation for today,
        // and filter the query to return weather only for dates after or including today.
        // Only return data after today.
        String startDate = WeatherContract.getDbDateString(new Date());

        // Sort order: Ascending, by date.
        String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATETEXT + " ASC";

        // We've been using an instance variable mLocation to save the preferred location we get when
        // in onCreateLoader.  This was so we could use this in onResume()
        mLocation = Utility.getPreferredLocation(getActivity());
        Uri weatherForLocationUri = WeatherContract.WeatherEntry.buildWeatherLocationWithStartDate(
                mLocation, startDate);

        // Now create and return a CursorLoader that will take care of creating a Cursor for the
        // data being displayed.
        return new CursorLoader(
                getActivity(),
                weatherForLocationUri,
                FORECAST_COLUMNS,
                null,
                null,
                sortOrder
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor data) {
        mForecastAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mForecastAdapter.swapCursor(null);
    }
}