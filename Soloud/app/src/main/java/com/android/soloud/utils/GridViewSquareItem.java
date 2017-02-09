package com.android.soloud.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by f.stamopoulos on 9/2/2017.
 */

public class GridViewSquareItem extends ImageView {

    public GridViewSquareItem(Context context) {
        super(context);
    }

    public GridViewSquareItem(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GridViewSquareItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec); // This is the key that will make the height equivalent to its width
    }
}
