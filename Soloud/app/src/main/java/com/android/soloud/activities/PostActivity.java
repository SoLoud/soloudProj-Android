package com.android.soloud.activities;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.soloud.R;
import com.android.soloud.ServiceGenerator;
import com.android.soloud.SoLoudApplication;
import com.android.soloud.apiCalls.LoginService;
import com.android.soloud.apiCalls.PostService;
import com.android.soloud.apiCalls.PostUserPhoto;
import com.android.soloud.dialogs.ImagePreviewDialog;
import com.android.soloud.dialogs.UserPostDialog;
import com.android.soloud.models.User;
import com.android.soloud.utils.NetworkStatusHelper;
import com.android.soloud.utils.SharedPrefsHelper;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginFragment;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

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
import static com.android.soloud.utils.SharedPrefsHelper.SOLOUD_TOKEN;

public class PostActivity extends AppCompatActivity implements UserPostDialog.OnOkPressedListener{

    public static final String TAG = "PostActivity";

    private Tracker mTracker;
    private Target mTarget;
    private ImageView photo_IV;
    private Bitmap bitmap1;
    private String photoUri;
    private CallbackManager mCallbackManager;
    private String postText;
    private ProgressWheel progressWheel;
    private CoordinatorLayout coordinatorLayout;
    private NetworkStatusHelper networkStatusHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        // Obtain the shared Tracker instance.
        SoLoudApplication application = (SoLoudApplication) getApplication();
        mTracker = application.getDefaultTracker();

        networkStatusHelper = new NetworkStatusHelper(this);

