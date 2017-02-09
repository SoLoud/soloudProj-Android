package com.android.soloud.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.android.soloud.R;
import com.android.soloud.utils.GridViewSquareItem;

/**
 * Created by f.stamopoulos on 5/2/2017.
 */

public class GalleryAdapter extends BaseAdapter{

    private Context context;
    private Integer[] imageIdArray;

    public GalleryAdapter(Context context, Integer[] imageIdArray){
        this.context = context;
        this.imageIdArray = imageIdArray;
    }

    @Override
    public int getCount() {
        return imageIdArray.length;
    }

    @Override
    public Object getItem(int position) {
        return imageIdArray[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View grid;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {

            grid = new View(context);
            grid = inflater.inflate(R.layout.gallery_item, null);
            GridViewSquareItem imageView = (GridViewSquareItem)grid.findViewById(R.id.square_item_image);
            imageView.setImageResource(imageIdArray[position]);
        } else {
            grid = (View) convertView;
        }

        return grid;

        //ImageView imageView;
        /*GridViewItem imageView;

        if (convertView == null) {
            imageView = new GridViewItem(context);
            //imageView = new ImageView(context);
            *//*imageView.setLayoutParams(new GridView.LayoutParams(160, 160));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);*//*
            //imageView.setPadding(8, 8, 8, 8);
        }
        else
        {
            imageView = (GridViewItem) convertView;
        }
        imageView.setImageResource(imageIdArray[position]);
        return imageView;*/
    }
}
