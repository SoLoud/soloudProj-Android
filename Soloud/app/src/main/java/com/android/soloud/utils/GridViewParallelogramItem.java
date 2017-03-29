package com.android.soloud.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by f.stamopoulos on 29/3/2017.
 */

public class GridViewParallelogramItem extends ImageView {

    public GridViewParallelogramItem(Context context) {
        super(context);
    }

    public GridViewParallelogramItem(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GridViewParallelogramItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec + widthMeasureSpec/2); // This is the key that will make the height equivalent to its width
    }

}
