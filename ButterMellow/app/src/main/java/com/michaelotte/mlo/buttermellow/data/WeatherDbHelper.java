package com.michaelotte.mlo.buttermellow.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

// Import the db contracts
import com.michaelotte.mlo.buttermellow.data.WeatherContract.LocationEntry;
import com.michaelotte.mlo.buttermellow.data.WeatherContract.WeatherEntry;

/**
 * Created by michael on 10/20/14.
 */
public class WeatherDbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you MUST increment the DATABASE_VERSION
    private static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "weather.db";

    public WeatherDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a table to hold locations.  A location consists of a postal code and a human
        // readable name (e.g. "Santa Cruz").
        final String SQL_CREATE_LOCATION_TABLE = "CREATE TABLE " + LocationEntry.TABLE_NAME + " (" +
                // There is a FK to the Weather table COLUMN_LOC_KEY
                LocationEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                LocationEntry.COLUMN_CITY_NAME + " TEXT NOT NULL," +
                // DOUBLE is also a float that has double precision - estimate of a FLOAT
                LocationEntry.COLUMN_COORD_LAT + " DOUBLE NOT NULL," +
                LocationEntry.COLUMN_COORD_LON + " DOUBLE NOT NULL," +
                LocationEntry.COLUMN_LOCATION_SETTING + " TEXT NOT NULL," +
                " UNIQUE (" + LocationEntry.COLUMN_LOCATION_SETTING + ") ON CONFLICT REPLACE);";

        final String SQL_CREATE_WEATHER_TABLE = "CREATE TABLE " + WeatherEntry.TABLE_NAME + " (" +
                WeatherEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                // makes a unique key when inserted but it will fill gaps in PKs if there were deleted rows

                // constraints on fields used.  Mainly "NOT NULL"
                // This allows the db to do most of our parameter validation for us
                // But the draw back is there are no errors if it fails, making debugging harder

                // the ID of the location entry is associated with this weather data LOC_KEY
                WeatherEntry.COLUMN_LOC_KEY + " INTEGER NOT NULL," +
                // unix time
                WeatherEntry.COLUMN_DATETEXT + " TEXT NOT NULL," +
                WeatherEntry.COLUMN_SHORT_DESC + " TEXT NOT NULL," +
                WeatherEntry.COLUMN_LONG_DESC + " TEXT NOT NULL," +
                WeatherEntry.COLUMN_WEATHER_ID + " INTEGER NOT NULL," +

                // REAL is float, could have used INTEGER, which is arguably faster but REAL is simple for us
                WeatherEntry.COLUMN_MIN_TEMP + " REAL NOT NULL," +
                WeatherEntry.COLUMN_MAX_TEMP + " REAL NOT NULL," +

                WeatherEntry.COLUMN_HUMIDITY + " REAL NOT NULL," +
                WeatherEntry.COLUMN_PRESSURE + " REAL NOT NULL," +
                WeatherEntry.COLUMN_WIND_SPEED + " REAL NOT NULL," +
                WeatherEntry.COLUMN_DEGREES + " REAL NOT NULL," +

                // Set up the location column as a foreign key to location table
                // This forces the weather object to correspond to the proper location object
                // and they can't be added or deleted separate of each other.
                " FOREIGN KEY (" + WeatherEntry.COLUMN_LOC_KEY + ") REFERENCES " +
                LocationEntry.TABLE_NAME + " (" + LocationEntry._ID + "), " +

                // Ensure one unique daily forecast for a specific location
                // To assure the application has just one weather entry per day
                // per location, it's created a UNIQUE constraint with REPLACE strategy
                // New one replaces the old
                " UNIQUE (" + WeatherEntry.COLUMN_DATETEXT + ", " +
                WeatherEntry.COLUMN_LOC_KEY + ") ON CONFLICT REPLACE);";

        // Actually create the tables with SQLiteDatabase db.execSQL()
        db.execSQL(SQL_CREATE_LOCATION_TABLE);
        db.execSQL(SQL_CREATE_WEATHER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop the tables for a new version because these tables are really a cache of location and
        // weather forecasts.  If it were user tables, I would do something like ALTER TABLE IF EXISTS
        db.execSQL("DROP TABLE IF EXISTS " + LocationEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + WeatherEntry.TABLE_NAME);
        // only fires if the version of the db is new
        onCreate(db);
    }
}
