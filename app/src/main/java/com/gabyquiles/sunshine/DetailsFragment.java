package com.gabyquiles.sunshine;


import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;
import android.support.v7.widget.ShareActionProvider;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.gabyquiles.sunshine.data.WeatherContract;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailsFragment extends Fragment  implements LoaderManager.LoaderCallbacks<Cursor>{
    private final String LOG_TAG = DetailsFragment.class.getSimpleName();
    private static final String FORECAST_SHARE_HASHTAG = " #SunshineApp";
    static final String DETAIL_URI = "URI";
    static final String DETAIL_TRANSITION_ANIMATION = "DTA";

    private String mForecast;
    private Uri mUri;
    private boolean mTransitionAnimation;

    private static final int DETAIL_LOADER = 0;

    private static final String[] DETAIL_COLUMNS = {
            // In this case the id needs to be fully qualified with a table name, since
            // the content provider joins the location & weather tables in the background
            // (both have an _id column)
            // On the one hand, that's annoying.  On the other, you can search the weather table
            // using the location set by the user, which is only in the Location table.
            // So the convenience is worth it.
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.WeatherEntry.COLUMN_HUMIDITY,
            WeatherContract.WeatherEntry.COLUMN_WIND_SPEED,
            WeatherContract.WeatherEntry.COLUMN_DEGREES,
            WeatherContract.WeatherEntry.COLUMN_PRESSURE,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
    };

    // These indices are tied to DETAIL_COLUMNS.  If DETAIL_COLUMNS changes, these
    // must change.
    static final int COL_WEATHER_DATE = 0;
    static final int COL_WEATHER_DESC = 1;
    static final int COL_WEATHER_MAX_TEMP = 2;
    static final int COL_WEATHER_MIN_TEMP = 3;
    static final int COL_WEATHER_HUMIDITY = 4;
    static final int COL_WEATHER_WIND_SPEED = 5;
    static final int COL_WEATHER_WIND_DIRECTION = 6;
    static final int COL_WEATHER_PRESSURE = 7;
    static final int COL_WEATHER_ID = 8;

    public ImageView mForecastIconImageView;
    public TextView mDateTextView;
    public TextView mForecastTextView;
    public TextView mHighTempTextView;
    public TextView mLowTempTextView;
    public TextView mHumidityTextView;
    public TextView mWindTextView;
    public TextView mPressureTextView;

    public DetailsFragment() {
        //Indicates activity that this fragment also has menu options
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(DetailsFragment.DETAIL_URI);
            mTransitionAnimation = arguments.getBoolean(DETAIL_TRANSITION_ANIMATION);
        }

        View rootView = inflater.inflate(R.layout.fragment_detail_start, container, false);
        mDateTextView= (TextView) rootView.findViewById(R.id.detail_date_textview);
        mForecastTextView = (TextView) rootView.findViewById(R.id.detail_forecast_textview);
        mHighTempTextView = (TextView) rootView.findViewById(R.id.detail_high_textview);
        mLowTempTextView = (TextView) rootView.findViewById(R.id.detail_low_textview);
        mHumidityTextView = (TextView) rootView.findViewById(R.id.detail_humidity_textview);
        mWindTextView = (TextView) rootView.findViewById(R.id.detail_wind_textview);
        mPressureTextView = (TextView) rootView.findViewById(R.id.detail_pressure_textview);
        mForecastIconImageView = (ImageView) rootView.findViewById(R.id.detail_icon);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstance) {
        /*
         * Initializes the CursorLoader. The URL_LOADER value is eventually passed
         * to onCreateLoader().
         */
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstance);
    }

    private Intent createShareForecastIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, mForecast + FORECAST_SHARE_HASHTAG);
        return shareIntent;
    }

    private void finishCreatingMenu(Menu menu) {
        // Retrieve the share menu item
        MenuItem menuItem = menu.findItem(R.id.action_share);
        menuItem.setIntent(createShareForecastIntent());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if ( getActivity() instanceof DetailsActivity ){
            // Inflate the menu; this adds items to the action bar if it is present.
            inflater.inflate(R.menu.menu_details, menu);
            finishCreatingMenu(menu);
        }
    }

    /*
* Callback that's invoked when the system has initialized the Loader and
* is ready to start the query. This usually happens when initLoader() is
* called. The loaderID argument contains the ID value passed to the
* initLoader() call.
*/
    @Override
    public Loader<Cursor> onCreateLoader(int loaderID, Bundle bundle)
    {
        if (null != mUri) {

            // Now create and return a CursorLoader that will take care of
            // creating a Cursor for the data being displayed.

            return new CursorLoader(getActivity(),
                    mUri,
                    DETAIL_COLUMNS,
                    null,
                    null,
                    null);
        }

        ViewParent vp = getView().getParent();
        if ( vp instanceof CardView ) {
            ((View)vp).setVisibility(View.INVISIBLE);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (!data.moveToFirst()) { return; }

        ViewParent vp = getView().getParent();
        if(vp instanceof CardView) {
            ((View) vp).setVisibility(View.VISIBLE);
        }

        String dateString = Utility.getFormattedMonthDay(getActivity(), data.getLong(COL_WEATHER_DATE));
        mDateTextView.setText(dateString);

        String weatherDescription = data.getString(COL_WEATHER_DESC);
        mForecastTextView.setText(weatherDescription);
        mForecastTextView.setContentDescription(getString(R.string.a11y_forecast, weatherDescription));

        String high = Utility.formatTemperature(getActivity(), data.getDouble(COL_WEATHER_MAX_TEMP));
        mHighTempTextView.setText(high);
        mHighTempTextView.setContentDescription(getString(R.string.a11y_high_temp, high));

        String low = Utility.formatTemperature(getActivity(), data.getDouble(COL_WEATHER_MIN_TEMP));
        mLowTempTextView.setText(low);
        mLowTempTextView.setContentDescription(getString(R.string.a11y_low_temp, low));

        String humidity = getActivity().getString(R.string.format_humidity, data.getDouble(COL_WEATHER_HUMIDITY));
        mHumidityTextView.setText(humidity);
        mHumidityTextView.setContentDescription(humidity);

        String wind = Utility.getFormattedWind(getActivity(), data.getFloat(COL_WEATHER_WIND_SPEED), data.getFloat(COL_WEATHER_WIND_DIRECTION));
        mWindTextView.setText(wind);
        mWindTextView.setContentDescription(wind);

        String pressure = getActivity().getString(R.string.format_pressure, data.getDouble(COL_WEATHER_PRESSURE));
        mPressureTextView.setText(pressure);
        mPressureTextView.setContentDescription(pressure);

        int weatherId = data.getInt(COL_WEATHER_ID);
        Glide.with(this)
                .load(Utility.getArtUrlForWeatherCondition(getActivity(), weatherId))
                .error(Utility.getArtResourceForWeatherCondition(weatherId))
                .crossFade()
                .into(mForecastIconImageView);
        mForecastIconImageView.setContentDescription(getString(R.string.a11y_forecast_icon, weatherDescription));

        AppCompatActivity activity = (AppCompatActivity)getActivity();
        Toolbar toolbarView = (Toolbar) getView().findViewById(R.id.toolbar);

        // We need to start the enter transition after the data has loaded
        if(mTransitionAnimation) {
            activity.supportStartPostponedEnterTransition();


            if ( null != toolbarView ) {
                activity.setSupportActionBar(toolbarView);

                activity.getSupportActionBar().setDisplayShowTitleEnabled(false);
                activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        } else {
            if ( null != toolbarView ) {
                Menu menu = toolbarView.getMenu();
                if ( null != menu ) menu.clear();
                toolbarView.inflateMenu(R.menu.menu_details);
                finishCreatingMenu(toolbarView.getMenu());
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    void onLocationChanged( String newLocation ) {
        // replace the uri, since the location has changed
        Uri uri = mUri;
        if (null != uri) {
            long date = WeatherContract.WeatherEntry.getDateFromUri(uri);
            Uri updatedUri = WeatherContract.WeatherEntry.buildWeatherLocationWithDate(newLocation, date);
            mUri = updatedUri;
            getLoaderManager().restartLoader(DETAIL_LOADER, null, this);
        }
        getView().setVisibility(View.INVISIBLE);
    }
}
