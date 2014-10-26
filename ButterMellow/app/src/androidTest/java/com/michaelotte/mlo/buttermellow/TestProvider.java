package com.michaelotte.mlo.buttermellow;

import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.test.AndroidTestCase;
import android.util.Log;

import com.michaelotte.mlo.buttermellow.data.WeatherContract.LocationEntry;
import com.michaelotte.mlo.buttermellow.data.WeatherContract.WeatherEntry;
import com.michaelotte.mlo.buttermellow.data.WeatherDbHelper;

import junit.framework.Test;

import java.util.Iterator;
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

    public void testInsertReadProvider() {
        /**
         * Test if location and weather data can be added to the location and weather tables
         */

        //If there's an error in the massive SQL table
        // errors will be thrown where you try to get a value
        ContentValues testValues = TestDb.getTestLocationContentValues();

        Uri locationUri = mContext.getContentResolver().insert(LocationEntry.CONTENT_URI, testValues);
        long locationRowId = ContentUris.parseId(locationUri);

        // verify we got a row back
        assertTrue(locationRowId != -1);
        Log.d(LOG_TAG, "New row id: " + locationRowId);

        // A cursor is your primary interface to the query results
        Cursor cursor = mContext.getContentResolver().query(
                LocationEntry.CONTENT_URI,// uri name
                null, // leaving "columns" null just returns all the columns
                null, // cols for "where" clause
                null, // values for "where" clause
                null // sort order
        );

        TestDb.validateCursor(testValues, cursor);

        cursor = mContext.getContentResolver().query(
                LocationEntry.buildLocationUri(locationRowId),
                null,
                null,
                null,
                null
        );

        TestDb.validateCursor(testValues, cursor);

        ContentValues weatherValues = TestDb.getTestWeatherContentValues(locationRowId);

        Uri weatherInsertUri = mContext.getContentResolver().insert(WeatherEntry.CONTENT_URI, weatherValues);

        assertTrue(weatherInsertUri != null);
        Log.d(LOG_TAG, "TestDb - New weather URI ID: " + weatherInsertUri);

        // get the contentresolver and queries the content_uri
        Cursor wCursor = mContext.getContentResolver().query(
                WeatherEntry.CONTENT_URI, // uri name
                null, // leaving "columns" null just returns all the columns
                null, // cols for "where" clause
                null, // values for "where" clause
                null // sort order
        );

        TestDb.validateCursor(weatherValues, wCursor);

        // Add the Location values in with the weather data so that we can make
        // sure that the join worked and we actually get all the values back
        addAllContentValues(weatherValues, testValues);

        // Get the joined Weather and Location data
        wCursor = mContext.getContentResolver().query(
                WeatherEntry.buildWeatherLocationUri(TestDb.TEST_LOCATION_SETTING),
                null,
                null,
                null,
                null
        );
        TestDb.validateCursor(weatherValues, wCursor);

        // Get the joined Weather and Location data with a start date
        wCursor = mContext.getContentResolver().query(
                WeatherEntry.buildWeatherLocationWithStartDate(
                        TestDb.TEST_LOCATION_SETTING, TestDb.TEST_DATE),
                null,
                null,
                null,
                null
        );

        TestDb.validateCursor(weatherValues, wCursor);
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

    // The target api annotation is needed for the call to keySet -- we wouldn't want
    // to use this in our app, but in a test it's fine to assume a higher target.
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    void addAllContentValues(ContentValues destination, ContentValues source) {
        for (String key : source.keySet()) {
            destination.put(key, source.getAsString(key));
        }
    }

}
