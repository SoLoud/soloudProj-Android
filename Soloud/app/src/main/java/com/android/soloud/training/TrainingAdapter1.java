package com.android.soloud.training;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.soloud.R;
import com.android.soloud.models.Training;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import jp.wasabeef.picasso.transformations.BlurTransformation;

/**
 * Created by f.stamopoulos on 9/7/2017.
 */

public class TrainingAdapter1 extends BaseAdapter {

    private Context mContext;
    private ArrayList<Training> mTrainingArrayList;
    /*private ImageView imageView;
    private ImageView lockImageView;
    private TextView textView;*/
    private LayoutInflater inflater;

    public TrainingAdapter1(@NonNull Context context, @NonNull ArrayList<Training> trainingArrayList) {
        this.mContext = context;
        this.mTrainingArrayList = trainingArrayList;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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

        ViewHolder holder;

        //View grid;

        if (convertView == null) {
            //grid = new View(mContext);
            convertView = inflater.inflate(R.layout.training_item3, null);
            holder = new ViewHolder();
            //SquareRL relativeLayout = (SquareRL)grid.findViewById(R.id.square_item_RL);
            holder.imageView = (ImageView) convertView.findViewById(R.id.training_IV);
            holder.lockImageView = (ImageView) convertView.findViewById(R.id.lock_IV);
            holder.textView = (TextView) convertView.findViewById(R.id.training_title_TV);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.imageView.setImageResource(mTrainingArrayList.get(position).getImageResourceId());

        int requiredPoints = mTrainingArrayList.get(position).getRequiredPoints();
        int userPoints = 150;

        if (userPoints > requiredPoints){
            holder.lockImageView.setVisibility(View.GONE);
            Picasso.with(mContext)
                    .load(mTrainingArrayList.get(position).getImageResourceId())
                    .into(holder.imageView);
        }else{
            holder.lockImageView.setVisibility(View.VISIBLE);
            holder.lockImageView.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
            Picasso.with(mContext)
                    .load(mTrainingArrayList.get(position).getImageResourceId())
                    .transform(new BlurTransformation(mContext, 20, 4))
                    .into(holder.imageView);
        }


        holder.textView.setText(mTrainingArrayList.get(position).getTitle());

        return convertView;
    }

    private static class ViewHolder{
        ImageView imageView;
        ImageView lockImageView;
        TextView textView;
    }

}
