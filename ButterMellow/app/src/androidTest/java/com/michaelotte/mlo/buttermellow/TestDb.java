package com.michaelotte.mlo.buttermellow;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.util.Log;

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
        // Test data we're going to insert into the DB
        String testName = "Philadelphia";
        String testLocationSetting = "4560349";
        double testLatitude = 39.9523;
        double testLongitude = -75.1625;

        //If there's an error in the massive SQL table
        // errors will be thrown here wh you try to get a value
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
            ContentValues weatherValues = new ContentValues();
        } else {
            // That's weird.  It should work.
            fail("No values returned :(");
        }
    }
}
