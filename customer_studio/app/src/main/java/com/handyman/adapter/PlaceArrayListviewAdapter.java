package com.handyman.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.handyman.R;
import com.handyman.logger.Logger;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;

public class PlaceArrayListviewAdapter extends ArrayAdapter<PlaceArrayListviewAdapter.PlaceAutocomplete> implements Filterable {
    private static final String TAG = "PlaceArrayAdapter";
    private GoogleApiClient mGoogleApiClient;
    private AutocompleteFilter mPlaceFilter;
    private LatLngBounds mBounds;
    private ArrayList<PlaceAutocomplete> mResultList = new ArrayList<PlaceAutocomplete>();
    Context context;
    int layoutResourceId;

    /**
     * Constructor
     *
     * @param context  Context
     * @param layoutResourceId Layout resource
     * @param bounds   Used to specify the search bounds
     * @param filter   Used to specify place types
     */
    public PlaceArrayListviewAdapter(Context context, int layoutResourceId, LatLngBounds bounds, AutocompleteFilter filter) {
        super(context, layoutResourceId);
        this.context = context;
        this.layoutResourceId= layoutResourceId;
        this.mBounds = bounds;
        this.mPlaceFilter = filter;
//        this.mResultList = new ArrayList<PlaceAutocomplete>();
    }

    public void setGoogleApiClient(GoogleApiClient googleApiClient) {
        if (googleApiClient == null || !googleApiClient.isConnected()) {
            mGoogleApiClient = null;
        } else {
            mGoogleApiClient = googleApiClient;
        }
    }

    @Override
    public int getCount() {
        int size = 0;
        if(mResultList != null && !mResultList.isEmpty()){
            size = mResultList.size();
        }

        return size;
    }

    @Override
    public PlaceAutocomplete getItem(int position) {
        return mResultList.get(position);
    }

    private ArrayList<PlaceAutocomplete> getPredictions(CharSequence constraint) {
        if (mGoogleApiClient != null) {
            Log.i(TAG, "Executing autocomplete query for: " + constraint);
            PendingResult<AutocompletePredictionBuffer> results =
                    Places.GeoDataApi.getAutocompletePredictions(mGoogleApiClient, constraint.toString(), mBounds, mPlaceFilter);
            // Wait for predictions, set the timeout.
            AutocompletePredictionBuffer autocompletePredictions = results
                    .await(60, TimeUnit.SECONDS);
            final Status status = autocompletePredictions.getStatus();
            if (!status.isSuccess()) {
//                Toast.makeText(getContext(), "Error: " + status.toString(),
//                        Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Error getting place predictions: " + status
                        .toString());
                autocompletePredictions.release();
                return null;
            }

            Log.i(TAG, "Query completed. Received " + autocompletePredictions.getCount()
                    + " predictions.");
            Iterator<AutocompletePrediction> iterator = autocompletePredictions.iterator();
            ArrayList resultList = new ArrayList<>(autocompletePredictions.getCount());

            while (iterator.hasNext()) {
                AutocompletePrediction prediction = iterator.next();
                resultList.add(new PlaceAutocomplete(prediction.getPlaceId(),prediction.getFullText(null),prediction.getPrimaryText(null),prediction.getSecondaryText(null)));
                // getFullText(null) use getDescription() depricated methord when use 9.4 google api.
            }
            // Buffer release
            autocompletePredictions.release();
            return resultList;
        }
        Log.e(TAG, "Google API client is not connected.");
        return null;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                if (constraint != null) {
                    // Query the autocomplete API for the entered constraint
                    mResultList = getPredictions(constraint);
                    if (mResultList != null) {
                        // Results
                        results.values = mResultList;
                        results.count = mResultList.size();
                    }
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    // The API returned at least one result, update the data.
                    notifyDataSetChanged();
                } else {
                    // The API did not return any results, invalidate the data set.
                    notifyDataSetInvalidated();
                }
            }
        };
        return filter;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        Holder holder = null;

        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new Holder();
            holder.imgMapIcon = (ImageView)row.findViewById(R.id.imgMapIcon);
            holder.txtAreaName = (TextView)row.findViewById(R.id.txtAreaName);
            holder.txtExtraText = (TextView)row.findViewById(R.id.txtExtraText);

            row.setTag(holder);
        }
        else
        {
            holder = (Holder)row.getTag();
        }

        if(mResultList.size() > 0 && mResultList != null){

            String CurrentString = String.valueOf(mResultList.get(position).description);

            holder.txtAreaName.setText(mResultList.get(position).primarytext);
            holder.txtExtraText.setText(mResultList.get(position).secondorytext);
        }

        return row;

    }

    static class Holder
    {
        ImageView imgMapIcon;
        TextView txtAreaName,txtExtraText;
    }

    public class PlaceAutocomplete {

        public CharSequence placeId;
        public CharSequence description;
        public CharSequence primarytext;
        public CharSequence secondorytext;

        PlaceAutocomplete(CharSequence placeId, CharSequence description, CharSequence primarytext, CharSequence secondorytext) {
            this.placeId = placeId;
            this.description = description;
            this.primarytext = primarytext;
            this.secondorytext = secondorytext;
        }

        @Override
        public String toString() {
            return description.toString() + " :: " + primarytext.toString() + " :: " + secondorytext.toString();
        }
    }
}
