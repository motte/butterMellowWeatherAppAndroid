package com.michaelotte.mlo.buttermellow;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.test.AndroidTestCase;
import android.util.Log;

import com.michaelotte.mlo.buttermellow.data.WeatherContract.LocationEntry;
import com.michaelotte.mlo.buttermellow.data.WeatherContract.WeatherEntry;
import com.michaelotte.mlo.buttermellow.data.WeatherDbHelper;

import java.util.Map;
import java.util.Set;

/**
 * Created by michael on 10/20/14.
 */
public class TestProvider extends AndroidTestCase {
    private static String LOG_TAG = TestProvider.class.getSimpleName();
    public String TEST_CITY_NAME = "Philadelphia";

    public void testDeleteDb() throws Throwable {
        // Delete/clean database first
        mContext.deleteDatabase(WeatherDbHelper.DATABASE_NAME);
    }

    // Make sure that everything in our content matches our insert
    static public void validateCursor(ContentValues expectedValues, Cursor valueCursor) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();

        //
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
    }

    public void testGetType() {
        // content://com.michaelotte.mlo.buttermellow.app/weather/
        String type = mContext.getContentResolver().getType(WeatherEntry.CONTENT_URI);
        // vnd.android.cursor.dir/com.michaelotte.mlo.buttermellow.app/weather
        assertEquals(WeatherEntry.CONTENT_TYPE, type);

        String testLocation = "4560349";
        // content://com.michaelotte.mlo.buttermellow.app/weather/4560349
        type = mContext.getContentResolver().getType(WeatherEntry.buildWeatherLocationUri(testLocation));
        // vnd.android.cursor.dir/com.michaelotte.mlo.buttermellow.app/weather
        assertEquals(WeatherEntry.CONTENT_TYPE, type);

        String testDate = "20141021";
        // content://com.michaelotte.mlo.buttermellow.app/4560349/20141021
        type = mContext.getContentResolver().getType(WeatherEntry.buildWeatherLocationWithStartDate(testLocation, testDate));
        assertEquals(WeatherEntry.CONTENT_TYPE, type);

        // content://com.michaelotte.mlo.buttermellow.app/location/
        type = mContext.getContentResolver().getType(LocationEntry.CONTENT_URI);
        // vnd.android.cursor.dir/com.michaelotte.mlo.buttermellow.app/location
        assertEquals(LocationEntry.CONTENT_TYPE, type);

        // content://com.michaelotte.mlo.buttermellow.app/location/1
        type = mContext.getContentResolver().getType(LocationEntry.buildLocationUri(1L));
        // vnd.android.cursor.item/com.michaelotte.mlo.buttermellow.app/location
        assertEquals(LocationEntry.CONTENT_ITEM_TYPE, type);
    }

    ContentValues getTestLocationContentValues() {
        // Test data we're going to insert into the DB
        String testLocationSetting = "4560349";
        double testLatitude = 39.9523;
        double testLongitude = -75.1625;

        // Create a new map of values where column names are the keys
        ContentValues values = new ContentValues();
        values.put(LocationEntry.COLUMN_CITY_NAME, TEST_CITY_NAME);
        values.put(LocationEntry.COLUMN_LOCATION_SETTING, testLocationSetting);
        values.put(LocationEntry.COLUMN_COORD_LAT, testLatitude);
        values.put(LocationEntry.COLUMN_COORD_LON, testLongitude);
        return values;
    }

    ContentValues getTestWeatherContentValues(long locationRowId) {
        // Now that we have location, add some weather!
        String testDate = "20141021";
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
