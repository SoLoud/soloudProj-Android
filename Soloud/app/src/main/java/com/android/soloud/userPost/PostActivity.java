package com.android.soloud.userPost;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.soloud.R;
import com.android.soloud.ServiceGenerator;
import com.android.soloud.SoLoudApplication;
import com.android.soloud.activities.HashTagsActivity;
import com.android.soloud.activities.LoginActivity;
import com.android.soloud.apiCalls.LoginService;
import com.android.soloud.apiCalls.PostUserPhoto;
import com.android.soloud.contests.ContestsActivity;
import com.android.soloud.dialogs.ImagePreviewDialog;
import com.android.soloud.dialogs.ProgressDialog;
import com.android.soloud.dialogs.UserPostDialog;
import com.android.soloud.facebookPlaces.fragments.LoginFragment;
import com.android.soloud.facebookPlaces.fragments.PlaceInfoFragment;
import com.android.soloud.facebookPlaces.fragments.PlaceSearchFragment;
import com.android.soloud.facebookPlaces.model.Place;
import com.android.soloud.models.Contest;
import com.android.soloud.models.CurrentState;
import com.android.soloud.models.User;
import com.android.soloud.utils.ImageHelper;
import com.android.soloud.utils.LogoutHelper;
import com.android.soloud.utils.NetworkStatusHelper;
import com.android.soloud.utils.SharedPrefsHelper;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.android.soloud.activities.MainActivity.POST_SN;
import static com.android.soloud.contests.ContestsActivity.CONTEST;
import static com.android.soloud.contests.ContestsActivity.CURRENT_STATE;
import static com.android.soloud.utils.MyStringHelper.isNoE;
import static com.android.soloud.utils.SharedPrefsHelper.POST_POP_UP_DISPLAYED;
import static com.android.soloud.utils.SharedPrefsHelper.SOLOUD_TOKEN;

public class PostActivity extends AppCompatActivity implements UserPostDialog.OnOkPressedListener, LoginFragment.Listener,
        PlaceSearchFragment.Listener, PlaceInfoFragment.Listener, UserPostFragment.OnLocationPressedListener{

    public static final String TAG = "PostActivity";

    private Tracker mTracker;

    private Contest contest;
    private CurrentState currentState;

    private String placeID;
    private static final int REQUEST_LOCATION = 537;


    private final AuthenticationType authenticationType = AuthenticationType.USER_TOKEN;
    private static final String CLIENT_TOKEN = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        if (savedInstanceState != null){
            contest = (Contest) getIntent().getSerializableExtra(CONTEST);
            currentState = (CurrentState) getIntent().getSerializableExtra(CURRENT_STATE);

            /*displayTagsAndDescription();
            displayUserPhoto();*/
            displayUserPostFragment(contest, currentState, null);
        }

        if(getIntent() != null && getIntent().getSerializableExtra(CONTEST) != null &&
                getIntent().getStringArrayListExtra("hashTagsList") != null){

            contest = (Contest) getIntent().getSerializableExtra(CONTEST);
            currentState = (CurrentState) getIntent().getSerializableExtra(CURRENT_STATE);

            /*displayTagsAndDescription();
            displayUserPhoto();*/
            displayUserPostFragment(contest, currentState, getIntent().getStringArrayListExtra("hashTagsList"));
        }

        googleAnalyticsTrack();

    }


    private void displayUserPostFragment(Contest contest, CurrentState currentState, ArrayList<String> hasTagsList) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.fragment_placeholder, UserPostFragment.newInstance(contest, currentState, hasTagsList)); // newInstance() is a static factory method.
        transaction.commit();
    }


    @Override
    protected void onStart() {
        super.onStart();

        requestLocationPermission();
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
                            PostActivity.this,
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

        Log.i(TAG, "Setting screen name: " + POST_SN);
        mTracker.setScreenName("Screen: " + POST_SN);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }





    @Override
    public void onOkPressed() {

        //checkForPublishPermissions();
    }


    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: " + data.toString());
    }*/





    @Override
    public void onBackPressed() {
        Intent intent = new Intent(PostActivity.this, HashTagsActivity.class);
        intent.putExtra(CONTEST, contest);
        intent.putExtra(CURRENT_STATE, currentState);
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


    private void displayPlaceListFragment() {
        PlaceSearchFragment placeListFragment = PlaceSearchFragment.newInstance();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_placeholder, placeListFragment);
        transaction.commit();
    }

    private void displayLoginFragment() {
        LoginFragment loginFragment = LoginFragment.newInstance();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_placeholder, loginFragment);
        transaction.commit();
    }

    private void onPlacesButtonClicked() {
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

    private void displayPlaceInfoFragment(Place place) {
        PlaceInfoFragment placeInfoFragment = PlaceInfoFragment.newInstance();
        Bundle bundle = new Bundle();
        bundle.putParcelable(PlaceInfoFragment.EXTRA_PLACE, place);
        placeInfoFragment.setArguments(bundle);
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_placeholder, placeInfoFragment, "details")
                .addToBackStack(place.get(Place.NAME))
                .commit();
    }

    @Override
    public void onLoginComplete() {
        displayPlaceListFragment();
    }

    @Override
    public void onCallPhone(Intent intent) {

    }

    @Override
    public void onPlaceSelected(Place place) {
        displayPlaceInfoFragment(place);
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
    }

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
