package com.android.soloud.profile;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.android.soloud.R;
import com.android.soloud.SoLoudApplication;
import com.android.soloud.utils.SharedPrefsHelper;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;

import static com.android.soloud.activities.MainActivity.USER_PROFILE_SN;
import static com.github.mikephil.charting.utils.ColorTemplate.rgb;

public class UserProfileActivity extends AppCompatActivity {

    private static final String TAG = "UserProfileActivity";
    private Tracker mTracker;

    //private PieChart mChart;
    private PieChart pieChart1;
    private PieChart pieChart2;
    private boolean currentContestExpanded = false;
    private int[] MATERIAL_COLORS;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        ImageButton expand_Btn = (ImageButton) findViewById(R.id.expand_Btn);
        expand_Btn.setOnClickListener(onClickListener);
        de.hdodenhof.circleimageview.CircleImageView  user_profile_IV = (CircleImageView) findViewById(R.id.user_profile_IV);
        TextView userName_TV = (TextView) findViewById(R.id.userName);
        String userName = SharedPrefsHelper.getFromPrefs(this, SharedPrefsHelper.USER_NAME);
        if (userName != null && !userName.isEmpty()){
            userName_TV.setText(userName);
        }else{
            userName_TV.setText("User Name");
        }

        int primaryColor = ContextCompat.getColor(this, R.color.colorPrimary);
        int secondaryColor = ContextCompat.getColor(this, R.color.mySecondary);
        int greyColor = ContextCompat.getColor(this, R.color.colorAccent);
        MATERIAL_COLORS = new int[]{
                primaryColor, secondaryColor, greyColor
        };

        RoundCornerProgressBar progressBar = (RoundCornerProgressBar) findViewById(R.id.userProgressBar);

        initializeChart1();
        initializeChart2();

        Picasso.with(this).load(SharedPrefsHelper.getFromPrefs(this, SharedPrefsHelper.USER_PROFILE_PICTURE_URL)).
                transform(new CropCircleTransformation()).into(user_profile_IV);

        // Obtain the shared Tracker instance.
        SoLoudApplication application = (SoLoudApplication) getApplication();
        mTracker = application.getDefaultTracker();
    }

    /*private String getUserNameFromPrefs(){
        SharedPreferences sharedPref = getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        return  sharedPref.getString(USER_NAME, null);
    }*/

    private void initializeChart1() {
        pieChart1 = (PieChart) findViewById(R.id.chart1);
        // creating data values
        ArrayList entries = new ArrayList<>();
        entries.add(new PieEntry(4f, "Likes"));
        entries.add(new PieEntry(8f, "Shares"));
        entries.add(new PieEntry(6f, "Comments"));

        PieDataSet dataset = new PieDataSet(entries, "");
        dataset.setColors(MATERIAL_COLORS); // set the color

        PieData data = new PieData(dataset); // initialize Piedata
        pieChart1.setData(data); // set data into chart

        pieChart1.setDrawEntryLabels(true);
        pieChart1.setEntryLabelColor(Color.BLACK);
        pieChart1.setEntryLabelTextSize(12f);
        //pieChart1.setUsePercentValues(true);
        pieChart1.setDrawHoleEnabled(true);
        pieChart1.setHoleColor(Color.WHITE);
        pieChart1.setTransparentCircleColor(Color.WHITE);
        pieChart1.setTransparentCircleAlpha(110);
        pieChart1.setHoleRadius(58f);
        pieChart1.setTransparentCircleRadius(61f);
        pieChart1.setDrawCenterText(true);
        pieChart1.setRotationAngle(0);
        // enable rotation of the chart by touch
        pieChart1.setRotationEnabled(true);
        pieChart1.setHighlightPerTapEnabled(true);
        pieChart1.getDescription().setEnabled(false);


        /*Legend l = pieChart1.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(0f);
        l.setYOffset(0f);*/
    }

    private void initializeChart2() {
        pieChart2 = (PieChart) findViewById(R.id.chart2);
        // creating data values
        ArrayList entries = new ArrayList<>();
        entries.add(new PieEntry(4f, "Likes"));
        entries.add(new PieEntry(8f, "Shares"));
        entries.add(new PieEntry(6f, "Comments"));


        PieDataSet dataset = new PieDataSet(entries, "");
        dataset.setColors(MATERIAL_COLORS); // set the color

        PieData data = new PieData(dataset); // initialize Piedata
        pieChart2.setData(data); // set data into chart

        pieChart2.setDrawEntryLabels(true);
        pieChart2.setEntryLabelColor(Color.BLACK);
        pieChart2.setEntryLabelTextSize(12f);
        //pieChart2.setUsePercentValues(true);
        pieChart2.setDrawHoleEnabled(true);
        pieChart2.setHoleColor(Color.WHITE);
        pieChart2.setTransparentCircleColor(Color.WHITE);
        pieChart2.setTransparentCircleAlpha(110);
        pieChart2.setHoleRadius(58f);
        pieChart2.setTransparentCircleRadius(61f);
        pieChart2.setDrawCenterText(true);
        pieChart2.setRotationAngle(0);
        // enable rotation of the chart by touch
        pieChart2.setRotationEnabled(true);
        pieChart2.setHighlightPerTapEnabled(true);
        pieChart2.getDescription().setEnabled(false);
    }


    @Override
    protected void onResume() {
        super.onResume();

        Log.i(TAG, "Setting screen name: " + USER_PROFILE_SN);
        mTracker.setScreenName("Screen: " + USER_PROFILE_SN);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }


    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.expand_Btn:
                    if (!currentContestExpanded){
                        pieChart1.setVisibility(View.VISIBLE);
                    /*v.startAnimation(
                            AnimationUtils.loadAnimation(UserProfileActivity.this, R.anim.rotate_180) );*/
                        v.animate().rotation(180).start();
                        currentContestExpanded = true;
                    }else{
                        pieChart1.setVisibility(View.GONE);
                        v.animate().rotation(360).start();
                        currentContestExpanded = false;
                    }
                    break;
            }
        }
    };

    public static void expand(final View v) {
        v.measure(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        final int targetHeight = v.getMeasuredHeight();

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.getLayoutParams().height = 1;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? LinearLayout.LayoutParams.WRAP_CONTENT
                        : (int)(targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int)(targetHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    public static void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if(interpolatedTime == 1){
                    v.setVisibility(View.GONE);
                }else{
                    v.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int)(initialHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

}
