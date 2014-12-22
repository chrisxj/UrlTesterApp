package com.example.chrisjohnson.myapplication;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by chrisjohnson on 19/12/14.
 */
public class ResultFragment extends Fragment {

    public final static String HTML_SOURCE = "html_source";
    public final static String RESPONSE_HEADERS = "response_headers";
    public final static String BUTTON_TOKEN = "button_token";
    private String html_source;
    private String button_token;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     */
    public static ResultFragment newInstance(String html_source, String button_token) {
        ResultFragment fragment = new ResultFragment();
        Bundle args = new Bundle();
        args.putString(HTML_SOURCE, html_source);
        args.putString(BUTTON_TOKEN, button_token);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            html_source = getArguments().getString(HTML_SOURCE);
            button_token = getArguments().getString(BUTTON_TOKEN);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("FRAG", "ResultFragment.onCreateView Called");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_result, container, false);
        // set the value of the response body
        TextView htmlSourceTextView = (TextView) view.findViewById(R.id.html_source);
        htmlSourceTextView.setText(html_source);

        // get the button widgets
        Button htmlSourceButton = (Button) view.findViewById(R.id.button_tab_source);
        Button responseHeadersButton = (Button) view.findViewById(R.id.button_tab_headers);

        // highlight the appropriate button
        if ( button_token == HTML_SOURCE ) {
            Log.d("FRAG", "Highlighting the HTML Source button");
            htmlSourceButton.setPressed(true);
            responseHeadersButton.setPressed(false);
        } else {
            Log.d("FRAG", "Highlighting the HTML Source button");
            htmlSourceButton.setPressed(false);
            responseHeadersButton.setPressed(true);
        }

        return view;
    }
}
