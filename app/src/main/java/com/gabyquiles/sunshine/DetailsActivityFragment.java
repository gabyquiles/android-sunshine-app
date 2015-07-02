package com.gabyquiles.sunshine;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ShareActionProvider;
import android.widget.TextView;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailsActivityFragment extends Fragment {
    private final String LOG_TAG = DetailsActivityFragment.class.getSimpleName();
    private static final String FORECAST_SHARE_HASHTAG = " #SunshineApp";

    private ShareActionProvider shareProvider;
    private String forecastData;

    public DetailsActivityFragment() {
        //Indicates activity that this fragment also has menu options
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_details, container, false);
        Intent intent = getActivity().getIntent();
        if(intent != null && intent.hasExtra(MainActivity.FORECAST_DATA)) {
            forecastData = intent.getStringExtra(MainActivity.FORECAST_DATA);
            TextView textView = (TextView) rootView.findViewById(R.id.placeholder);
            textView.setText(forecastData);
        }
        return rootView;
    }

    private Intent createShareForecastIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, forecastData + FORECAST_SHARE_HASHTAG);
        return shareIntent;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.forecastfragment, menu);

        //Get MenuItem for sharing
        MenuItem item = menu.findItem(R.id.action_share);

        //Get share provider
        shareProvider = (ShareActionProvider) item.getActionProvider();

        //Atthach an intent to this ShareActionProvider. Update when data to be shared changes
        if(shareProvider != null) {
            shareProvider.setShareIntent(createShareForecastIntent());
        } else {
            Log.v(LOG_TAG, "Share action provider is null");
        }
    }

}
