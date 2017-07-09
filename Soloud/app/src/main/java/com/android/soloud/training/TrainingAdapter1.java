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
import com.android.soloud.utils.GridViewSquareItem;
import com.android.soloud.utils.SquareRL;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import jp.wasabeef.picasso.transformations.BlurTransformation;

/**
 * Created by f.stamopoulos on 9/7/2017.
 */

public class TrainingAdapter1 extends BaseAdapter {

    private Context mContext;
    private ArrayList<Training> mTrainingArrayList;
    private ImageView imageView;
    private ImageView lockImageView;
    private TextView textView;

    public TrainingAdapter1(@NonNull Context context, @NonNull ArrayList<Training> trainingArrayList) {
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
        View grid;
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {

            grid = new View(mContext);
            grid = inflater.inflate(R.layout.training_item3, null);
            //SquareRL relativeLayout = (SquareRL)grid.findViewById(R.id.square_item_RL);
            imageView = (ImageView) grid.findViewById(R.id.training_IV);
            lockImageView = (ImageView) grid.findViewById(R.id.lock_IV);
            textView = (TextView) grid.findViewById(R.id.training_title_TV);

        } else {
            grid = (View) convertView;
        }

        imageView.setImageResource(mTrainingArrayList.get(position).getImageResourceId());

        int requiredPoints = mTrainingArrayList.get(position).getRequiredPoints();
        int userPoints = 150;

        if (userPoints > requiredPoints){
            lockImageView.setVisibility(View.GONE);
            Picasso.with(mContext)
                    .load(mTrainingArrayList.get(position).getImageResourceId())
                    .into(imageView);
        }else{
            lockImageView.setVisibility(View.VISIBLE);
            lockImageView.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
            Picasso.with(mContext)
                    .load(mTrainingArrayList.get(position).getImageResourceId())
                    .transform(new BlurTransformation(mContext, 20, 4))
                    .into(imageView);
        }


        textView.setText(mTrainingArrayList.get(position).getTitle());

        return grid;
    }

}
