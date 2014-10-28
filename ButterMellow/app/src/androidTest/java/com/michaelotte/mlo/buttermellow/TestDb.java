package com.michaelotte.mlo.buttermellow;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.util.Log;

import java.util.Map;
import java.util.Set;

import com.michaelotte.mlo.buttermellow.data.WeatherContract.WeatherEntry;
import com.michaelotte.mlo.buttermellow.data.WeatherContract.LocationEntry;
import com.michaelotte.mlo.buttermellow.data.WeatherDbHelper;

/**
 * Created by michael on 10/20/14.
 */
public class TestDb extends AndroidTestCase {
    private static String LOG_TAG = TestDb.class.getSimpleName();
    public static final String TEST_LOCATION_SETTING = "4560349";
    public static final String TEST_DATE = "20141021";

    public void testCreateDb() throws Throwable {
        // Delete/clean database first
        mContext.deleteDatabase(WeatherDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new WeatherDbHelper(this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());
        db.close();
    }

    // Make sure that everything in our content matches our insert
    static public void validateCursor(ContentValues expectedValues, Cursor valueCursor) {
        assertTrue(valueCursor.moveToFirst());

        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry: valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse(idx == -1);
            String expectedValue = entry.getValue().toString();
            // valueCursor.getString(idx) on a float type converts any float-integer
            // (e.g. 60.0, 80.0 to 60, 80 respectively) to integer, so all float to the valueCursor
            // will always fail for float-integers.
            assertEquals(expectedValue, valueCursor.getString(idx));
        }
        valueCursor.close();
    }

    static public ContentValues getTestLocationContentValues() {
        String testCityName = "Philadelphia";
        // Test data we're going to insert into the DB
        double testLatitude = 39.9523;
        double testLongitude = -75.1625;

        // Create a new map of values where column names are the keys
        ContentValues values = new ContentValues();
        values.put(LocationEntry.COLUMN_CITY_NAME, testCityName);
        values.put(LocationEntry.COLUMN_LOCATION_SETTING, TEST_LOCATION_SETTING);
        values.put(LocationEntry.COLUMN_COORD_LAT, testLatitude);
        values.put(LocationEntry.COLUMN_COORD_LON, testLongitude);
        return values;
    }

    static public ContentValues getTestWeatherContentValues(long locationRowId) {
        // Now that we have location, add some weather!
        String testDate = TEST_DATE;
        double testDegrees = 1.2;
        double testHumidity = 1.3;
        double testPressure = 1.4;
        double testMaxTemp = 80.2;
        double testMinTemp = 65.1;
        String testShortDesc = "Sky is falling";
        String testLongDesc = "Sky is falling so everyone should recycle";
        double testWindSpeed = 5.5;
        int testWeatherId = 321;

        // Make a new map of values to add to db
        ContentValues weatherValues = new ContentValues();
        weatherValues.put(WeatherEntry.COLUMN_LOC_KEY, locationRowId);
        weatherValues.put(WeatherEntry.COLUMN_DATETEXT, testDate);
        weatherValues.put(WeatherEntry.COLUMN_DEGREES, testDegrees);
        weatherValues.put(WeatherEntry.COLUMN_HUMIDITY, testHumidity);
        weatherValues.put(WeatherEntry.COLUMN_PRESSURE, testPressure);
        weatherValues.put(WeatherEntry.COLUMN_MAX_TEMP, testMaxTemp);
        weatherValues.put(WeatherEntry.COLUMN_MIN_TEMP, testMinTemp);
        weatherValues.put(WeatherEntry.COLUMN_SHORT_DESC, testShortDesc);
        weatherValues.put(WeatherEntry.COLUMN_LONG_DESC, testLongDesc);
        weatherValues.put(WeatherEntry.COLUMN_WIND_SPEED, testWindSpeed);
        weatherValues.put(WeatherEntry.COLUMN_WEATHER_ID, testWeatherId);
        return weatherValues;
    }

    public void testInsertReadDb() {
        /**
         * Test if location and weather data can be added to the location and weather tables
         */

        //If there's an error in the massive SQL table
        // errors will be thrown where you try to get a value
        WeatherDbHelper dbHelper =
                new WeatherDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues locationValues = getTestLocationContentValues();

        long locationRowId;
        locationRowId = db.insert(LocationEntry.TABLE_NAME, null, locationValues);

        // verify we got a row back
        assertTrue(locationRowId != -1);
        Log.d(LOG_TAG, "New row id: " + locationRowId);

        // A cursor is your primary interface to the query results
        Cursor locCursor = db.query(
                LocationEntry.TABLE_NAME, // Table to query
                null, // the desired columns
                null, // columns for the WHERE clause
                null, // values for the WHERE clause
                null, // columns to GROUP by
                null, // columns to FILTER by row groups
                null // sort ORDER
        );

        if (locCursor.moveToFirst()) {
            validateCursor(locationValues, locCursor);

            ContentValues weatherValues = getTestWeatherContentValues(locationRowId);

            long weatherRowId;
            weatherRowId = db.insert(WeatherEntry.TABLE_NAME, null, weatherValues);

            assertTrue(weatherRowId != -1);
            Log.d(LOG_TAG, "TestDb - New weather row ID: " + weatherRowId);

            Cursor wCursor = db.query(
                    WeatherEntry.TABLE_NAME,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null
            );

            if (wCursor.moveToFirst()) {
                validateCursor(weatherValues, wCursor);
            } else {
                fail("No weather values returned");
            }
        } else {
            // That's weird.  It should work.
            fail("No values returned :(");
        }
    }
}
