package com.gabyquiles.sunshine;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by gabrielquiles-perez on 6/13/15.
 */
public class WeatherDataParser {
    /**
     * Given a string of the form returned by the api call:
     * http://api.openweathermap.org/data/2.5/forecast/daily?q=94043&mode=json&units=metric&cnt=7
     * retrieve the maximum temperature for the day indicated by dayIndex
     * (Note: 0-indexed, so 0 would refer to the first day).
     */
    public static double getMaxTemperatureForDay(String weatherJsonStr, int dayIndex)
            throws JSONException {
        JSONObject weatherJsonObj = new JSONObject(weatherJsonStr);
        JSONArray daysList = weatherJsonObj.getJSONArray("list");
        JSONObject dayObj = daysList.getJSONObject(dayIndex);
        Double maxTemp = dayObj.getJSONObject("temp").getDouble("max");
        return maxTemp;
    }
}
