package com.example.chrisjohnson.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.io.IOException;



public class ViewUrl extends Activity {
    private TextView urlText;
    private TextView networkStatusTextView;
    private TextView wifiStatusTextView;
    private TextView radioStatusTextView;
    private TextView responseCodeTextView;
    private TextView htmlSourceTextView;
    private ProgressBar progressBarView;
    private String networkStatusTextValue = "";
    private String wifiStatusTextValue = "";
    private String radioStatusTextValue = "";
    private String responseCodeTextValue = "";
    private String htmlSourceTextValue = "";
    private String NETWORK_STATUS_KEY = "networkStatus";
    private String WIFI_STATUS_KEY = "wifiStatus";
    private String RADIO_STATUS_KEY = "radioStatus";
    private String RESPONSE_CODE_KEY = "responseCode";
    private String HTML_SOURCE_KEY = "htmlSource";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_url);

        // get the message from the intent
        Intent intent = getIntent();
        String url = intent.getStringExtra(MainActivity.EXTRA_URL);

        // get the text view and populate it with the message
        urlText = (TextView) findViewById(R.id.url_called);
        urlText.setText("URL Called: " + url);

        // populate the other textView widgets in local vars
        networkStatusTextView = (TextView) findViewById(R.id.network_status);
        wifiStatusTextView = (TextView) findViewById(R.id.wifi_status);
        radioStatusTextView = (TextView) findViewById(R.id.radio_status);
        responseCodeTextView = (TextView) findViewById(R.id.response_code);
        htmlSourceTextView = (TextView) findViewById(R.id.html_source);
        progressBarView = (ProgressBar) findViewById(R.id.view_url_spinner);

        // if we have no saved instance state (i.e. view is being created for the first time)
        // perform the request and populate the view widgets with the results
        if ( savedInstanceState == null) {
            // set default values for the status text vars
            networkStatusTextValue = getResources().getString(R.string.network_status_down);
            wifiStatusTextValue = getResources().getString(R.string.wifi_status_down);
            radioStatusTextValue = getResources().getString(R.string.radio_status_down);
            responseCodeTextValue = getResources().getString(R.string.request_in_progress);
            htmlSourceTextValue = getResources().getString(R.string.html_source);
            // attempt the request
            performRequestAndStoreValues(url);
        } else {
            // get values from saved instance state
            networkStatusTextValue = savedInstanceState.getString(NETWORK_STATUS_KEY);
            wifiStatusTextValue = savedInstanceState.getString(WIFI_STATUS_KEY);
            radioStatusTextValue = savedInstanceState.getString(RADIO_STATUS_KEY);
            responseCodeTextValue = savedInstanceState.getString(RESPONSE_CODE_KEY);
            htmlSourceTextValue = savedInstanceState.getString(HTML_SOURCE_KEY);
            // update the status TextView widgets
            networkStatusTextView.setText(networkStatusTextValue);
            wifiStatusTextView.setText(wifiStatusTextValue);
            radioStatusTextView.setText(radioStatusTextValue);
            responseCodeTextView.setText(responseCodeTextValue);
            htmlSourceTextView.setText(htmlSourceTextValue);
            // show the htmlSource textView
            htmlSourceTextView.setVisibility(View.VISIBLE);
        }


    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // store the value of our random string for future use
        savedInstanceState.putString(NETWORK_STATUS_KEY,networkStatusTextValue);
        savedInstanceState.putString(WIFI_STATUS_KEY,wifiStatusTextValue);
        savedInstanceState.putString(RADIO_STATUS_KEY,radioStatusTextValue);
        savedInstanceState.putString(RESPONSE_CODE_KEY,responseCodeTextValue);
        savedInstanceState.putString(HTML_SOURCE_KEY,htmlSourceTextValue);
        // call the super equivalent
        super.onSaveInstanceState(savedInstanceState);
    }

    private void performRequestAndStoreValues(String url) {
        //check the network is up, and then call URL
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        // use the network status helper to establish whether network is up
        // the helper needs access to the ConnectivityManager class
        NetworkStatusHelper helper = new NetworkStatusHelper(connMgr);

        if ( helper.IsNetworkUp()) {
            networkStatusTextValue = getResources().getString(R.string.network_status_up);
            // set the spinner going, then perform the request
            progressBarView.setVisibility(View.VISIBLE);
            // network is up, so call the URL via an async task
            new DownloadWebpageTask().execute(url);
        } else {
            // update status fields with relevant info since network not available
            responseCodeTextValue = getResources().getString(R.string.request_cancelled);
            htmlSourceTextValue = getResources().getString(R.string.no_source_available);
            responseCodeTextView.setText(responseCodeTextValue);
            htmlSourceTextView.setText(htmlSourceTextValue);
            // show the htmlSource textView
            htmlSourceTextView.setVisibility(View.VISIBLE);
        }

        // show status for wifi and radio connectivity
        if ( helper.IsWifiConnected()) {
            wifiStatusTextValue = getResources().getString(R.string.wifi_status_up);
        }

        if ( helper.IsRadioConnected()) {
            radioStatusTextValue = getResources().getString(R.string.radio_status_up);
        }

        // append extra info for Radio and Wifi interfaces, if available
        if ( helper.GetWifiExtraInfo() != "") {
            wifiStatusTextValue += ", " + helper.GetWifiExtraInfo();
        }
        if ( helper.GetRadioExtraInfo() != "") {
            radioStatusTextValue += ", " + helper.GetRadioExtraInfo();
        }

        // display roaming info for radio interface
        if ( helper.IsRoaming()) {
            radioStatusTextValue += ", " +
                    getResources().getString(R.string.roaming_active);
        } else {
            radioStatusTextValue += ", " +
                    getResources().getString(R.string.roaming_not_active);
        }

        // update the status TextView widgets not involved in the async request
        networkStatusTextView.setText(networkStatusTextValue);
        wifiStatusTextView.setText(wifiStatusTextValue);
        radioStatusTextView.setText(radioStatusTextValue);
    }

    // Uses AsyncTask to create a task away from the main UI thread. This task takes a
    // URL string and uses it to create an HttpUrlConnection. Once the connection
    // has been established, the AsyncTask downloads the contents of the webpage as
    // an InputStream. Finally, the InputStream is converted into a string, which is
    // displayed in the UI by the AsyncTask's onPostExecute method.
    private class DownloadWebpageTask extends AsyncTask<String, Void, String> {
        private DownloadHelper download = new DownloadHelper();
        @Override
        protected String doInBackground(String... urls) {

            // params comes from the execute() call: params[0] is the url.
            try {
                download.requestUrl(urls[0]);
                return download.getResponseBody();
            } catch (IOException e) {
                return getResources().getString(R.string.url_invalid);
            }
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            // set source and response code from the result of the request
            htmlSourceTextValue = result;
            responseCodeTextValue = getResources().getString(R.string.response_code)
                    + " " + String.valueOf(download.getResponseCode());

            //update the text view widgets
            htmlSourceTextView.setText(htmlSourceTextValue);
            responseCodeTextView.setText(responseCodeTextValue);

            // hide the spinner and display the textView
            progressBarView.setVisibility(View.INVISIBLE);
            htmlSourceTextView.setVisibility(View.VISIBLE);
        }
    }




        @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_url, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
