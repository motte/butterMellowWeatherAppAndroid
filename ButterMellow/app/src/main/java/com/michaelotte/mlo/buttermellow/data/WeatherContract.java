package com.michaelotte.mlo.buttermellow.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by michael on 10/20/14.
 *
 * This is a contract class
 * A db contract is an agreement between db and views on how all the db info are stored
 */
public class WeatherContract {
    // The "Content authority" is a name for the entire content provider.
    // A convenient string to use for the content authority is the package name for the app,
    // which is guaranteed to be unique on the device.
    public static final String CONTENT_AUTHORITY = "com.michaelotte.mlo.buttermellow.app";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact the
    // content provider
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Possible paths (appended to base content URI for possible URI's)
    // Essentially our tables
    public static final String PATH_WEATHER = "weather";
    public static final String PATH_LOCATION = "location";

    // Format used for storing dates in the db.  Also used for converting those strings back into
    // date objects for comparison/processing
    public static final String DATE_FORMAT = "yyyyMMdd";

    /**
     * Converts Date class to a string representation, used for easy comparison and database lookup.
     * @param date The input date
     * @return a DB-friendly representation of the date, using the format defined in DATE_FORMAT.
     */
    public static String getDbDateString(Date date){
        // Because the API returns a unix timestamp (measured in seconds),
        // it must be converted to milliseconds in order to be converted to valid date.
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        return sdf.format(date);
    }

    /**
     * Converts a dateText to a long Unix time representation
     * @param dateText the input date string
     * @return the Date object
     */
    public static Date getDateFromDb(String dateText) {
        SimpleDateFormat dbDateFormat = new SimpleDateFormat(DATE_FORMAT);
        try {
            return dbDateFormat.parse(dateText);
        } catch ( ParseException e ) {
            e.printStackTrace();
            return null;
        }
    }

    /* Inner class that defines the table contents of the weather table */
    public static final class LocationEntry implements BaseColumns {
        // base location for the location table
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_LOCATION).build();

        // values to the location and weather contracts
        // MIMETYPE prefixes to tell to return dir/list or single item
        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_LOCATION;

        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_LOCATION;

        // Unique table name
        public static final String TABLE_NAME = "location";

        // The location setting string is what will be sent to OWM as the location query
        public static final String COLUMN_LOCATION_SETTING = "location_setting";

        // Human readable location string, provided by the API.
        public static final String COLUMN_CITY_NAME = "city_name";

        // Lat and Lon for the city to pin point in map intent
        public static final String COLUMN_COORD_LAT = "coord_lat";
        public static final String COLUMN_COORD_LON = "coord_lon";

        // this queries the Location table by id
        public static Uri buildLocationUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
    /* Inner class that defines the table contents of the weather table */
    public static final class WeatherEntry implements BaseColumns {
        // base location/path for weather table
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_WEATHER).build();

        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_WEATHER;

        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_WEATHER;

        // Unique table name
        public static final String TABLE_NAME = "weather";

        // Foreign key for the location table
        public static final String COLUMN_LOC_KEY = "location_id";
        // Date stored as Text with format yyyy-MM-dd
        public static final String COLUMN_DATETEXT = "date";
        // Weather id as returned by API to identify the icon to be used
        public static final String COLUMN_WEATHER_ID = "weather_id";

        // Short description of the weather as provided by the API
        // e.g. "clear"
        public static final String COLUMN_SHORT_DESC = "short_desc";
        // Long description of the weather as provided by the API
        public static final String COLUMN_LONG_DESC = "long_desc";

        // Min and max temperatures for the day (stored as floats) in metric
        public static final String COLUMN_MIN_TEMP = "min";
        public static final String COLUMN_MAX_TEMP = "max";

        // Humidity is stored as a float representing percentage
        public static final String COLUMN_HUMIDITY = "humidity";
        public static final String COLUMN_PRESSURE = "pressure";
        public static final String COLUMN_WIND_SPEED = "wind_speed";
        // Degrees are meteorological degrees (e.g. 0 is north 180 is south). Stored as float
        public static final String COLUMN_DEGREES = "degrees";

        /*
        URI Builders and Decoder functions - good for keeping the actual URI encoding in Contract
        - These functions build the URI to retrieve different information from sqlite
         */
        // If using integer primary key in weather table only (common)
        public static Uri buildWeatherUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildWeatherLocationUri(String locationSetting) {
            return CONTENT_URI.buildUpon().appendPath(locationSetting).build();
        }

        // builds two part URI with location and date segments
        public static Uri buildWeatherLocationWithStartDate(
                String locationSetting, String startDate) {
            return CONTENT_URI.buildUpon().appendPath(locationSetting)
                    .appendQueryParameter(COLUMN_DATETEXT, startDate).build();
        }

        public static Uri buildWeatherLocationWithDate(String locationSetting, String date) {
            return CONTENT_URI.buildUpon().appendPath(locationSetting).appendPath(date).build();
        }

        /*
        These are helper functions to decode the URI structure to also hide the
        URI encoding/structure in Contract
         */
        public static String getLocationSettingFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

        public static String getDateFromUri(Uri uri) {
            return uri.getPathSegments().get(2);
        }

        public static String getStartDateFromUri(Uri uri) {
            return uri.getQueryParameter(COLUMN_DATETEXT);
        }

        /**
         * The unique ID for a row
         * <P>Type: Integer (long)</P>
         */
        public static final String _ID = "_id";

        /**
         * The count of rows in a directory
         * <P>Type: Integer</P>
         */
        public static final String _COUNT = "_count";
    }
}
