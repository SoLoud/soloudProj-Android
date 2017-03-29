package com.android.soloud.training;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.soloud.R;
import com.android.soloud.models.Training;

import java.util.ArrayList;

/**
 * Created by f.stamopoulos on 29/3/2017.
 */

public class TrainingAdapter extends BaseAdapter {

    /*private Context context;
    private Integer[] imageIdArray;*/
    private ArrayList<Training> mTrainingArrayList;

    public TrainingAdapter(ArrayList<Training> trainingArrayList){

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
            convertView = inflater.inflate(R.layout.training_item, parent, false);
            mViewHolder = new ViewHolder();
            mViewHolder.imageView = (ImageView) convertView.findViewById(R.id.training_IV);
            mViewHolder.imageView.setImageResource(mTrainingArrayList.get(position).getImageResourceId());
            mViewHolder.lockImageView = (ImageView) convertView.findViewById(R.id.lock_IV);
            mViewHolder.imagesRL = (RelativeLayout) convertView.findViewById(R.id.images_RL);
            convertView.setTag(mViewHolder);
        }else{
            mViewHolder = (ViewHolder)convertView.getTag();
        }
        int requiredPoints = mTrainingArrayList.get(position).getRequiredPoints();
        int userPoints = 150;

        if (userPoints > requiredPoints){
            mViewHolder.lockImageView.setVisibility(View.INVISIBLE);
            mViewHolder.imageView.setVisibility(View.VISIBLE);
            mViewHolder.imagesRL.setBackgroundColor(Color.parseColor("#ffffff"));
        }else{
            mViewHolder.lockImageView.setVisibility(View.VISIBLE);
            mViewHolder.imageView.setVisibility(View.INVISIBLE);
            mViewHolder.imagesRL.setBackgroundColor(Color.parseColor("#e5e5e5"));
        }
        mViewHolder.textView = (TextView) convertView.findViewById(R.id.training_title_TV);
        mViewHolder.textView.setText(mTrainingArrayList.get(position).getTitle());

        return convertView;

    }

    static class ViewHolder{
        ImageView imageView;
        ImageView lockImageView;
        RelativeLayout imagesRL;
        TextView textView;
    }

}

