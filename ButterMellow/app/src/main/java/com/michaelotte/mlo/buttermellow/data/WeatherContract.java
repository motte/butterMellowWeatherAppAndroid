package com.michaelotte.mlo.buttermellow.data;

import android.provider.BaseColumns;

/**
 * Created by michael on 10/20/14.
 *
 * This is a contract class
 * A db contract is an agreement between db and views on how all the db info are stored
 */
public class WeatherContract {
    /* Inner class that defines the table contents of the weather table */
    public static final class LocationEntry implements BaseColumns {
        // Unique table name
        public static final String TABLE_NAME = "location";

        // The location setting string is what will be sent to OWM as the location query
        public static final String COLUMN_LOCATION_SETTING = "location_setting";

        // Human readable location string, provided by the API.
        public static final String COLUMN_CITY_NAME = "city_name";

        // Lat and Lon for the city to pin point in map intent
        public static final String COLUMN_COORD_LAT = "coord_lat";
        public static final String COLUMN_COORD_LON = "coord_lon";
    }
    /* Inner class that defines the table contents of the weather table */
    public static final class WeatherEntry implements BaseColumns {
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
        public static final String COLUMN_DEGREES = "units";


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
