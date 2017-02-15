package com.android.soloud.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.soloud.R;
import com.android.soloud.ServiceGenerator;
import com.android.soloud.SoLoudApplication;
import com.android.soloud.adapters.ContestsAdapter;
import com.android.soloud.apiCalls.ContestsService;
import com.android.soloud.models.Contest;
import com.android.soloud.utils.NetworkStatusHelper;
import com.android.soloud.utils.SharedPrefsHelper;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.android.soloud.activities.MainActivity.CATEGORY_CONTESTS_SN;
import static com.android.soloud.fragments.CategoriesFragment.CONTEST_NAME;

public class ContestsActivity extends AppCompatActivity {

    public static final String COMPANY_NAME = "CompanyName";
    public static final String CONTEST = "contest";
    private static final String TAG = "ContestsActivity";
    private Tracker mTracker;
    private ListView listView;
    public static ArrayList<Contest> contestsList;
    //private ProgressWheel progressWheel;
    private CoordinatorLayout coordinatorLayout;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private int contestsFailureRequestsCounter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contests);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        /*progressWheel = (ProgressWheel) findViewById(R.id.progress_wheel);
        progressWheel.spin();*/
        listView = (ListView) findViewById(R.id.listView);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.mySecondary);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (NetworkStatusHelper.isNetworkAvailable(ContestsActivity.this)) {
                    initContestsService();
                } else {
                    mSwipeRefreshLayout.setRefreshing(false);
                    displayNoConnectionMessage();
                }
            }
        });

        if (getIntent() != null && getIntent().getStringExtra(CONTEST_NAME) != null) {
            String contestName = getIntent().getStringExtra(CONTEST_NAME);
            getSupportActionBar().setTitle(contestName);
        }

        contestsFailureRequestsCounter = 0;

        if (savedInstanceState != null) {
            contestsList = (ArrayList<Contest>) savedInstanceState.getSerializable("contestsList");
            initializeListView();
        } else if (contestsList != null) {
            initializeListView();
        } else {
            if (NetworkStatusHelper.isNetworkAvailable(ContestsActivity.this)) {
                initContestsService();
            } else {
                //displayNoConnectionMessage(networkStatusHelper);
                displayNoConnectionMessage();
            }
        }

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
                            initContestsService();
                        } else {
                            displayNoConnectionMessage();
                        }
                    }
                }).setActionTextColor(ContextCompat.getColor(this, R.color.mySecondary)).show();
    }

    private void initializeListView() {
        listView.setAdapter(new ContestsAdapter(ContestsActivity.this, contestsList));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Contest contest = contestsList.get(position);

                Contest.User user = contest.getmUser();
                String companyName = user.getmUserName();

                //String companyName = contestsList.get(position).getUser().;
                Intent intent = new Intent(ContestsActivity.this, ContestDetails.class);
                intent.putExtra(COMPANY_NAME, companyName);
                intent.putExtra(CONTEST, contest);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        outState.putSerializable("contestsList", contestsList);

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.i(TAG, "Setting screen name: " + CATEGORY_CONTESTS_SN);
        mTracker.setScreenName("Screen: " + CATEGORY_CONTESTS_SN);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    private void initContestsService() {

        mSwipeRefreshLayout.setRefreshing(true);
        //progressWheel.setVisibility(View.VISIBLE);
        // Create a very simple REST adapter which points the API endpoint.
        ContestsService client = ServiceGenerator.createService(ContestsService.class);

        // Fetch the Contests.
        Call<List<Contest>> call = client.getContests("Bearer " + SharedPrefsHelper.getFromPrefs(this, SharedPrefsHelper.SOLOUD_TOKEN));
        Callback<List<Contest>> contestsCallback = new Callback<List<Contest>>() {
            @Override
            public void onResponse(Call<List<Contest>> call, Response<List<Contest>> response) {
                if (response.isSuccessful()) {
                    contestsList = (ArrayList<Contest>) response.body();
                    //progressWheel.stopSpinning();
                    mSwipeRefreshLayout.setRefreshing(false);
                    initializeListView();
                } else {
                    // error response, no access to resource?
                    if (response.code() == 401){
                        // TODO: 26/1/2017 Na kanw diaxeirisi an einai unauthorized. Refresh Token?


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

}
