package com.example.chrisjohnson.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;


public class MainActivity extends Activity {
    public final static String EXTRA_MESSAGE = "com.example.myapplication.MESSAGE";
    public final static String EXTRA_URL = "com.example.myapplication.URL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_main);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_search:
                openSearch();
                return true;
            case R.id.action_settings:
                openSettings();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void openSearch() {

    }

    private void openSettings() {
    }

    /** Called when the user clicks the send button */
    /*public void sendMessage(View view) {
        // do something in response to the button
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        EditText editText = (EditText) findViewById(R.id.edit_message);
        String message = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }*/

    /** Called when the user clicks the go button */
    public void loadUrl(View view) {
        // load the URL view activity ...
        Intent intent = new Intent(this, ViewUrl.class);
        EditText editUrl = (EditText) findViewById(R.id.edit_url);
        String url = editUrl.getText().toString();
        UrlHelper helper = new UrlHelper();
        intent.putExtra(EXTRA_URL, helper.NormaliseUrl(url));
        startActivity(intent);
    }
}
