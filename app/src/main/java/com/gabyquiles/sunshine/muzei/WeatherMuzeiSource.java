package com.gabyquiles.sunshine.muzei;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;

import com.gabyquiles.sunshine.MainActivity;
import com.gabyquiles.sunshine.Utility;
import com.gabyquiles.sunshine.data.WeatherContract;
import com.gabyquiles.sunshine.sync.SunshineSyncAdapter;
import com.google.android.apps.muzei.api.Artwork;
import com.google.android.apps.muzei.api.MuzeiArtSource;

/**
 * Created by gabrielquiles-perez on 10/22/15.
 */
public class WeatherMuzeiSource extends MuzeiArtSource {
    private static final String[] FORECAST_COLUMNS = new String[]{
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC
    };
    // these indices must match the projection
    private static final int INDEX_WEATHER_ID = 0;
    private static final int INDEX_SHORT_DESC = 1;

    public WeatherMuzeiSource() {
        super("WeatherMuzeiSource");
    }

    @Override
    protected void onUpdate(int reason) {
        String location = Utility.getPreferredLocation(this);
        Uri weatherUri = WeatherContract.WeatherEntry.buildWeatherLocationWithDate(location,
                System.currentTimeMillis());

        Cursor cursor = getContentResolver().query(weatherUri, FORECAST_COLUMNS, null, null, null);
        if(cursor.moveToFirst()) {
            int weatherId = cursor.getInt(INDEX_WEATHER_ID);
            String description = cursor.getString(INDEX_SHORT_DESC);

            String imageUrl = Utility.getImageUrlForWeatherCondition(weatherId);
            if(imageUrl != null) {
                publishArtwork(new Artwork.Builder()
                        .imageUri(Uri.parse(imageUrl))
                        .title(description)
                        .byline(location)
                        .viewIntent(new Intent(this, MainActivity.class))
                        .build()
                );
            }
        }
        cursor.close();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        super.onHandleIntent(intent);

        boolean dataUpdated = intent != null
                && SunshineSyncAdapter.ACTION_DATA_UPDATED.equals(intent.getAction());

        if(dataUpdated && isEnabled()) {
            onUpdate(UPDATE_REASON_OTHER);
        }
    }
}