        TextView post_info_TV = (TextView) findViewById(R.id.post_info_TV);
        photo_IV = (ImageView) findViewById(R.id.post_photo_IV);
        Button shareButton = (Button) findViewById(R.id.share_button);
        progressWheel = (ProgressWheel) findViewById(R.id.progress_wheel);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id
                .coordinatorLayout);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (networkStatusHelper.isNetworkAvailable()){
                    showShareDialog();
                }else{
                 displayNoConnectionMessage();
                }
            }
        });

        String description = "";
        photoUri = "";
        postText = "";

        if(getIntent() != null && getIntent().getStringExtra("description") != null &&
                getIntent().getStringArrayListExtra("hashTagsList") != null &&
                getIntent().getStringExtra("photoUri") != null){

            description = getIntent().getStringExtra("description");
            ArrayList<String> tagsList = getIntent().getStringArrayListExtra("hashTagsList");
            photoUri = getIntent().getStringExtra("photoUri");
            Log.d(TAG, "onCreate: " + description + ", " + tagsList.toString());

            String tags = tagsList.toString();
            int tagsSize = tags.length();
            String tagsWithoutBrackets = tags.substring(1,tagsSize-1);
            postText = description + " " + tagsWithoutBrackets;

            String sourceString = description +" " +"<b>" + tagsWithoutBrackets + "</b> ";
            post_info_TV.setText(Html.fromHtml(sourceString));

            Picasso.with(this).load(photoUri).placeholder(R.drawable.ic_account_circle_white_24dp).
                    error(R.drawable.ic_account_circle_white_24dp).into(photo_IV);

            //loadImage(this, photoUri);
        }

        photo_IV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFullScreenImageDialog();
            }
        });

    }

    private void showFullScreenImageDialog(){
        DialogFragment dialogFragment = ImagePreviewDialog.newInstance(photoUri);
        dialogFragment.show(getSupportFragmentManager(),"imagePreview");
    }

    private void displayNoConnectionMessage() {
        Snackbar.make(coordinatorLayout, getResources().
                getString(R.string.error_no_internet_connection), Snackbar.LENGTH_LONG).
                setAction(R.string.retry, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (networkStatusHelper.isNetworkAvailable()){
                            showShareDialog();
                        }else{
                            displayNoConnectionMessage();
                        }
                    }
                }).setActionTextColor(ContextCompat.getColor(this, R.color.mySecondary)).show();
    }

    private void checkForPublishPermissions() {
        String fb_token_from_prefs = SharedPrefsHelper.getFromPrefs(this, SharedPrefsHelper.FB_TOKEN);
        // TODO: 15/1/2017 prepei na elegxw an exw parei token gia publish kai apo ton Kwsta!!
        progressWheel.setVisibility(View.VISIBLE);
        progressWheel.spin();
        if (fb_token_from_prefs.equals(AccessToken.getCurrentAccessToken().getToken())){
            Set<String> permissions_set = AccessToken.getCurrentAccessToken().getPermissions();
            if (!permissions_set.contains("publish_actions")){
                askFacebookPublishPermissions();
                LoginManager.getInstance().logInWithPublishPermissions(PostActivity.this, Arrays.asList("publish_actions"));
            }else{
                if (!isNoE(photoUri)){
                    initPostUserPhotoService(photoUri, postText);
                }
            }
        }
    }

    private boolean isNoE( final String s ) {
        // Null-safe, short-circuit evaluation.
        return s == null || s.trim().isEmpty();
    }

    private void askFacebookPublishPermissions() {
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        mCallbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(mCallbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Log.d(TAG, "Login to FB: success with new token which has publish permissions");
                        //Toast.makeText(PostActivity.this, "Login Success", Toast.LENGTH_LONG).show();

                        SharedPrefsHelper.storeInPrefs(PostActivity.this, loginResult.getAccessToken().getToken(), SharedPrefsHelper.FB_TOKEN);
                        SharedPrefsHelper.storeInPrefs(PostActivity.this, loginResult.getAccessToken().getUserId(), SharedPrefsHelper.USER_FB_ID);

                        loginToBackend(loginResult.getAccessToken().getToken());
                    }

                    @Override
                    public void onCancel() {
                        Log.d(TAG, "Login to FB: canceled");
                        //Toast.makeText(PostActivity.this, "Login Cancel", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Log.d(TAG, "Login to FB: error");
                        //Toast.makeText(PostActivity.this, exception.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    void loadImage(Context context, String url) {

        //final ImageView imageView = (ImageView) findViewById(R.id.image);

        mTarget = new Target() {
            @Override
            public void onBitmapLoaded (final Bitmap bitmap, Picasso.LoadedFrom from){
                //Do something

               // photo_IV.setImageBitmap(bitmap);
                bitmap1 = bitmap;
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };

        Picasso.with(context)
                .load(url)
                .into(mTarget);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.i(TAG, "Setting screen name: " + POST_SN);
        mTracker.setScreenName("Screen: " + POST_SN);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }


    private void showShareDialog() {
        FragmentManager fm = getSupportFragmentManager();
        UserPostDialog alertDialog = UserPostDialog.newInstance();
        alertDialog.show(fm, "post_dialog");
    }


    private void initPostsService(String description) {

        // Create a very simple REST adapter which points the API endpoint.
        PostService client = ServiceGenerator.createService(PostService.class);

        // Make the user post to So Loud Backend
        String soLoudToken = SharedPrefsHelper.getFromPrefs(this, SOLOUD_TOKEN);
        if (soLoudToken != null && !soLoudToken.isEmpty()){
            Call<Object> call = client.sendUserPostToBackend("Bearer " + SharedPrefsHelper.getFromPrefs(this, SOLOUD_TOKEN), description);
            call.enqueue(new Callback<Object>() {
                @Override
                public void onResponse(Call<Object> call, Response<Object> response) {
                    if (response.isSuccessful()) {
                        Log.d(TAG, "Success posting: " + response.toString());

                    } else {
                        // error response, no access to resource?
                    }
                }

                @Override
                public void onFailure(Call<Object> call, Throwable t) {
                    // something went completely south (like no internet connection)
                    Log.d(TAG, "Error posting: " + t.getMessage());
                }
            });
        }else{
            // User has no token !!!
        }

    }

    private void initPostUserPhotoService(String filePath, String description){

        PostUserPhoto service = ServiceGenerator.createService(PostUserPhoto.class);

        //File file = new File(filePath);

        /////////////////

        File file = new File(getRealPathFromURI(this,Uri.parse(filePath)));

        /////////

        // TODO: 14/12/2016 na pairnw ton tupo tou arxeiou programmatistika k na min to bazw karfwta

        RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("upload", file.getName(), reqFile);
        RequestBody desc = RequestBody.create(MediaType.parse("text/plain"), description);

        String soLoudToken = SharedPrefsHelper.getFromPrefs(this, SOLOUD_TOKEN);
        Call<ResponseBody> req = service.postImage("Bearer " + soLoudToken, body, desc);
        req.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()){
                    progressWheel.stopSpinning();
                    Snackbar.make(coordinatorLayout, getResources().getString(R.string.success_post_for_revision), Snackbar.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.toString());
            }
        });
    }


    @Override
    public void onOkPressed() {

        checkForPublishPermissions();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: " + data.toString());
    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }


    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] projection = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  projection, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private void loginToBackend(String token) {

        // Create a very simple REST adapter which points the API endpoint.
        LoginService client = ServiceGenerator.createService(LoginService.class);

        // Post the user's Facebook Token
        Call<User> call = client.login(LoginActivity.FACEBOOK_PROVIDER, token, "password");
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    User soLoudUser = response.body();
                    String soLoudToken = soLoudUser.getSoloudToken();
                    SharedPrefsHelper.storeInPrefs(PostActivity.this, soLoudToken, SharedPrefsHelper.SOLOUD_TOKEN);

                    initPostUserPhotoService(photoUri, postText);

                } else {
                    // error response, no access to resource?
                    Log.d(TAG, "Backend login error in response: " + response.toString());
                    //Snackbar.make(coordinatorLayout, "Login no response", Snackbar.LENGTH_LONG).show();
                }
            }


            @Override
            public void onFailure(Call<User> call, Throwable t) {
                // something went completely south (like no internet connection)
                Log.d(TAG, "Backend login Failure: " + t.getMessage());
                //Snackbar.make(coordinatorLayout, "Login failure", Snackbar.LENGTH_LONG).show();
            }
        });
    }


}
