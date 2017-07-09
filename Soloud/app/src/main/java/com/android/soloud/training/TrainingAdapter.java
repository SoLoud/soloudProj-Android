package com.android.soloud.training;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.soloud.R;
import com.android.soloud.models.Training;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import jp.wasabeef.picasso.transformations.BlurTransformation;

/**
 * Created by f.stamopoulos on 29/3/2017.
 */

public class TrainingAdapter extends BaseAdapter {

    private Context mContext;
    /*private Integer[] imageIdArray;*/
    private ArrayList<Training> mTrainingArrayList;

    public TrainingAdapter(Context context, ArrayList<Training> trainingArrayList){
        this.mContext = context;
        this.mTrainingArrayList = trainingArrayList;
    }

    @Override
    public int getCount() {
        return mTrainingArrayList.size();
    }

    @Override
    public Training getItem(int position) {
        return mTrainingArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder mViewHolder;

        if (convertView == null) {
            Context context = parent.getContext();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.training_item2, parent, false);
            mViewHolder = new ViewHolder();
            mViewHolder.imageView = (ImageView) convertView.findViewById(R.id.training_IV);
            mViewHolder.imageView.setImageResource(mTrainingArrayList.get(position).getImageResourceId());
            mViewHolder.lockImageView = (ImageView) convertView.findViewById(R.id.lock_IV);
            /*mViewHolder.imagesRL = (RelativeLayout) convertView.findViewById(R.id.images_RL);*/
            convertView.setTag(mViewHolder);
        }else{
            mViewHolder = (ViewHolder)convertView.getTag();
        }
        int requiredPoints = mTrainingArrayList.get(position).getRequiredPoints();
        int userPoints = 150;

        if (userPoints > requiredPoints){
            mViewHolder.lockImageView.setVisibility(View.GONE);
            Picasso.with(mContext)
                    .load(mTrainingArrayList.get(position).getImageResourceId())
                    .into(mViewHolder.imageView);
        }else{
            mViewHolder.lockImageView.setVisibility(View.VISIBLE);
            mViewHolder.lockImageView.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
            Picasso.with(mContext)
                    .load(mTrainingArrayList.get(position).getImageResourceId())
                    .transform(new BlurTransformation(mContext, 20, 4))
                    .into(mViewHolder.imageView);
        }

        mViewHolder.textView = (TextView) convertView.findViewById(R.id.training_title_TV);
        mViewHolder.textView.setText(mTrainingArrayList.get(position).getTitle());

        return convertView;

    }

    static class ViewHolder{
        ImageView imageView;
        ImageView lockImageView;
        /*RelativeLayout imagesRL;*/
        TextView textView;
    }

}

