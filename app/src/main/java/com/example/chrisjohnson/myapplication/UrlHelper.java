package com.example.chrisjohnson.myapplication;

import android.util.Log;

/**
 * Created by chrisjohnson on 11/12/14.
 */
public class UrlHelper {
    private final static String DEBUG_TAG = "UrlHelper";

    public String NormaliseUrl( String urlString) {
        Log.d(DEBUG_TAG, "Url passed in: " + urlString);
        if ( urlString.matches("^(https?:\\/\\/).+")) {
            Log.d(DEBUG_TAG, "Url starts with http(s) - nothing to do");
        } else {
            Log.d(DEBUG_TAG, "no protocol passed in - assuming http");
            urlString = "http://" + urlString;
        }
        Log.d(DEBUG_TAG, "Url returned: " + urlString);
        return urlString;
    }
}
