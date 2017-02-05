package com.android.soloud.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

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

        ImageView imageView;

        if (convertView == null) {
            imageView = new ImageView(context);
            imageView.setLayoutParams(new GridView.LayoutParams(160, 160));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            //imageView.setPadding(8, 8, 8, 8);
        }
        else
        {
            imageView = (ImageView) convertView;
        }
        imageView.setImageResource(imageIdArray[position]);
        return imageView;
    }
}
