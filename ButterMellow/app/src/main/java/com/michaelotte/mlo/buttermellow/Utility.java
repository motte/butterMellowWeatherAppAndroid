package com.michaelotte.mlo.buttermellow;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.util.Log;

import com.michaelotte.mlo.buttermellow.data.WeatherContract;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by michael on 10/6/14.
 */
public class Utility {
    private final String LOG_TAG = Utility.class.getSimpleName();
    /**
     * The date/time conversion method
     * Converts unix timestamp in seconds to milliseconds then converted to date format.
     */
    private String getReadableDateString(long time) {
        Date date = new Date(time * 1000);
        SimpleDateFormat format = new SimpleDateFormat("E, MMM d");
        return format.format(date).toString();
    }

    /**
     * Format the highs and lows double
     * @param high
     * @param low
     * @return
     */
    private String formatHighLows(double high, double low) {
        long roundedHigh = Math.round(high);
        long roundedLow = Math.round(low);

        String highLowStr = roundedHigh + "/" + roundedLow;
        return highLowStr;
    }

    public String getCityDataFromJson(String forecastJsonStr)
            throws JSONException {
        final String OWM_CITY = "city";
        final String OWM_COORD = "coord";
        final String OWM_LAT = "lat";
        final String OWM_LON = "lon";

        JSONObject forecastJson = new JSONObject(forecastJsonStr);

        JSONObject weatherCityObject = forecastJson.getJSONObject(OWM_CITY);
        JSONObject weatherCoordObject = weatherCityObject.getJSONObject(OWM_COORD);
        String latString = weatherCoordObject.getString(OWM_LAT);
        String lonString = weatherCoordObject.getString(OWM_LON);

        return latString + " " + lonString;
    }

    public static String getPreferredLocation(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_location_key),
                context.getString(R.string.pref_default_location));
    }
}
