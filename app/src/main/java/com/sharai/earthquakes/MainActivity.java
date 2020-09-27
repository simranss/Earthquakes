package com.sharai.earthquakes;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String> {

    static String urlStr = "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&minmagnitude=1";

    RecyclerView earthquakeListView;

    LinearLayoutManager layoutManager;

    EarthquakeAdapter adapter;
    ArrayList<Earthquake> earthquakes;
    static ArrayList<Earthquake> earthquakesFull;
    static ArrayList<Earthquake> earthquakesOriginal;

    LoaderManager loaderManager;

    static TextView alertTextView;

    int loaderId;

    Button retryButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initialize the retry button
        retryButton = findViewById(R.id.retry_button);
        retryButton.setVisibility(View.GONE);

        //initialize recyclerview
        earthquakeListView = findViewById(R.id.earthquake_list);

        //Set the layout manager for the recyclerview
        layoutManager = new LinearLayoutManager(this);
        earthquakeListView.setLayoutManager(layoutManager);

        //Set the adapter for the recyclerview
        earthquakes = new ArrayList<>();
        adapter = new EarthquakeAdapter(earthquakes, this);
        earthquakeListView.setAdapter(adapter);

        //initialize alertTextView
        alertTextView = findViewById(R.id.alert_text);

        if (internet()) {
            //Load the data
            loaderManager = LoaderManager.getInstance(this);
            loaderManager.initLoader(1, null, this);
        } else {
            alertTextView.setText(R.string.no_internet);
        }
    }

    //Create and display the initial data
    public void initializeLists(String JSONResponse) {
        //Get the earthquakes from the JSON
        earthquakes.addAll(QueryUtils.extractEarthquakes(JSONResponse));
        earthquakesFull = new ArrayList<>(earthquakes);
        earthquakesOriginal = new ArrayList<>(earthquakes);

        Log.d("Data found", String.valueOf(earthquakes.size()));

        //Display the data collected
        adapter.notifyDataSetChanged();
    }

    //Checks the internet
    public boolean internet() {
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(CONNECTIVITY_SERVICE);

        if (cm != null) {
            if (Build.VERSION.SDK_INT < 23) {
                final NetworkInfo ni = cm.getActiveNetworkInfo();

                if (ni != null) {
                    return (ni.isConnected() && (ni.getType() == ConnectivityManager.TYPE_WIFI || ni.getType() == ConnectivityManager.TYPE_MOBILE || ni.getType() == ConnectivityManager.TYPE_VPN));
                }
            } else {
                final Network n = cm.getActiveNetwork();

                if (n != null) {
                    final NetworkCapabilities nc = cm.getNetworkCapabilities(n);

                    assert nc != null;
                    return (nc.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || nc.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || nc.hasTransport(NetworkCapabilities.TRANSPORT_VPN));
                }
            }
        }

        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater  = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        //Initialize the SearchView
        MenuItem actionSearch = menu.findItem(R.id.action_search);
        SearchView search = (SearchView) actionSearch.getActionView();

        //Searching process
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sort_mag:
                //Sort by magnitude
                Log.d("sort clicked", "magnitude");
                adapter.sortByMag();
                return true;
            case R.id.action_sort_location:
                //Sort by location
                Log.d("sort clicked", "location");
                adapter.sortByLoc();
                return true;
            case R.id.action_sort_time:
                //Sort by recent time
                adapter.sortByTime();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @NonNull
    @Override
    public Loader<String> onCreateLoader(int id, @Nullable Bundle args) {
        loaderId = id;
        return new DataLoader(this, urlStr);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<String> loader, String data) {
        if (data == null) {
            Log.d("JSONResponse", "result is null");
            alertTextView.setText(R.string.no_data);
            return;
        } else {
            alertTextView.setText("");
            Log.d("JSONResponse", "result is not null");
        }

        //Create and display the initial data
        initializeLists(data);

        //Destroy the loader
        loader.stopLoading();
        loader.cancelLoad();
        loader.abandon();
        loaderManager.destroyLoader(loaderId);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<String> loader) {
        //Clear the earthquakeListView
        earthquakes.clear();
        earthquakesFull.clear();
        earthquakesOriginal.clear();
        adapter.notifyDataSetChanged();
    }
}