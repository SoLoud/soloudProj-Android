package com.android.soloud.contests;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.soloud.R;
import com.android.soloud.ServiceGenerator;
import com.android.soloud.SoLoudApplication;
import com.android.soloud.activities.ContestDetails;
import com.android.soloud.login.LoginActivity;
import com.android.soloud.activities.MainActivity;
import com.android.soloud.login.LoginApi;
import com.android.soloud.models.Contest;
import com.android.soloud.models.CurrentState;
import com.android.soloud.models.User;
import com.android.soloud.utils.LogoutHelper;
import com.android.soloud.utils.MyStringHelper;
import com.android.soloud.utils.NetworkStatusHelper;
import com.android.soloud.utils.SharedPrefsHelper;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.github.ksoichiro.android.observablescrollview.ObservableListView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.nineoldandroids.view.ViewHelper;
import com.pnikosis.materialishprogress.ProgressWheel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.android.soloud.login.LoginActivity.FACEBOOK_PROVIDER;
import static com.android.soloud.activities.MainActivity.CATEGORY_CONTESTS_SN;
import static com.android.soloud.fragments.CategoriesFragment.CONTEST_NAME;

public class ContestsActivity extends AppCompatActivity implements ObservableScrollViewCallbacks{

    public static final String COMPANY_NAME = "CompanyName";
    public static final String CONTEST = "contest";
    public static final String CURRENT_STATE = "currentState";
    private static final String TAG = "ContestsActivity";
    private Tracker mTracker;
    //private ListView listView;
    ObservableListView listView;
    public static ArrayList<Contest> contestsList;
    private CoordinatorLayout coordinatorLayout;
    private int contestsFailureRequestsCounter;
    private CurrentState currentState;
    private String contestName;


    @BindView(R.id.progress_wheel) ProgressWheel progressWheel;

    @BindView(R.id.offline_IV) ImageView offlineIV;

    private static final float MAX_TEXT_SCALE_DELTA = 0.3f;

    private View mImageView;
    private View mOverlayView;
    private View mListBackgroundView;
    private TextView mTitleView;
    private int mActionBarSize;
    private int mFlexibleSpaceImageHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contests);

        ButterKnife.bind(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        mFlexibleSpaceImageHeight = getResources().getDimensionPixelSize(R.dimen.flexible_space_image_height);
        mActionBarSize = getActionBarHeight();
        mImageView = findViewById(R.id.image);
        mOverlayView = findViewById(R.id.overlay);
        listView = (ObservableListView) findViewById(R.id.list);
        listView.setScrollViewCallbacks(ContestsActivity.this);
        View paddingView = new View(this);
        AbsListView.LayoutParams lp = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT,
                mFlexibleSpaceImageHeight);
        paddingView.setLayoutParams(lp);

        // This is required to disable header's list selector effect
        paddingView.setClickable(true);

        listView.addHeaderView(paddingView);
        //setDummyData(listView);
        mTitleView = (TextView) findViewById(R.id.title);
        //mTitleView.setText(getTitle());
        setTitle(null);
        // mListBackgroundView makes ListView's background except header view.
        mListBackgroundView = findViewById(R.id.list_background);

        //////////////////////////////////
        if (getIntent() != null) {
            if (getIntent().getStringExtra(CONTEST_NAME) != null){
                contestName = getIntent().getStringExtra(CONTEST_NAME);
                if (!MyStringHelper.isNoE(contestName)){
                    getSupportActionBar().setTitle(contestName);
                }
            }

            if (getIntent().getSerializableExtra(CURRENT_STATE) != null){
                currentState = (CurrentState) getIntent().getSerializableExtra(CURRENT_STATE);
                if (currentState != null && !MyStringHelper.isNoE(currentState.getContestCategoryName())){
                    contestName = currentState.getContestCategoryName();
                    getSupportActionBar().setTitle(contestName);
                }
            }
            mImageView.setBackgroundResource(getImageResourceId(contestName));
        }

        contestsFailureRequestsCounter = 0;

        if (savedInstanceState != null) {

            contestName = savedInstanceState.getString(CONTEST_NAME);

            contestsList = (ArrayList<Contest>) savedInstanceState.getSerializable("contestsList");
            initializeListView();
        } else if (contestsList != null) {
            initializeListView();
        } else {
            if (NetworkStatusHelper.isNetworkAvailable(this)) {
                progressWheel.setVisibility(View.VISIBLE);
                getContestsFromBackend();
            } else {
                offlineIV.setVisibility(View.VISIBLE);
                displayFailMessage();
            }
        }


        googleAnalyticsTrack();
    }


    private int getImageResourceId(String categoryName){
        switch (categoryName){
            case "Charity":
                return R.drawable.charity;
            case "Cosmetics":
                return R.drawable.cosmetics;
            case "Home Decoration":
                return R.drawable.decoration;
            case "Entertainment":
                return R.drawable.entertainment;
            case "Fashion":
                return R.drawable.fashion;
            case "Fitness":
                return R.drawable.fitness;
            case "Food":
                return R.drawable.food;
            case "Pets":
                return R.drawable.pets;
            case "Travel":
                return R.drawable.travel;
            default:
                return R.drawable.charity;
        }
    }


    private void googleAnalyticsTrack() {
        // Obtain the shared Tracker instance.
        SoLoudApplication application = (SoLoudApplication) getApplication();
        mTracker = application.getDefaultTracker();
    }

    /*private void displayNoConnectionMessage() {
        Snackbar.make(coordinatorLayout, getResources().
                getString(R.string.error_no_internet_connection), Snackbar.LENGTH_LONG).
                setAction(R.string.retry, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (NetworkStatusHelper.isNetworkAvailable(ContestsActivity.this)) {
                            mSwipeRefreshLayout.setRefreshing(true);
                            getContestsFromBackend();
                        } else {
                            displayNoConnectionMessage();
                        }
                    }
                }).setActionTextColor(ContextCompat.getColor(this, R.color.mySecondary)).show();
    }*/

    private int getActionBarHeight(){
        // Calculate ActionBar height
        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
        {
            return TypedValue.complexToDimensionPixelSize(tv.data,getResources().getDisplayMetrics());
        }
        return 0;
    }

    private void initializeListView() {
        listView.setAdapter(new ContestsAdapter1(ContestsActivity.this, contestsList));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Contest contest = contestsList.get(position-1);

                Contest.User user = contest.getmUser();
                String companyName = user.getmUserName();

                Intent intent = new Intent(ContestsActivity.this, ContestDetails.class);
                intent.putExtra(CONTEST, contest);
                currentState = new CurrentState(null, companyName, null, null, contestName);
                intent.putExtra(CURRENT_STATE, currentState);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        outState.putSerializable("contestsList", contestsList);

        outState.putString(CONTEST_NAME, contestName);

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.i(TAG, "Setting screen name: " + CATEGORY_CONTESTS_SN);
        mTracker.setScreenName("Screen: " + CATEGORY_CONTESTS_SN);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }


