package com.android.soloud.facebookPlaces;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.android.soloud.R;
import com.android.soloud.SoLoudApplication;
import com.android.soloud.activities.HashTagsActivity;
import com.android.soloud.facebookPlaces.fragments.LoginFragment;
import com.android.soloud.facebookPlaces.fragments.PlaceSearchFragment;
import com.android.soloud.facebookPlaces.model.Place;
import com.android.soloud.models.Contest;
import com.android.soloud.models.CurrentState;
import com.android.soloud.userPost.PostActivity;
import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import static com.android.soloud.activities.MainActivity.POST_SN;
import static com.android.soloud.contests.ContestsActivity.CONTEST;
import static com.android.soloud.contests.ContestsActivity.CURRENT_STATE;

/**
 * Created by f.stamopoulos on 4/2/2018.
 */

public class CheckInActivity extends AppCompatActivity implements PlaceSearchFragment.Listener{

    private final AuthenticationType authenticationType = AuthenticationType.USER_TOKEN;
    private static final String CLIENT_TOKEN = "";

    public static final String TAG = "CheckInActivity";

    private Tracker mTracker;

    private Contest contest;
    private CurrentState currentState;

    private String placeID;
    private static final int REQUEST_LOCATION = 537;

    public static final String PLACE = "place";

    private String placeName;
    private Place mPlace;
    private boolean isNextActionEnabled;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.check_in_activity);

        isNextActionEnabled = false;

        if(getIntent() != null && getIntent().getSerializableExtra(CONTEST) != null &&
                getIntent().getSerializableExtra(CURRENT_STATE) != null){

            contest = (Contest) getIntent().getSerializableExtra(CONTEST);
            currentState = (CurrentState) getIntent().getSerializableExtra(CURRENT_STATE);

            if (getIntent().hasExtra(PLACE)) {
                Place mPlace = getIntent().getParcelableExtra(PLACE);
                placeName = mPlace.get(Place.NAME);
            }
        }

        if (savedInstanceState != null){
            contest = (Contest) getIntent().getSerializableExtra(CONTEST);
            currentState = (CurrentState) getIntent().getSerializableExtra(CURRENT_STATE);
        }

        checkToken();

        googleAnalyticsTrack();
    }


    @Override
    protected void onStart() {
        super.onStart();

        requestLocationPermission();
    }

    private void displayPlaceListFragment() {
        PlaceSearchFragment placeListFragment = PlaceSearchFragment.newInstance(placeName);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_in_from_right, R.anim.slide_out_to_the_left);
        transaction.replace(R.id.fragment_placeholder, placeListFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void displayLoginFragment() {
        LoginFragment loginFragment = LoginFragment.newInstance();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_placeholder, loginFragment);
        transaction.commit();
    }

    private void checkToken() {
        if (authenticationType == AuthenticationType.USER_TOKEN) {
            AccessToken accessToken = AccessToken.getCurrentAccessToken();
            if (accessToken == null) {
                /**
                 * When a User Access Token is used, and if the token is not present,
                 * then prompt the user to log into Facebook.
                 */
                displayLoginFragment();
            } else {
                displayPlaceListFragment();
            }
        } else {
            /**
             * When a Client Token is used, set the client token to the Facebook SDK class
             * as illustrated below. Users do not need to log into Facebook. PlaceManager requests
             * can be placed once the client token has been set.
             */
            FacebookSdk.setClientToken(CLIENT_TOKEN);
            displayPlaceListFragment();
        }
    }

    @Override
    public void onPlaceSelected(Place place) {

        // TODO: 11/2/2018 Enable Next button
        isNextActionEnabled = true;
        mPlace = place;
        invalidateOptionsMenu();
    }

    @Override
    public void onLocationPermissionsError() {

    }

    @Override
    public boolean hasLocationPermission() {
        return false;
    }

    @Override
    public void onNothingSelected() {
        isNextActionEnabled = false;
        invalidateOptionsMenu();
    }


    private enum AuthenticationType {
        USER_TOKEN,
        CLIENT_TOKEN,
    }

    private void requestLocationPermission() {
        /*
         * Prompts the user to grant location permissions. Use the
         * device's' location to get the current place from the Place Graph SDK,
         * and to perform local place searches.
         */
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.permission_prompt_location);
            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ActivityCompat.requestPermissions(
                            CheckInActivity.this,
                            new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                                    android.Manifest.permission.ACCESS_COARSE_LOCATION},
                            REQUEST_LOCATION);
                }
            });
            builder.create().show();
        } else {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                            android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_LOCATION);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        outState.putSerializable(CONTEST, contest);
        outState.putSerializable(CURRENT_STATE, currentState);

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

        locationStatusCheck();

        Log.i(TAG, "Setting screen name: " + POST_SN);
        mTracker.setScreenName("Screen: " + POST_SN);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

    }

    public void locationStatusCheck() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();

        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.location_message)
                .setCancelable(false)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, HashTagsActivity.class);
        intent.putExtra(CONTEST, contest);
        intent.putExtra(CURRENT_STATE, currentState);
        //intent.putExtra(PLACE, place);
        startActivity(intent);
        finish();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.hashtags_menu, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_proceed:
                goToPostPreviewActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        /*MenuItem actionNext = menu.findItem(R.id.action_proceed);
        actionNext.setEnabled(isNextActionEnabled);*/
        return isNextActionEnabled;
    }

    private void goToPostPreviewActivity() {
        Intent intent = new Intent(this, PostActivity.class);
        intent.putExtra(CONTEST, contest);
        intent.putExtra(CURRENT_STATE, currentState);
        intent.putExtra(PLACE, mPlace);
        startActivity(intent);
        finish();
    }
}
