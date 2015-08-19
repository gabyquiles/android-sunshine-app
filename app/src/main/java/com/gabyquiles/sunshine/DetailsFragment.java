package com.gabyquiles.sunshine;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ShareActionProvider;
import android.widget.TextView;

import com.gabyquiles.sunshine.data.WeatherContract;

import org.w3c.dom.Text;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailsFragment extends Fragment  implements LoaderManager.LoaderCallbacks<Cursor>{
    private final String LOG_TAG = DetailsFragment.class.getSimpleName();
    private static final String FORECAST_SHARE_HASHTAG = " #SunshineApp";

    private ShareActionProvider mShareActionProvider;
    private String mForecast;

    private static final int FORECAST_LOADER = 0;

    private static final String[] FORECAST_COLUMNS = {
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

    // These indices are tied to FORECAST_COLUMNS.  If FORECAST_COLUMNS changes, these
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
    public TextView mDayNameTextView;
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
        View rootView = inflater.inflate(R.layout.fragment_details, container, false);
        mDayNameTextView = (TextView) rootView.findViewById(R.id.day_name_textview);
        mDateTextView= (TextView) rootView.findViewById(R.id.date_textview);
        mForecastTextView = (TextView) rootView.findViewById(R.id.forecast_textview);
        mHighTempTextView = (TextView) rootView.findViewById(R.id.high_temp_textview);
        mLowTempTextView = (TextView) rootView.findViewById(R.id.low_temp_textview);
        mHumidityTextView = (TextView) rootView.findViewById(R.id.humidity_textview);
        mWindTextView = (TextView) rootView.findViewById(R.id.wind_textview);
        mPressureTextView = (TextView) rootView.findViewById(R.id.pressure_textview);
        mForecastIconImageView = (ImageView) rootView.findViewById(R.id.forecast_icon_imageview);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstance) {
        /*
         * Initializes the CursorLoader. The URL_LOADER value is eventually passed
         * to onCreateLoader().
         */
        getLoaderManager().initLoader(FORECAST_LOADER, null, this);
        super.onActivityCreated(savedInstance);
    }

    private Intent createShareForecastIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, mForecast + FORECAST_SHARE_HASHTAG);
        return shareIntent;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.forecastfragment, menu);

        //Get MenuItem for sharing
        MenuItem item = menu.findItem(R.id.action_share);

        //Get share provider
        mShareActionProvider = (ShareActionProvider) item.getActionProvider();

        //Atthach an intent to this ShareActionProvider. Update when data to be shared changes
        if(mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(createShareForecastIntent());
        } else {
            Log.v(LOG_TAG, "Share action provider is null");
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

        Intent intent = getActivity().getIntent();
        if (intent == null) {
            return null;
        }
        String locationSetting = Utility.getPreferredLocation(getActivity());

        String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATE + " ASC";
        Uri weatherForLocationUri = WeatherContract.WeatherEntry.buildWeatherLocationWithStartDate(
                locationSetting, System.currentTimeMillis());

        return new CursorLoader(getActivity(),
                intent.getData(),
                FORECAST_COLUMNS,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (!data.moveToFirst()) { return; }
        boolean isMetric = Utility.isMetric(getActivity());

        String dayName = Utility.getDayName(getActivity(), data.getLong(COL_WEATHER_DATE));

        mDayNameTextView.setText(dayName);

        String dateString = Utility.getFormattedMonthDay(getActivity(), data.getLong(COL_WEATHER_DATE));
        mDateTextView.setText(dateString);

        String weatherDescription = data.getString(COL_WEATHER_DESC);
        mForecastTextView.setText(weatherDescription);

        String high = Utility.formatTemperature(getActivity(), data.getDouble(COL_WEATHER_MAX_TEMP), isMetric);
        mHighTempTextView.setText(high);

        String low = Utility.formatTemperature(getActivity(), data.getDouble(COL_WEATHER_MIN_TEMP), isMetric);
        mLowTempTextView.setText(low);

        String humidity = getActivity().getString(R.string.format_humidity, data.getDouble(COL_WEATHER_HUMIDITY));
        mHumidityTextView.setText(humidity);

        String wind = Utility.getFormattedWind(getActivity(), data.getFloat(COL_WEATHER_WIND_SPEED), data.getFloat(COL_WEATHER_WIND_DIRECTION));
        mWindTextView.setText(wind);

        String pressure = getActivity().getString(R.string.format_pressure, data.getDouble(COL_WEATHER_PRESSURE));
        mPressureTextView.setText(pressure);


        int iconId = Utility.getArtResourceForWeatherCondition(data.getInt(COL_WEATHER_ID));
        mForecastIconImageView.setImageResource(iconId);

        // If onCreateOptionsMenu has already happened, we need to update the share intent now.
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(createShareForecastIntent());
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
