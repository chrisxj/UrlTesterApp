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
import android.widget.TextView;
import java.io.IOException;



public class ViewUrl extends Activity {
    private TextView urlText;
    private TextView networkStatusText;
    private TextView wifiStatusText;
    private TextView radioStatusText;
    private TextView responseCodeText;
    private TextView htmlSource;

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
        networkStatusText = (TextView) findViewById(R.id.network_status);
        wifiStatusText = (TextView) findViewById(R.id.wifi_status);
        radioStatusText = (TextView) findViewById(R.id.radio_status);
        responseCodeText = (TextView) findViewById(R.id.response_code);
        htmlSource = (TextView) findViewById(R.id.html_source);

        //check the network is up, and then call URL
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        // use the network status helper to establish whether network is up
        // the helper needs access to the ConnectivityManager class
        NetworkStatusHelper helper = new NetworkStatusHelper(connMgr);

        if ( helper.IsNetworkUp()) {
            networkStatusText.setText(R.string.network_status_up);
            // network is up, so call the URL via an async task
            new DownloadWebpageTask().execute(url);
        }

        // show status for wifi and radio connectivity
        if ( helper.IsWifiConnected()) {
            wifiStatusText.setText(R.string.wifi_status_up);
        }
        if ( helper.IsRadioConnected()) {
            radioStatusText.setText(R.string.radio_status_up);
        }

        // append extra info for Radio and Wifi interfaces, if available
        if ( helper.GetWifiExtraInfo() != "") {
            wifiStatusText.append(", " + helper.GetWifiExtraInfo());
        }
        if ( helper.GetRadioExtraInfo() != "") {
            radioStatusText.append(", " + helper.GetRadioExtraInfo());
        }

        // display roaming info for radio interface
        if ( helper.IsRoaming()) {
            radioStatusText.append(", " +
                    getResources().getString(R.string.roaming_active));
        } else {
            radioStatusText.append(", " + getResources().getString(R.string.roaming_not_active));
        }
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
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            htmlSource.setText("Response Body: \n" + result);
            responseCodeText.setText("Response Code: " + String.valueOf(download.getResponseCode()));
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
