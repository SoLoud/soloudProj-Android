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
import android.widget.TextView;

import com.android.soloud.R;
import com.android.soloud.models.Contest;
import com.squareup.picasso.Target;

import org.joda.time.DateTime;

import java.util.ArrayList;

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
            holder.product_IV = (de.hdodenhof.circleimageview.CircleImageView) convertView.findViewById(R.id.product_IV);
            holder.prize_TV = (TextView) convertView.findViewById(R.id.prize_TV);
            holder.endingDate_TV = (TextView) convertView.findViewById(R.id.ending_date_TV);

            convertView.setTag(holder);
        }else{
            holder = (ContestsAdapter.ViewHolder) convertView.getTag();
        }

        Contest contestItem = mContestsList.get(position);

        // Display Company Name
        Contest.User user = contestItem.getUser();
        String companyEmail = user.getEmail();
        holder.companyName_TV.setText(companyEmail);

        // Display Company Image
        // TODO: 4/12/2016 Na to deixnw me Picasso gia na min exw problima me tis eikones sto scroll
        Contest.Photo[] photosArray = contestItem.getPhotos();
        if (photosArray.length > 0){
            String photoBase64 = photosArray[0].getContent();
            byte[] decodedString = Base64.decode(photoBase64, Base64.DEFAULT);
            Bitmap photoBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            holder.product_IV.setImageBitmap(photoBitmap);
        }else{
            holder.product_IV.setImageResource(R.drawable.ic_view_list_white_24dp);
        }

        // Display Prize Description
        String prizeDescription = contestItem.getTitle();
        holder.prize_TV.setText(prizeDescription);

        // Display Ending Date
        String endingDateISO = contestItem.getEndingAt();
        DateTime dateTime = new DateTime(endingDateISO);
        int day = dateTime.getDayOfMonth();
        int year = dateTime.getYear();
        int month = dateTime.getMonthOfYear();
        String endingDate = day + "-" + month + "-" +year;
        holder.endingDate_TV.setText(endingDate);


        //holder.product_IV.setImageResource(R.drawable.ic_arrow_back_white);
        //holder.prize_TV.setImageResource(R.drawable.ic_arrow_back_white);

        /*Picasso.with(mContext).cancelRequest(holder.product_IV);
        Picasso.with(mContext).load(photoBitmap).placeholder(R.drawable.ic_view_list_white_24dp).
                transform(new CropCircleTransformation()).
                error(R.drawable.ic_view_list_white_24dp).into(holder.product_IV);

        mTarget = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                holder.product_IV.setImageBitmap(bitmap);
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };*/

        return convertView;
    }


    private static class ViewHolder {
        TextView companyName_TV;
        de.hdodenhof.circleimageview.CircleImageView product_IV;
        TextView prize_TV;
        TextView endingDate_TV;
    }
}
