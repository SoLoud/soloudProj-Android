/**
 * Copyright (c) 2014-present, Facebook, Inc. All rights reserved.
 *
 * You are hereby granted a non-exclusive, worldwide, royalty-free license to use,
 * copy, modify, and distribute this software in source code or binary form for use
 * in connection with the web services and APIs provided by Facebook.
 *
 * As with any software that integrates with the Facebook platform, your use of
 * this software is subject to the Facebook Developer Principles and Policies
 * [http://developers.facebook.com/policy/]. This copyright notice shall be
 * included in all copies or substantial portions of the software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.android.soloud.facebookPlaces.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.soloud.R;
import com.android.soloud.facebookPlaces.model.Place;
import com.android.soloud.facebookPlaces.model.PlaceTextUtils;

import java.util.List;

public class PlaceListAdapter extends RecyclerView.Adapter<PlaceListAdapter.PlaceViewHolder> {

    private List<Place> places;
    private Listener listener;
    private int layoutId;
    private int selectedIndex = -1;

    public interface Listener {
        void onPlaceSelected(Place place);
    }

    public class PlaceViewHolder extends RecyclerView.ViewHolder {
        private View container;
        private TextView placeNameTextView;
        private TextView placeAddressTextView;
        private Place currentPlace;
        private ImageView checkIV;

        public PlaceViewHolder(View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.place_container);
            placeNameTextView = (TextView) itemView.findViewById(R.id.place_name);
            placeAddressTextView = (TextView) itemView.findViewById(R.id.place_address);
            checkIV = (ImageView) itemView.findViewById(R.id.check_IV);
            container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = places.indexOf(currentPlace);
                    selectedIndex = position;
                    //Log.d("PlaceListAdapter", "onClick position: "+ position);
                    listener.onPlaceSelected(currentPlace);
                }
            });
        }

        void refresh(Place place) {
            this.currentPlace = place;
            placeNameTextView.setText(place.get(Place.NAME));
            placeAddressTextView.setText(PlaceTextUtils.getAddress(place));
        }
    }

    public PlaceListAdapter(int layoutId, List<Place> places, Listener listener) {
        this.layoutId = layoutId;
        this.places = places;
        this.listener = listener;
        this.selectedIndex = -1;
    }

    @Override
    public PlaceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutId, parent, false);
        PlaceViewHolder viewHolder = new PlaceViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(PlaceViewHolder holder, int position) {
        Place place = places.get(position);
        holder.refresh(place);
        if (position == selectedIndex) {
            holder.checkIV.setVisibility(View.VISIBLE);
        } else {
            holder.checkIV.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return places.size();
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }
}
