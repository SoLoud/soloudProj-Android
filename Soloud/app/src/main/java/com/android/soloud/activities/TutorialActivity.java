package com.android.soloud.activities;

import android.os.Bundle;
import android.util.Log;

import com.android.soloud.R;
import com.android.soloud.SoLoudApplication;
import com.android.soloud.utils.SampleSlide;
import com.github.paolorotolo.appintro.AppIntro2;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import static com.android.soloud.activities.MainActivity.TUTORIAL_SN;

public class TutorialActivity extends AppIntro2 {

    private static final String TAG = "TutorialActivity";
    private Tracker mTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addSlide(SampleSlide.newInstance(R.layout.tutorial_slide_1));
        addSlide(SampleSlide.newInstance(R.layout.tutorial_slide_2));
        addSlide(SampleSlide.newInstance(R.layout.tutorial_slide_3));
        addSlide(SampleSlide.newInstance(R.layout.tutorial_slide_4));

        setFadeAnimation();

        showSkipButton(false);

        // Obtain the shared Tracker instance.
        SoLoudApplication application = (SoLoudApplication) getApplication();
        mTracker = application.getDefaultTracker();
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.i(TAG, "Setting screen name: " + TUTORIAL_SN);
        mTracker.setScreenName("Screen: " + TUTORIAL_SN);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }


    @Override
    public void onSkipPressed() {
        // Do something when users tap on Skip button.
    }

    @Override
    public void onNextPressed() {
        // Do something when users tap on Next button.
    }

    @Override
    public void onDonePressed() {
        // Do something when users tap on Done button.
        finish();
    }

    @Override
    public void onSlideChanged() {
        // Do something when slide is changed
    }

}
