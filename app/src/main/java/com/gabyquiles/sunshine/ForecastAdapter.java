package com.gabyquiles.sunshine;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.gabyquiles.sunshine.data.WeatherContract;

/**
 * {@link ForecastAdapter} exposes a list of weather forecasts
 * from a {@link android.database.Cursor} to a {@link android.widget.ListView}.
 */
public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ViewHolder>  {
    private final int VIEW_TYPE_TODAY = 0;
    private final int VIEW_TYPE_FUTURE_DAY= 1;

    private boolean mUseTodayLayout;

    private Cursor mCursor;
    final private Context mContext;
    final private ForecastAdapterOnClickHandler mClickHandler;
    final private View mEmptyView;
    final private ItemChoiceManager mICM;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public final ImageView mIconView;
        public final TextView mDateView;
        public final TextView mDescriptionView;
        public final TextView mHighTempView;
        public final TextView mLowTempView;

        public ViewHolder(View view) {
            super(view);
            mIconView = (ImageView) view.findViewById(R.id.list_item_icon);
            mDateView = (TextView) view.findViewById(R.id.list_item_date_textview);
            mDescriptionView = (TextView) view.findViewById(R.id.list_item_forecast_textview);
            mHighTempView = (TextView) view.findViewById(R.id.list_item_high_textview);
            mLowTempView = (TextView) view.findViewById(R.id.list_item_low_textview);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);
            int dateColumnIndex = mCursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_DATE);
            mClickHandler.onClick(mCursor.getLong(dateColumnIndex), this);
            mICM.onClick(this);
        }
    }

    public static interface ForecastAdapterOnClickHandler {
        void onClick(Long date, ViewHolder vh);
    }

    public ForecastAdapter(Context context, View emtpyView, ForecastAdapterOnClickHandler clickHandler, int choiceMode) {
        mContext = context;
        mClickHandler = clickHandler;
        mEmptyView = emtpyView;
        mICM = new ItemChoiceManager(this);
        mICM.setChoiceMode(choiceMode);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if(viewGroup instanceof RecyclerView) {
            int layoutId = getLayout(viewType);
            View view = LayoutInflater.from(mContext).inflate(layoutId, viewGroup, false);
            view.setFocusable(true);
            return new ViewHolder(view);
        } else {
            throw new RuntimeException("Not bound to RecyclerViewSelection");
        }
    }

    /*
        This is where we fill-in the views with the contents of the cursor.
     */
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        mCursor.moveToPosition(position);

        int weatherId = mCursor.getInt(ForecastFragment.COL_WEATHER_CONDITION_ID);

        int defaultImage;
        switch (getItemViewType(mCursor.getPosition())) {
            case VIEW_TYPE_TODAY:
                defaultImage = Utility.getArtResourceForWeatherCondition(weatherId);
                break;
            default:
                defaultImage = Utility.getIconResourceForWeatherCondition(weatherId);
        }

        if (Utility.usingLocalGraphics(mContext)) {
            viewHolder.mIconView.
                    setImageResource(defaultImage);
        } else {
            Glide.with(mContext)
                    .load(Utility.getArtUrlForWeatherCondition(mContext, weatherId))
                    .error(defaultImage)
                    .crossFade()
                    .into(viewHolder.mIconView);
        }

        String forecast_conditions = mCursor.getString(ForecastFragment.COL_WEATHER_DESC);
        viewHolder.mDescriptionView.setText(forecast_conditions);
        viewHolder.mDescriptionView.setContentDescription(mContext.getString(R.string.a11y_forecast, forecast_conditions));

        String dateStr = Utility.getDayName(mContext, mCursor.getLong(ForecastFragment.COL_WEATHER_DATE));
        viewHolder.mDateView.setText(dateStr);

        // Read high temperature from cursor

        float high = mCursor.getFloat(ForecastFragment.COL_WEATHER_MAX_TEMP);
        String high_temp = Utility.formatTemperature(mContext, high);
        viewHolder.mHighTempView.setText(high_temp);
        viewHolder.mHighTempView.setContentDescription(mContext.getString(R.string.a11y_high_temp, high_temp));

        float low = mCursor.getFloat(ForecastFragment.COL_WEATHER_MIN_TEMP);
        String low_temp = Utility.formatTemperature(mContext, low);
        viewHolder.mLowTempView.setText(low_temp);
        viewHolder.mLowTempView.setContentDescription(mContext.getString(R.string.a11y_low_temp, low_temp));
        mICM.onBindViewHolder(viewHolder, position);
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        mICM.onRestoreInstanceState(savedInstanceState);
    }

    public void onSaveInstanceState(Bundle outState) {
        mICM.onSaveInstanceState(outState);
    }

    public void setUseTodayLayout(boolean useTodayLayout) {
        this.mUseTodayLayout = useTodayLayout;
    }

    public int getSelectedItemPosition() {
        return mICM.getSelectedItemPosition();
    }

    @Override
    public int getItemViewType(int position) {
        return (position == 0 && mUseTodayLayout) ? VIEW_TYPE_TODAY : VIEW_TYPE_FUTURE_DAY;
    }

    @Override
    public int getItemCount() {
        if ( null == mCursor ) return 0;
        return mCursor.getCount();
    }

    private int getLayout(int requestedViewType) {
        int layoutId = -1;
        switch (requestedViewType) {
            case VIEW_TYPE_TODAY:
                layoutId = R.layout.list_item_forecast_today;
                break;
            case VIEW_TYPE_FUTURE_DAY:
                layoutId = R.layout.list_item_forecast;
                break;
            default:
                layoutId = R.layout.list_item_forecast;
        }
        return layoutId;
    }



    public void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        mEmptyView.setVisibility(getItemCount() == 0 ? View.VISIBLE : View.GONE);
        notifyDataSetChanged();
    }

    public Cursor getCursor() {
        return mCursor;
    }

    public void selectView(RecyclerView.ViewHolder viewHolder) {
        if ( viewHolder instanceof ViewHolder ) {
            ViewHolder vfh = (ViewHolder)viewHolder;
            vfh.onClick(vfh.itemView);
        }
    }

}