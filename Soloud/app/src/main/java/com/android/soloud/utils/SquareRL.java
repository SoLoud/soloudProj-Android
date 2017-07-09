package com.android.soloud.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * Created by f.stamopoulos on 9/7/2017.
 */

public class SquareRL extends RelativeLayout {

    public SquareRL(Context context) {
        super(context);
    }

    public SquareRL(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareRL(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec); // This is the key that will make the height equivalent to its width
    }
}
