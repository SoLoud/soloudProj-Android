package com.android.soloud.contests;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.soloud.R;
import com.android.soloud.ServiceGenerator;
import com.android.soloud.SoLoudApplication;
import com.android.soloud.activities.ContestDetails;
import com.android.soloud.activities.LoginActivity;
import com.android.soloud.activities.MainActivity;
import com.android.soloud.apiCalls.ContestsService;
import com.android.soloud.apiCalls.LoginService;
import com.android.soloud.models.Contest;
import com.android.soloud.models.CurrentState;
import com.android.soloud.models.User;
import com.android.soloud.utils.LogoutHelper;
import com.android.soloud.utils.MyStringHelper;
import com.android.soloud.utils.NetworkStatusHelper;
import com.android.soloud.utils.SharedPrefsHelper;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.android.soloud.activities.LoginActivity.FACEBOOK_PROVIDER;
import static com.android.soloud.activities.MainActivity.CATEGORY_CONTESTS_SN;
import static com.android.soloud.fragments.CategoriesFragment.CONTEST_NAME;

public class ContestsActivity extends AppCompatActivity {

    public static final String COMPANY_NAME = "CompanyName";
    public static final String CONTEST = "contest";
    public static final String CURRENT_STATE = "currentState";
    private static final String TAG = "ContestsActivity";
    private Tracker mTracker;
    private ListView listView;
    public static ArrayList<Contest> contestsList;
    private CoordinatorLayout coordinatorLayout;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private int contestsFailureRequestsCounter;
    private CurrentState currentState;
    private String contestName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contests);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        listView = (ListView) findViewById(R.id.listView);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.mySecondary);
        mSwipeRefreshLayout.setOnRefreshListener(refreshListener);

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
        }

        contestsFailureRequestsCounter = 0;

        if (savedInstanceState != null) {

            contestName = savedInstanceState.getString(CONTEST_NAME);

            contestsList = (ArrayList<Contest>) savedInstanceState.getSerializable("contestsList");
            initializeListView();
        } else if (contestsList != null) {
            initializeListView();
        } else {
            if (NetworkStatusHelper.isNetworkAvailable(ContestsActivity.this)) {
                getContestsFromBackend();
            } else {
                //displayNoConnectionMessage(networkStatusHelper);
                displayNoConnectionMessage();
            }
        }


        googleAnalyticsTrack();
    }

    private void googleAnalyticsTrack() {
        // Obtain the shared Tracker instance.
        SoLoudApplication application = (SoLoudApplication) getApplication();
        mTracker = application.getDefaultTracker();
    }

    private void displayNoConnectionMessage() {
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
    }

    private void initializeListView() {
        listView.setAdapter(new ContestsAdapter1(ContestsActivity.this, contestsList));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Contest contest = contestsList.get(position);

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

    private void getContestsFromBackend() {

        mSwipeRefreshLayout.setRefreshing(true);
        // Create a very simple REST adapter which points the API endpoint.
        ContestsService client = ServiceGenerator.createService(ContestsService.class);

        // Fetch the Contests.
        String soLoudToken = "Bearer " + SharedPrefsHelper.getFromPrefs(this, SharedPrefsHelper.SOLOUD_TOKEN);
        Call<List<Contest>> call = client.getContests(soLoudToken);
        Callback<List<Contest>> contestsCallback = new Callback<List<Contest>>() {
            @Override
            public void onResponse(Call<List<Contest>> call, Response<List<Contest>> response) {
                if (response.isSuccessful()) {
                    contestsList = (ArrayList<Contest>) response.body();
                    mSwipeRefreshLayout.setRefreshing(false);
                    initializeListView();
                } else {
                    // error response, no access to resource?
                    if (response.code() == 401){
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
                    mSwipeRefreshLayout.setRefreshing(false);
                    Snackbar.make(coordinatorLayout, R.string.error_requesting_contests, Snackbar.LENGTH_LONG).show();
                }
            }
        };
        call.enqueue(contestsCallback);
    }

    private SwipeRefreshLayout.OnRefreshListener refreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            if (NetworkStatusHelper.isNetworkAvailable(ContestsActivity.this)) {
                getContestsFromBackend();
            } else {
                mSwipeRefreshLayout.setRefreshing(false);
                displayNoConnectionMessage();
            }
        }
    };

    private void loginToBackend(String facebookToken) {
        // Create a very simple REST adapter which points the API endpoint.
        LoginService client = ServiceGenerator.createService(LoginService.class);

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
                    mSwipeRefreshLayout.setRefreshing(false);
                    logout();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                // something went completely south (like no internet connection)
                //Log.d(TAG, "Backend login Failure: " + t.getMessage());
                mSwipeRefreshLayout.setRefreshing(false);
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
}
