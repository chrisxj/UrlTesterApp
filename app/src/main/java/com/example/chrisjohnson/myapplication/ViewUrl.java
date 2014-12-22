package com.example.chrisjohnson.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import java.io.IOException;

public class ViewUrl extends Activity {

    private TextView urlTextView;
    private TextView networkStatusTextView;
    private TextView wifiStatusTextView;
    private TextView radioStatusTextView;
    private TextView responseCodeTextView;
    private TextView responseTimeTextView;
    private String htmlSourceText;
    private String responseHeadersText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_url);

        // get the message from the intent
        Intent intent = getIntent();
        String url = intent.getStringExtra(MainActivity.EXTRA_URL);

        // get the text view and populate it with the message
        urlTextView = (TextView) findViewById(R.id.url_called);
        urlTextView.setText("URL Called: " + url);

        // populate the other textView widgets in local vars
        networkStatusTextView = (TextView) findViewById(R.id.network_status);
        wifiStatusTextView = (TextView) findViewById(R.id.wifi_status);
        radioStatusTextView = (TextView) findViewById(R.id.radio_status);
        responseCodeTextView = (TextView) findViewById(R.id.response_code);
        responseTimeTextView = (TextView) findViewById(R.id.response_time);

        // if we have no saved instance state (i.e. view is being created for the first time)
        // perform the request and populate the view widgets with the results
        if ( savedInstanceState == null) {
            // attempt the request
            performRequestAndStoreValues(url);
        }
    }

    private void performRequestAndStoreValues(String url) {
        //check the network is up, and then call URL
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        // use the network status helper to establish whether network is up
        // the helper needs access to the ConnectivityManager class
        NetworkStatusHelper helper = new NetworkStatusHelper(connMgr);

        // get the default values for the status fields, which assume status down
        String networkStatusTextValue = getResources().getString(R.string.network_status_down);
        String wifiStatusTextValue = getResources().getString(R.string.wifi_status_down);
        String radioStatusTextValue = getResources().getString(R.string.radio_status_down);

        if ( helper.IsNetworkUp()) {
            networkStatusTextValue = getResources().getString(R.string.network_status_up);
            // set the spinner going, then perform the request
            SpinnerFragment spinner = new SpinnerFragment();
            getFragmentManager().beginTransaction().add(R.id.result_fragment, spinner).commit();
            // network is up, so call the URL via an async task
            new DownloadWebpageTask().execute(url);
        } else {
            // update status fields with relevant info since network not available
            String responseCodeTextValue = getResources().getString(R.string.request_cancelled);
            responseCodeTextView.setText(responseCodeTextValue);

            // add the results tab fragment with the error message
            htmlSourceText = getResources().getString(R.string.no_source_available);
            ResultFragment fragment = ResultFragment.newInstance(htmlSourceText, ResultFragment.HTML_SOURCE);
            getFragmentManager().beginTransaction().add(R.id.result_fragment, fragment).commit();
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
                return download.GetResponseBody();
            } catch (IOException e) {
                return getResources().getString(R.string.url_invalid);
            }
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            // set source and response code from the result of the request
            String responseCodeTextValue = getResources().getString(R.string.response_code)
                    + " " + String.valueOf(download.GetResponseCode());
            String responseTimeTextValue = getResources().getString(R.string.response_time)
                    + " " + String.valueOf(download.GetResponseTimeInMillis()) + "ms";

            // update the text view widgets in the main activity
            responseCodeTextView.setText(responseCodeTextValue);
            responseTimeTextView.setText(responseTimeTextValue);

            // store the resulting HTML and headers for later
            htmlSourceText = result;
            responseHeadersText = download.GetResponseHeadersText();

            // display the result in the frag
            replaceFragWithResultSource(htmlSourceText, ResultFragment.HTML_SOURCE);
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

    public void onHtmlSourceTabClicked(View view) {
        Log.d("FRAG", "ViewUrl.onHtmlSourceTabClicked Called");
        replaceFragWithResultSource(htmlSourceText, ResultFragment.HTML_SOURCE);
    }

    public void onResponseHeadersTabClicked(View view) {
        Log.d("FRAG", "ViewUrl.onResponseHeadersTabClicked Called");
        replaceFragWithResultSource(responseHeadersText, ResultFragment.RESPONSE_HEADERS);
    }

    private void replaceFragWithResultSource(String html_source, String button_token) {
        ResultFragment fragment = ResultFragment.newInstance(html_source, button_token);
        getFragmentManager().beginTransaction().replace(R.id.result_fragment, fragment).commit();
    }
}
