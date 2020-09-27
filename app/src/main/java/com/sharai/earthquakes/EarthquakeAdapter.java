package com.sharai.earthquakes;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class EarthquakeAdapter extends RecyclerView.Adapter<EarthquakeAdapter.EarthquakeViewHolder> implements Filterable {

    //Data
    ArrayList<Earthquake> earthquakes;

    //Inflating the layout
    Context context;
    LayoutInflater inflater;

    //Date
    SimpleDateFormat locFormat = new SimpleDateFormat("dd MMM yyyy\nhh:mm a");
    Date dateObject;
    String date;

    //Location
    String location;
    String[] locationParts;

    //Magnitude decimals
    DecimalFormat magFormat = new DecimalFormat("0.0");
    String magnitude;

    //Magnitude Circle color
    int colorId;

    public EarthquakeAdapter (ArrayList<Earthquake> earthquakes, Context context) {
        this.earthquakes = earthquakes;
        this.context = context;
    }

    @NonNull
    @Override
    public EarthquakeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        inflater = LayoutInflater.from(context);
        View view  = inflater.inflate(R.layout.list_item, parent, false);
        return new EarthquakeViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(@NonNull EarthquakeViewHolder holder, int position) {
        //Date
        dateObject = new Date(earthquakes.get(position).getTime());
        date = locFormat.format(dateObject);

        //Location split
        location = earthquakes.get(position).getPlace();
        locationParts = splitLocation(location);

        //Bring magnitude to one decimal
        magnitude = magFormat.format(earthquakes.get(position).getMagnitude());

        //Color of the background of the magnitude
        colorId = getMagColor(Float.parseFloat(magnitude));
        setMagColor(colorId, holder);

        //Display the data
        holder.magTextView.setText(magnitude);
        holder.offLocTextView.setText(locationParts[0]);
        holder.priLocTextView.setText(locationParts[1]);
        holder.timeTextView.setText(date);
    }

    @Override
    public int getItemCount() {
        return earthquakes.size();
    }

    @Override
    public Filter getFilter() {
        return earthquakeFilter;
    }

    Filter earthquakeFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Earthquake> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(MainActivity.earthquakesFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (int i = 0; i < MainActivity.earthquakesFull.size(); i++) {
                    Earthquake earthquake = MainActivity.earthquakesFull.get(i);
                    String[] locationParts = splitLocation(earthquake.getPlace());
                    if (locationParts[1].toLowerCase().contains(filterPattern)) {
                        filteredList.add(earthquake);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            earthquakes.clear();
            earthquakes.addAll((ArrayList) results.values);
            if ((earthquakes.size() == 0) || (earthquakes == null)) {
                MainActivity.alertTextView.setText(R.string.no_results);
            } else {
                MainActivity.alertTextView.setText("");
            }
            notifyDataSetChanged();
        }
    };

    private void setMagColor(int colorId, EarthquakeViewHolder holder) {
        GradientDrawable magCircle = (GradientDrawable) holder.magTextView.getBackground();
        magCircle.setColor(colorId);
    }

    private int getMagColor(float mag) {
        int magFloor = (int) Math.floor(mag);
        int colorId;

        //Decide the color for the circle behind magnitude
        switch (magFloor) {
            case 1:
                colorId = R.color.magnitude1;
                break;
            case 2:
                colorId = R.color.magnitude2;
                break;
            case 3:
                colorId = R.color.magnitude3;
                break;
            case 4:
                colorId = R.color.magnitude4;
                break;
            case 5:
                colorId = R.color.magnitude5;
                break;
            case 6:
                colorId = R.color.magnitude6;
                break;
            case 7:
                colorId = R.color.magnitude7;
                break;
            case 8:
                colorId = R.color.magnitude8;
                break;
            case 9:
                colorId = R.color.magnitude9;
                break;
            default:
                colorId = R.color.magnitude10plus;
                break;
        }
        return ContextCompat.getColor(context, colorId);
    }

    public String[] splitLocation (String location) {
        String[] locationParts;

        if (location.contains(" of ")) {
            locationParts = location.split(" of ");
            locationParts[0] = locationParts[0] + " of";
        } else {
            locationParts = new String[2];
            locationParts[0] = "Near the";
            locationParts[1] = location;
        }

        return locationParts;
    }

    public void sortByMag() {
        Collections.sort(earthquakes, new Comparator<Earthquake>() {
            @Override
            public int compare(Earthquake o1, Earthquake o2) {
                return Float.compare(o2.getMagnitude(), o1.getMagnitude());
            }
        });
        MainActivity.earthquakesFull.clear();
        MainActivity.earthquakesFull.addAll(earthquakes);
        notifyDataSetChanged();
    }

    public void sortByLoc() {
        Collections.sort(earthquakes, new Comparator<Earthquake>() {
            @Override
            public int compare(Earthquake o1, Earthquake o2) {
                String[] locationParts1 = splitLocation(o1.getPlace());
                String[] locationParts2 = splitLocation(o2.getPlace());
                return locationParts1[1].compareTo(locationParts2[1]);
            }
        });
        MainActivity.earthquakesFull.clear();
        MainActivity.earthquakesFull.addAll(earthquakes);
        notifyDataSetChanged();
    }

    public void sortByTime() {
        MainActivity.earthquakesFull.clear();
        MainActivity.earthquakesFull.addAll(MainActivity.earthquakesOriginal);
        earthquakes.clear();
        earthquakes.addAll(MainActivity.earthquakesOriginal);
        notifyDataSetChanged();
    }

    public static class EarthquakeViewHolder extends RecyclerView.ViewHolder {

        TextView magTextView, offLocTextView, timeTextView, priLocTextView;

        public EarthquakeViewHolder(@NonNull final View itemView, final EarthquakeAdapter adapter) {
            super(itemView);

            magTextView = itemView.findViewById(R.id.list_item_mag);
            offLocTextView = itemView.findViewById(R.id.location_offset);
            priLocTextView = itemView.findViewById(R.id.location_primary);
            timeTextView = itemView.findViewById(R.id.list_item_time);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getLayoutPosition();

                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(adapter.earthquakes.get(position).getUrl()));
                    adapter.context.startActivity(browserIntent);
                }
            });
        }
    }

}