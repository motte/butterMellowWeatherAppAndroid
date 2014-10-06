package com.michaelotte.mlo.buttermellow;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.os.Build;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;


/**
 * Runs when app is launched
 */
public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     * fragment is a modular container
     */
    public static class PlaceholderFragment extends Fragment {

        private ArrayAdapter<String> dayForecastAdapter;

        public PlaceholderFragment() {
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
            dayForecastAdapter =
                    new ArrayAdapter<String>(
                            // The current context (this fragment's parent activity that are global)
                            getActivity(),
                            R.layout.list_item_forecast,
                            R.id.list_item_forecast_textview,
                            // Forecast data in an array
                            weekForecast);

            ListView listView = (ListView) rootView.findViewById(
                    R.id.listview_forecast);
            listView.setAdapter(dayForecastAdapter);


            return rootView;
        }
    }
}
