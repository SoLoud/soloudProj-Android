package com.android.soloud.activities;

import android.content.Intent;
import android.os.Bundle;
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
import com.android.soloud.utils.SharedPrefsHelper;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.pnikosis.materialishprogress.ProgressWheel;

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
    private ProgressWheel progressWheel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contests);

        progressWheel = (ProgressWheel) findViewById(R.id.progress_wheel);
        progressWheel.spin();
        listView = (ListView) findViewById(R.id.listView);

        if (getIntent() != null && getIntent().getStringExtra(CONTEST_NAME) != null){
            String contestName = getIntent().getStringExtra(CONTEST_NAME);
            getSupportActionBar().setTitle(contestName);
        }


        if (savedInstanceState != null){
            contestsList = (ArrayList<Contest>) savedInstanceState.getSerializable("contestsList");
            initializeListView();
        }else if (contestsList != null){
            initializeListView();
        }else{
            initContestsService();
        }

        // Obtain the shared Tracker instance.
        SoLoudApplication application = (SoLoudApplication) getApplication();
        mTracker = application.getDefaultTracker();
    }

    private void initializeListView(){
        listView.setAdapter(new ContestsAdapter(ContestsActivity.this, contestsList));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Contest contest = contestsList.get(position);

                Contest.User user = contest.getUser();
                String companyEmail = user.getEmail();

                //String companyName = contestsList.get(position).getUser().;
                Intent intent = new Intent(ContestsActivity.this, ContestDetails.class);
                intent.putExtra(COMPANY_NAME, companyEmail);
                intent.putExtra(CONTEST, contest);
                startActivity(intent);
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

        progressWheel.setVisibility(View.VISIBLE);
        // Create a very simple REST adapter which points the API endpoint.
        ContestsService client = ServiceGenerator.createService(ContestsService.class);

        // Fetch the Contests.
        Call<List<Contest>> call = client.getContests("Bearer " + SharedPrefsHelper.getFromPrefs(this, SharedPrefsHelper.SOLOUD_TOKEN));
        call.enqueue(new Callback<List<Contest>>() {
                         @Override
                         public void onResponse(Call<List<Contest>> call, Response<List<Contest>> response) {
                             if (response.isSuccessful()) {
                                 contestsList = (ArrayList<Contest>) response.body();
                                 progressWheel.stopSpinning();
                                 initializeListView();
                             } else {
                                 // error response, no access to resource?
                             }
                         }

                         @Override
                         public void onFailure(Call<List<Contest>> call, Throwable t) {
                             // something went completely south (like no internet connection)
                             Log.d(TAG, "Error getting contests: " + t.getMessage());
                         }
                     });
    }

}
