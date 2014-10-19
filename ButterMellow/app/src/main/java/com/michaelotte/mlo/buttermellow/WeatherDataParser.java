package com.michaelotte.mlo.buttermellow;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by michael on 10/6/14.
 */
public class WeatherDataParser {
    private final String LOG_TAG = WeatherDataParser.class.getSimpleName();
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

    /**
     * Given a string of the form returned by the api call:
     * http://api.openweathermap.org/data/2.5/forecast/daily?id=12934&mode=json&units=metric&cnt=7
     * retrieve the maximum temperature for the day indicated by dayIndex
     *
     * @param forecastJsonStr
     * @param numDays
     * @return resultStrs (array of weather strings)
     * @throws JSONException
     */
    public String[] getWeatherDataFromJson(String forecastJsonStr, int numDays)
            throws JSONException {
//        Open Weather Map keys
        final String OWM_LIST = "list";
        final String OWM_WEATHER = "weather";
        final String OWM_TEMPERATURE = "temp";
        final String OWM_MAX = "max";
        final String OWM_MIN = "min";
        final String OWM_DATETIME = "dt";
        final String OWM_MAIN = "main";
        final String OWM_DESCRIPTION = "description";

        JSONObject forecastJson = new JSONObject(forecastJsonStr);
        JSONArray weatherListArray = forecastJson.getJSONArray(OWM_LIST);

        String[] resultStrs = new String[numDays];
        for(int i = 0; i < weatherListArray.length(); i++) {
            String day;
            String description;
            String highAndLow;

            JSONObject dayForecast = weatherListArray.getJSONObject(i);

            long dateTime = dayForecast.getLong(OWM_DATETIME);
            day = getReadableDateString(dateTime);

            JSONArray weatherArray = dayForecast.getJSONArray(OWM_WEATHER);
            JSONObject weatherMain = weatherArray.getJSONObject(0);
            description = weatherMain.getString(OWM_DESCRIPTION);

            JSONObject temperatureObject = dayForecast.getJSONObject(OWM_TEMPERATURE);
            double high = temperatureObject.getDouble(OWM_MAX);
            double low = temperatureObject.getDouble(OWM_MIN);

            highAndLow = formatHighLows(high, low);
            resultStrs[i] = day + " - " + description + " - " + highAndLow;
        }

        for(String s : resultStrs) {
            Log.v(LOG_TAG, "Forecast entry: " + s);
        }

        return resultStrs;

    }
}
