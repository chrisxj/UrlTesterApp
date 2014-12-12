package com.example.chrisjohnson.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.Random;


public class DisplayMessageActivity extends Activity {

    static String RANDOM_KEY = "randKey";
    private String RANDOM_VALUE = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_message);

        // get the message from the intent
        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

        // get the text view and populate it with the message
        TextView textView = (TextView) findViewById(R.id.message_text);
        textView.setText(message);

        // also set the text within the repeater text view with a random integer
        //  - this is an experiment to look at activity lifecycle quirks
        if (savedInstanceState != null) {
            // we have saved state - pull the random string from there
            RANDOM_VALUE = savedInstanceState.getString(RANDOM_KEY);
        } else {
            // no saved activity state - create from scratch
            Integer randomInteger = new Random().nextInt();
            RANDOM_VALUE = String.valueOf(randomInteger);
            // we can't store this in the saned state yet, as it's not been created :-S
        }
        // fill in the text view widget
        TextView textView2 = (TextView) findViewById(R.id.message_repeater);
        textView2.setText("Your random number is: "+RANDOM_VALUE);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // store the value of our random string for future use
        savedInstanceState.putString(RANDOM_KEY,RANDOM_VALUE);
        // call the super equivalent
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /** Called when the user clicks the try another button */
    public void tryAnother(View view) {
        // do something in response to the button
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

}
