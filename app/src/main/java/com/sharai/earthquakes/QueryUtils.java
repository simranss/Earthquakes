package com.sharai.earthquakes;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public final class QueryUtils {

    private QueryUtils() {
    }

    public static ArrayList<Earthquake> extractEarthquakes(String JSONResponse) {
        ArrayList<Earthquake> earthquakes = new ArrayList<>();
        if (JSONResponse != null) {
            Log.d("JSONResponse", JSONResponse);
            try {
                JSONObject root = new JSONObject(JSONResponse);
                JSONArray features = root.optJSONArray("features");
                assert features != null;
                int size = features.length();

                for (int i = 0; i < size; i++) {

                    //initialize variables for Earthquake.class
                    float magnitude;
                    String place, url, id;
                    long time;

                    //initialize the JSON objects
                    JSONObject feature = features.optJSONObject(i);
                    JSONObject properties = feature.optJSONObject("properties");
                    Log.d("count", String.valueOf(i));

                    //value of the variables for Earthquake.class
                    assert properties != null;
                    magnitude = Float.parseFloat(properties.optString("mag"));
                    place = properties.optString("place");
                    time = Long.parseLong(properties.optString("time"));
                    url = properties.optString("url");
                    id = feature.optString("id");

                    Earthquake earthquake = new Earthquake(magnitude, place, time, url, id);

                    earthquakes.add(earthquake);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Log.d("JSONResponse", "Empty");
        }

        return earthquakes;
    }
}
