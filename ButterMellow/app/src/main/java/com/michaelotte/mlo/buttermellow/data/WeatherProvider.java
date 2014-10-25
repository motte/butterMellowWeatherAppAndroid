package com.michaelotte.mlo.buttermellow.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

/**
 * Created by michael on 10/22/14.
 */
public class WeatherProvider extends ContentProvider {
    // 5 different integer constants for types of uris
    // content://com.michaelotte.mlo.buttermellow.app/weather
    private static final int WEATHER = 100;
    // content://com.michaelotte.mlo.buttermellow.app/weather/[LOCATION_QUERY]
    private static final int WEATHER_WITH_LOCATION = 101;
    // content://com.michaelotte.mlo.buttermellow.app/weather/[LOCATION_QUERY]/[DATE]
    private static final int WEATHER_WITH_LOCATION_AND_DATE = 102;
    // content://com.michaelotte.mlo.buttermellow.app/location
    private static final int LOCATION = 300;
    // content://com.michaelotte.mlo.buttermellow.app/location/[LOCATION_ID]
    private static final int LOCATION_ID = 301;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    // instance variable for database helper
    private WeatherDbHelper mOpenHelper;

    private static UriMatcher buildUriMatcher() {
        // Could use regex instead of UriMatcher
        // All paths added to UriMatcher has a corresponding code to return when a match is found.
        // The code passed into the constructor represents the code to return for the root URI.
        // It's common to use NO_MATCH as the code for this case.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = WeatherContract.CONTENT_AUTHORITY;

        // For each type of URI, you want to add/create a corresponding code.
        matcher.addURI(authority, WeatherContract.PATH_WEATHER, WEATHER);
        // We're matching wildcard strings here because that's how they were stored in the db
        matcher.addURI(authority, WeatherContract.PATH_WEATHER + "/*", WEATHER_WITH_LOCATION);
        matcher.addURI(authority, WeatherContract.PATH_WEATHER + "/*/*", WEATHER_WITH_LOCATION_AND_DATE);

        // For each type of URI, you want to add/create a corresponding code.
        matcher.addURI(authority, WeatherContract.PATH_LOCATION, LOCATION);
        // ID in db is always an int so /#
        matcher.addURI(authority, WeatherContract.PATH_LOCATION + "/#", LOCATION_ID);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        /**
         * onCreate of this contentprovider
         */
        mOpenHelper = new WeatherDbHelper(getContext());
        // return true to show that content provider initialized successfully
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return null;
    }

    @Override
    public String getType(Uri uri) {
        /***
         * getType method returns mimetype of the data associated with the given uri
         */
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case WEATHER_WITH_LOCATION_AND_DATE:
                return WeatherContract.WeatherEntry.CONTENT_ITEM_TYPE;
            case WEATHER_WITH_LOCATION:
                return WeatherContract.WeatherEntry.CONTENT_TYPE;
            case WEATHER:
                return WeatherContract.WeatherEntry.CONTENT_TYPE;
            case LOCATION:
                return WeatherContract.LocationEntry.CONTENT_TYPE;
            case LOCATION_ID:
                return WeatherContract.LocationEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
