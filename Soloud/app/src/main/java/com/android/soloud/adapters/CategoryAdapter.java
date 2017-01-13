package com.android.soloud.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.soloud.R;
import com.android.soloud.models.Category;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import jp.wasabeef.picasso.transformations.BlurTransformation;

/**
 * Created by f.stamopoulos on 23/10/2016.
 */

public class CategoryAdapter extends ArrayAdapter {

    private Context mContext;
    private LayoutInflater layoutInflater;
    private ArrayList<Category> mCategoriesList;

    public CategoryAdapter(Context context, ArrayList<Category> categoriesList) {
        super(context, R.layout.category_row, categoriesList);
        layoutInflater = LayoutInflater.from(context);
        mContext = context;
        mCategoriesList = categoriesList;
    }


    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;
        if (convertView == null) {
            if (layoutInflater == null)
                layoutInflater = (LayoutInflater) mContext
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.category_row, parent, false);
            holder = new ViewHolder();

            holder.category_IV = (ImageView) convertView.findViewById(R.id.category_IV);
            holder.category_TV = (TextView) convertView.findViewById(R.id.categories_TV);

            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        Category item = mCategoriesList.get(position);

        holder.category_TV.setText(item.getName());

        Picasso.with(mContext).cancelRequest(holder.category_IV);
        Picasso.with(mContext).load(item.getImageResourceId()).
                transform(new BlurTransformation(mContext, 7)).into(holder.category_IV);

        return convertView;
    }


    private static class ViewHolder {
        private TextView category_TV;
        private ImageView category_IV;
    }
}
