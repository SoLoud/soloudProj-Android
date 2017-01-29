package com.android.soloud.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.soloud.R;
import com.android.soloud.models.Contest;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.joda.time.DateTime;

import java.util.ArrayList;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

/**
 * Created by f.stamopoulos on 28/10/2016.
 */

public class ContestsAdapter extends ArrayAdapter {

    private Context mContext;
    private LayoutInflater layoutInflater;
    private ArrayList<Contest> mContestsList;

    private Target mTarget;

    public ContestsAdapter(Context context, ArrayList<Contest> contestsList) {
        super(context, R.layout.contest_row_card, contestsList);
        layoutInflater = LayoutInflater.from(context);
        mContext = context;
        mContestsList = contestsList;
    }


    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ContestsAdapter.ViewHolder holder;
        if (convertView == null) {
            if (layoutInflater == null)
                layoutInflater = (LayoutInflater) mContext
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.contest_row_card, parent, false);
            holder = new ContestsAdapter.ViewHolder();

            holder.companyName_TV = (TextView) convertView.findViewById(R.id.companyName_TV);
            holder.product_IV = (ImageView) convertView.findViewById(R.id.product_IV);
            holder.prize_TV = (TextView) convertView.findViewById(R.id.prize_TV);
            holder.endingDate_TV = (TextView) convertView.findViewById(R.id.ending_date_TV);

            convertView.setTag(holder);
        }else{
            holder = (ContestsAdapter.ViewHolder) convertView.getTag();
        }

        Contest contestItem = mContestsList.get(position);

        // Display Company Name
        Contest.User user = contestItem.getmUser();
        String companyEmail = user.getmUserName();
        holder.companyName_TV.setText(companyEmail);

        // Display Company Image
        Picasso.with(mContext).cancelRequest(holder.product_IV);

        Contest.Photo[] photosArray = contestItem.getmProductPhotos();
        if (photosArray != null && photosArray.length > 0){

            String photoUrl = photosArray[0].getmUrl();
            Picasso.with(mContext).load(photoUrl).placeholder(R.drawable.ic_view_list_white_24dp).
                    transform(new CropCircleTransformation()).
                    error(R.drawable.ic_view_list_white_24dp).into(holder.product_IV);
        }else{
            Picasso.with(mContext).cancelRequest(holder.product_IV);
            holder.product_IV.setImageResource(R.drawable.bazo);
        }

        // Display Prize Description
        String prizeDescription = contestItem.getmTitle();
        holder.prize_TV.setText(prizeDescription);

        // Display Ending Date
        String endingDateISO = contestItem.getmEndingAt();
        DateTime dateTime = new DateTime(endingDateISO);
        int day = dateTime.getDayOfMonth();
        int year = dateTime.getYear();
        int month = dateTime.getMonthOfYear();
        String endingDate = day + "-" + month + "-" +year;
        holder.endingDate_TV.setText(endingDate);

        return convertView;
    }


    private static class ViewHolder {
        TextView companyName_TV;
        ImageView product_IV;
        TextView prize_TV;
        TextView endingDate_TV;
    }
}
