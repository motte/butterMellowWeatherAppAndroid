package com.michaelotte.mlo.buttermellow;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.util.Log;

import com.michaelotte.mlo.buttermellow.data.WeatherContract.WeatherEntry;
import com.michaelotte.mlo.buttermellow.data.WeatherContract.LocationEntry;
import com.michaelotte.mlo.buttermellow.data.WeatherDbHelper;

/**
 * Created by michael on 10/20/14.
 */
public class TestDb extends AndroidTestCase {
    private static String LOG_TAG = TestDb.class.getSimpleName();

    public void testCreateDb() throws Throwable {
        // Delete/clean database first
        mContext.deleteDatabase(WeatherDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new WeatherDbHelper(this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());
        db.close();
    }

    public void testInsertReadDb() {
        /**
         * Test if location and weather data can be added to the location and weather tables
         */
        // Test data we're going to insert into the DB
        String testName = "Philadelphia";
        String testLocationSetting = "4560349";
        double testLatitude = 39.9523;
        double testLongitude = -75.1625;

        //If there's an error in the massive SQL table
        // errors will be thrown where you try to get a value
        WeatherDbHelper dbHelper =
                new WeatherDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Create a new map of values where column names are the keys
        ContentValues values = new ContentValues();
        values.put(LocationEntry.COLUMN_CITY_NAME, testName);
        values.put(LocationEntry.COLUMN_LOCATION_SETTING, testLocationSetting);
        values.put(LocationEntry.COLUMN_COORD_LAT, testLatitude);
        values.put(LocationEntry.COLUMN_COORD_LON, testLongitude);

        long locationRowId;
        locationRowId = db.insert(LocationEntry.TABLE_NAME, null, values);

        // verify we got a row back
        assertTrue(locationRowId != -1);
        Log.d(LOG_TAG, "New row id: " + locationRowId);

        // Specify which columns you want
        String[] columns = {
                LocationEntry._ID,
                LocationEntry.COLUMN_LOCATION_SETTING,
                LocationEntry.COLUMN_CITY_NAME,
                LocationEntry.COLUMN_COORD_LAT,
                LocationEntry.COLUMN_COORD_LON,
        };

        // A cursor is your primary interface to the query results
        Cursor cursor = db.query(
                LocationEntry.TABLE_NAME, // Table to query
                columns, // the desired columns
                null, // columns for the WHERE clause
                null, // values for the WHERE clause
                null, // columns to GROUP by
                null, // columns to FILTER by row groups
                null // sort ORDER
        );

        if (cursor.moveToFirst()) {
            // Get the value in each column by finding the appropriate column index
            int locationIndex = cursor.getColumnIndex(LocationEntry.COLUMN_LOCATION_SETTING); // get index
            String location = cursor.getString(locationIndex); // get value by index

            int nameIndex = cursor.getColumnIndex((LocationEntry.COLUMN_CITY_NAME));
            String name = cursor.getString(nameIndex);

            int latIndex = cursor.getColumnIndex((LocationEntry.COLUMN_COORD_LAT));
            double latitude = cursor.getDouble(latIndex);

            int lonIndex = cursor.getColumnIndex((LocationEntry.COLUMN_COORD_LON));
            double longitude = cursor.getDouble(lonIndex);

            // Hooray, data was returned!  Assert that it's the right data, and the database
            // creation code is working as intended.
            assertEquals(testName, name);
            assertEquals(testLocationSetting, location);
            assertEquals(testLatitude, latitude);
            assertEquals(testLongitude, longitude);

            // Now that we have location, add some weather!
            String testDate = "20141020";
            double testDegrees = 1.2;
            double testHumidity = 1.3;
            double testPressure = 1.4;
            double testMaxTemp = 80;
            double testMinTemp = 65;
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

            long weatherRowId;
            weatherRowId = db.insert(WeatherEntry.TABLE_NAME, null, weatherValues);

            assertTrue(weatherRowId != -1);
            Log.d(LOG_TAG, "TestDb - New weather row ID: " + weatherRowId);

            String[] weatherColumns = {
                    WeatherEntry._ID,
                    WeatherEntry.COLUMN_LOC_KEY,
                    WeatherEntry.COLUMN_DATETEXT,
                    WeatherEntry.COLUMN_DEGREES,
                    WeatherEntry.COLUMN_HUMIDITY,
                    WeatherEntry.COLUMN_PRESSURE,
                    WeatherEntry.COLUMN_MAX_TEMP,
                    WeatherEntry.COLUMN_MIN_TEMP,
                    WeatherEntry.COLUMN_SHORT_DESC,
                    WeatherEntry.COLUMN_WIND_SPEED,
                    WeatherEntry.COLUMN_WEATHER_ID,
            };

            Cursor wCursor = db.query(
                    WeatherEntry.TABLE_NAME,
                    weatherColumns,
                    null,
                    null,
                    null,
                    null,
                    null
            );

            if (wCursor.moveToFirst()) {

                int dateIndex = wCursor.getColumnIndex(WeatherEntry.COLUMN_DATETEXT);
                String date = wCursor.getString(dateIndex);

                int degreesIndex = wCursor.getColumnIndex(WeatherEntry.COLUMN_DEGREES);
                String degrees = wCursor.getString(degreesIndex);

                int humidityIndex = wCursor.getColumnIndex(WeatherEntry.COLUMN_HUMIDITY);
                String humidity = wCursor.getString(humidityIndex);

                int pressureIndex = wCursor.getColumnIndex(WeatherEntry.COLUMN_PRESSURE);
                String pressure = wCursor.getString(pressureIndex);

                int maxTempIndex = wCursor.getColumnIndex(WeatherEntry.COLUMN_MAX_TEMP);
                String maxTemp = wCursor.getString(maxTempIndex);

                int minTempIndex = wCursor.getColumnIndex(WeatherEntry.COLUMN_MIN_TEMP);
                String minTemp = wCursor.getString(minTempIndex);

                int shortDescIndex = wCursor.getColumnIndex(WeatherEntry.COLUMN_SHORT_DESC);
                String shortDesc = wCursor.getString(shortDescIndex);

                int windSpeedIndex = wCursor.getColumnIndex(WeatherEntry.COLUMN_WIND_SPEED);
                String windSpeed = wCursor.getString(windSpeedIndex);

                int weatherIdIndex = wCursor.getColumnIndex(WeatherEntry.COLUMN_WEATHER_ID);
                String weatherId = wCursor.getString(weatherIdIndex);

                assertEquals(testDate, date);
                assertEquals(testDegrees, degrees);
                assertEquals(testHumidity, humidity);
                assertEquals(testPressure, pressure);
                assertEquals(testMaxTemp, maxTemp);
                assertEquals(testMinTemp, minTemp);
                assertEquals(testShortDesc, shortDesc);
                assertEquals(testWindSpeed, windSpeed);
                assertEquals(testWeatherId, weatherId);
            } else {
                fail("No weather values returned");
            }
        } else {
            // That's weird.  It should work.
            fail("No values returned :(");
        }
    }
}
