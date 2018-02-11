package com.android.soloud.userPost;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import com.android.soloud.R;
import com.android.soloud.SoLoudApplication;
import com.android.soloud.facebookPlaces.CheckInActivity;
import com.android.soloud.facebookPlaces.model.Place;
import com.android.soloud.models.Contest;
import com.android.soloud.models.CurrentState;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;

import static com.android.soloud.activities.MainActivity.POST_SN;
import static com.android.soloud.contests.ContestsActivity.CONTEST;
import static com.android.soloud.contests.ContestsActivity.CURRENT_STATE;
import static com.android.soloud.facebookPlaces.CheckInActivity.PLACE;

public class PostActivity extends AppCompatActivity {

    public static final String TAG = "PostActivity";

    private Tracker mTracker;

    private Contest contest;
    private CurrentState currentState;

    private Place place;

    private final AuthenticationType authenticationType = AuthenticationType.USER_TOKEN;
    private static final String CLIENT_TOKEN = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(getIntent() != null && getIntent().getSerializableExtra(CONTEST) != null && getIntent().getParcelableExtra("place") != null){

            contest = (Contest) getIntent().getSerializableExtra(CONTEST);
            currentState = (CurrentState) getIntent().getSerializableExtra(CURRENT_STATE);

            place = getIntent().getParcelableExtra("place");

            displayUserPostFragment(contest, currentState, place);
        }

        googleAnalyticsTrack();

    }


    private void displayUserPostFragment(Contest contest, CurrentState currentState, Place selectedPlace) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.fragment_placeholder, UserPostFragment.newInstance(contest, currentState, selectedPlace)); // newInstance() is a static factory method.
        transaction.commit();
    }



    @Override
    protected void onSaveInstanceState(Bundle outState) {

        outState.putSerializable(CONTEST, contest);
        outState.putSerializable(CURRENT_STATE, currentState);
        outState.putParcelable(PLACE, place);

        super.onSaveInstanceState(outState);
    }

    private void googleAnalyticsTrack() {
        // Obtain the shared Tracker instance.
        SoLoudApplication application = (SoLoudApplication) getApplication();
        mTracker = application.getDefaultTracker();
    }



    @Override
    protected void onResume() {
        super.onResume();

        Log.i(TAG, "Setting screen name: " + POST_SN);
        mTracker.setScreenName("Screen: " + POST_SN);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

    }


    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: " + data.toString());
    }*/


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(PostActivity.this, CheckInActivity.class);
        intent.putExtra(CONTEST, contest);
        intent.putExtra(CURRENT_STATE, currentState);
        //intent.putExtra("hashTagsList", tagsList);
        intent.putExtra(PLACE, place);
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


    /*private void checkForLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_LOCATION);
        }
    }*/

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        /*switch (requestCode) {
            case REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Toast.makeText(this, "Location permission granted", Toast.LENGTH_SHORT).show();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
                }
            }
        }*/
    }



    /*@Override
    public void onLoginComplete() {
        displayPlaceListFragment();
    }

    @Override
    public void onCallPhone(Intent intent) {

    }

    @Override
    public void onPlaceSelected(Place place) {
        // TODO: 17/12/2017 Na perasw to selected place sto User Post Fragment
        //displayPlaceInfoFragment(place);
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack();
        }
        displayUserPostFragment(contest, currentState, getIntent().getStringArrayListExtra("hashTagsList"), place);
    }

    @Override
    public void onLocationPermissionsError() {
        requestLocationPermission();
    }

    @Override
    public boolean hasLocationPermission() {
        return hasPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)
                || hasPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION);
    }

    private boolean hasPermission(String permission) {
        return ActivityCompat.checkSelfPermission(this, permission)
                == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onLocationPressed() {
        //displayPlaceListFragment();
        onPlacesButtonClicked();
    }*/

    private enum AuthenticationType {
        USER_TOKEN,
        CLIENT_TOKEN,
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
