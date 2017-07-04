package com.android.soloud.wizard;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;

import com.android.soloud.R;


import static com.android.soloud.wizard.DefaultIndicatorController.DEFAULT_COLOR;

/**
 * Created by f.stamopoulos on 1/7/2017.
 */

public class WizardActivity extends AppCompatActivity {

    private static final int NUM_PAGES = 4;
    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;
    private IndicatorController mController;
    protected int selectedIndicatorColor = DEFAULT_COLOR;
    protected int unselectedIndicatorColor = DEFAULT_COLOR;
    private int currentlySelectedItem = -1;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wizard_activity);

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.setPageTransformer(true, new ViewPageTransformer(ViewPageTransformer.TransformType.FADE));


        mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageScrollStateChanged(int state) {
            }

            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            public void onPageSelected(int position) {
                // Check if this is the page you want.
                mController.selectPosition(position);
                currentlySelectedItem = position;
            }
        });
    }


    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        initController();
        mController.selectPosition(0);
    }

    private class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int pos) {
            switch(pos) {
                case 0:
                    return SlideFragment.newInstance(R.drawable.categories_screenshot, getString(R.string.wizard_0_description), R.color.mySecondary, R.drawable.ic_navigate_next_white, false);
                case 1:
                    return SlideFragment.newInstance(R.drawable.contests_screenshot, getString(R.string.wizard_1_description), R.color.dark_grey, R.drawable.ic_navigate_next_white, false);
                case 2:
                    return SlideFragment.newInstance(R.drawable.add_photo_screenshot, getString(R.string.wizard_2_description), R.color.mySecondary, R.drawable.ic_navigate_next_white, false);
                case 3:
                    return SlideFragment.newInstance(R.drawable.add_tags_screenshot, getString(R.string.wizard_3_description), R.color.medium_grey, R.drawable.ic_check_white_24dp, true);
                default:
                    return SlideFragment.newInstance(R.drawable.categories_screenshot, getString(R.string.wizard_0_description), R.color.mySecondary, R.drawable.ic_navigate_next_white, false);
            }
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }



    private void initController() {
        if (mController == null)
            mController = new DefaultIndicatorController();

        FrameLayout indicatorContainer = (FrameLayout) findViewById(R.id.indicator_container);
        indicatorContainer.addView(mController.newInstance(this));

        mController.initialize(NUM_PAGES);
        if (selectedIndicatorColor != DEFAULT_COLOR)
            mController.setSelectedIndicatorColor(selectedIndicatorColor);
        if (unselectedIndicatorColor != DEFAULT_COLOR)
            mController.setUnselectedIndicatorColor(unselectedIndicatorColor);

        mController.selectPosition(currentlySelectedItem);
    }


}
