package com.gabyquiles.sunshine;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class ForecastFragment extends Fragment {
    private final String LOG_TAG = ForecastFragment.class.getSimpleName();
    protected ArrayAdapter<String> mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Indicates activity that this fragment also has menu options
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.forecastfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            updateWeather();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateWeather();
    }

    private void updateWeather() {
        Activity parent = getActivity();
        FetchWeatherTask task = new FetchWeatherTask(getActivity(), mAdapter);
        //Remeber that this preferences are the DEFAULT
        SharedPreferences postalCode = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String zip = postalCode.getString(parent.getString(R.string.pref_location_key), parent.getString(R.string.pref_location_default));
        task.execute(zip);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        List<String> week_forecast = new ArrayList<String>();

        mAdapter = new ArrayAdapter<String>(getActivity(),
                R.layout.list_item_forecast, R.id.list_item_forecast_textview, week_forecast);
        View rootView = inflater.inflate(R.layout.fragment_main, container);

        ListView list = (ListView) rootView.findViewById(R.id.listview_forecast);

        list.setAdapter(mAdapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Context context = getActivity();
                String selected_item = mAdapter.getItem(position);

                Intent intent = new Intent(context, DetailsActivity.class);
                intent.putExtra(MainActivity.FORECAST_DATA, selected_item);
                context.startActivity(intent);
            }
        });

        return rootView;
    }
}
