package com.gabyquiles.sunshine;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailsActivityFragment extends Fragment {

    public DetailsActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Intent intent = getActivity().getIntent();
        String forecast_string = intent.getStringExtra(MainActivity.FORECAST_DATA);
        View rootView = inflater.inflate(R.layout.fragment_details, container, false);
        TextView textView = (TextView) rootView.findViewById(R.id.placeholder);
        textView.setText(forecast_string);
        return rootView;
    }
}
