package com.example.chrisjohnson.myapplication;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

/**
 * Created by chrisjohnson on 10/12/14.
 */
public class DownloadHelper {

    public int responseCode;

    public String GetResponseBody() {
        return responseBody;
    }

    public String responseBody;

    public int GetResponseCode() {
        return responseCode;
    }

    private long responseTime;

    public long GetResponseTimeInMillis() { return responseTime; }

    private Map<String, List<String>> responseHeaders;

    public Map<String, List<String>> GetResponseHeaders() { return responseHeaders; }

    // Given a URL, establishes an HttpUrlConnection and retrieves
    // the web page content as a InputStream, which it stores
    // in a local var as well as the response code
    public void requestUrl(String myurl) throws IOException {
        InputStream is = null;
        // store the current time
        long timestamp = System.currentTimeMillis();

        // Only display the first 500 characters of the retrieved
        // web page content.
        int len = 500;

        try {
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            responseCode = conn.getResponseCode();
            responseHeaders = conn.getHeaderFields();

            is = conn.getInputStream();

            // Convert the InputStream into a string
            responseBody = readIt(is, len);

            // store the response time
            responseTime = System.currentTimeMillis() - timestamp;

            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    // Reads an InputStream and converts it to a String.
    private String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);
        return new String(buffer);
    }

    // utility function to get the response headers in human-readable form
    public String GetResponseHeadersText() {
        String text = "";
        Map<String, List<String>> map = responseHeaders;
        for (Map.Entry<String, List<String>> entry : map.entrySet()) {
            if (entry.getKey() != null ) {
                text += entry.getKey() +
                        " = " + entry.getValue() + "\n";
            }
        }
        return text;
    }
}