/*    private void getContestsRX() {
        // Retrofit instance which was created earlier
        ContestsApi contestsApi = ServiceGenerator.createService(ContestsApi.class);
        // Return type as defined in TwitterApi interface
        String soLoudToken = "Bearer " + SharedPrefsHelper.getFromPrefs(this, SharedPrefsHelper.SOLOUD_TOKEN);
        //Observable<List<Contest>> ob = contestsApi.getContestsRX(soLoudToken);

        contestsApi.getContestsRX(soLoudToken)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleResults, this::handleError );
    }


    private void handleError(Throwable throwable) {

    }

    private void handleResults(List<Contest> contests) {

        if (contests != null) {
            Log.d(TAG, "handleResults: " + contests.toString());
        }
    }*/



    private void getContestsFromBackend() {

        // Create a very simple REST adapter which points the API endpoint.
        ContestsApi client = ServiceGenerator.createService(ContestsApi.class);

        // Fetch the Contests.
        String soLoudToken = "Bearer " + SharedPrefsHelper.getFromPrefs(this, SharedPrefsHelper.SOLOUD_TOKEN);
        Call<List<Contest>> call = client.getContests(soLoudToken);
        Callback<List<Contest>> contestsCallback = new Callback<List<Contest>>() {
            @Override
            public void onResponse(Call<List<Contest>> call, Response<List<Contest>> response) {
                if (response.isSuccessful()) {
                    contestsList = (ArrayList<Contest>) response.body();
                    progressWheel.setVisibility(View.GONE);
                    initializeListView();
                } else {
                    // error response, no access to resource?
                    if (response.code() == 401){
                        progressWheel.setVisibility(View.GONE);
                        LogoutHelper logoutHelper = new LogoutHelper(ContestsActivity.this);
                        logoutHelper.logOut();
                    }else{
                        handleResponseFailure(call);
                    }
                }

            }

            @Override
            public void onFailure(Call<List<Contest>> call, Throwable t) {
                // something went completely south (like no internet connection)
                //Log.d(TAG, "Error getting contests: " + t.getMessage());
                handleResponseFailure(call);
            }

            private void handleResponseFailure(Call<List<Contest>> call) {
                // Try 3 times to login
                contestsFailureRequestsCounter++;
                if (contestsFailureRequestsCounter < 3) {
                    // Request reuse
                    Call<List<Contest>> newCall = call.clone();
                    newCall.enqueue(this);
                } else {
                    progressWheel.setVisibility(View.GONE);
                    if (!NetworkStatusHelper.isNetworkAvailable(ContestsActivity.this)){
                        offlineIV.setVisibility(View.VISIBLE);
                    }
                    displayFailMessage();
                    //Snackbar.make(coordinatorLayout, R.string.error_requesting_contests, Snackbar.LENGTH_LONG).show();
                }
            }
        };
        call.enqueue(contestsCallback);
    }


    private void loginToBackend(String facebookToken) {
        // Create a very simple REST adapter which points the API endpoint.
        LoginApi client = ServiceGenerator.createService(LoginApi.class);

        // Post the user's Facebook Token
        Call<User> call = client.login(FACEBOOK_PROVIDER, facebookToken, "password");
        Callback<User> loginCallback = new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    User soLoudUser = response.body();
                    String soLoudToken = soLoudUser.getSoloudToken();
                    SharedPrefsHelper.storeInPrefs(ContestsActivity.this, soLoudToken, SharedPrefsHelper.SOLOUD_TOKEN);
                    getContestsFromBackend();

                } else {
                    // error response, no access to resource?
                    //Log.d(TAG, "Backend login error in response: " + response.toString());
                    logout();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                logout();
            }
        };
        call.enqueue(loginCallback);
    }

    private void logout(){
        String[] prefsToDelete = {SharedPrefsHelper.USER_FB_ID, SharedPrefsHelper.FB_TOKEN};
        SharedPrefsHelper.deleteFromPrefs(ContestsActivity.this, prefsToDelete);

        FacebookSdk.sdkInitialize(getApplicationContext());
        LoginManager.getInstance().logOut();
        Intent intent = new Intent(ContestsActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        // Translate overlay and image
        float flexibleRange = mFlexibleSpaceImageHeight - mActionBarSize;
        int minOverlayTransitionY = mActionBarSize - mOverlayView.getHeight();
        ViewHelper.setTranslationY(mOverlayView, ScrollUtils.getFloat(-scrollY, minOverlayTransitionY, 0));
        ViewHelper.setTranslationY(mImageView, ScrollUtils.getFloat(-scrollY / 2, minOverlayTransitionY, 0));

        // Translate list background
        ViewHelper.setTranslationY(mListBackgroundView, Math.max(0, -scrollY + mFlexibleSpaceImageHeight));

        // Change alpha of overlay
        ViewHelper.setAlpha(mOverlayView, ScrollUtils.getFloat((float) scrollY / flexibleRange, 0, 1));

        // Scale title text
        scaleTitleText(scrollY, flexibleRange);
    }

    private void scaleTitleText(int scrollY, float flexibleRange) {
        float scale = 1 + ScrollUtils.getFloat((flexibleRange - scrollY) / flexibleRange, 0, MAX_TEXT_SCALE_DELTA);
        setPivotXToTitle();
        ViewHelper.setPivotY(mTitleView, 0);
        ViewHelper.setScaleX(mTitleView, scale);
        ViewHelper.setScaleY(mTitleView, scale);

        // Translate title text
        int maxTitleTranslationY = (int) (mFlexibleSpaceImageHeight - mTitleView.getHeight() * scale);
        int titleTranslationY = maxTitleTranslationY - scrollY;
        ViewHelper.setTranslationY(mTitleView, titleTranslationY);
    }

    @Override
    public void onDownMotionEvent() {

    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {

    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void setPivotXToTitle() {
        Configuration config = getResources().getConfiguration();
        if (Build.VERSION_CODES.JELLY_BEAN_MR1 <= Build.VERSION.SDK_INT
                && config.getLayoutDirection() == View.LAYOUT_DIRECTION_RTL) {
            ViewHelper.setPivotX(mTitleView, findViewById(android.R.id.content).getWidth());
        } else {
            ViewHelper.setPivotX(mTitleView, 0);
        }
    }

    private void displayFailMessage(){
        Snackbar mySnackbar = Snackbar.make(findViewById(R.id.coordinatorLayout),
                R.string.error_requesting_contests, Snackbar.LENGTH_INDEFINITE);
        mySnackbar.setAction(R.string.retry, onClickListener);
        mySnackbar.show();
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            offlineIV.setVisibility(View.GONE);
            progressWheel.setVisibility(View.VISIBLE);
            getContestsFromBackend();
        }
    };


}
