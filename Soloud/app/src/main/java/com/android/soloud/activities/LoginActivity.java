package com.android.soloud.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.customtabs.CustomTabsIntent;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.Spanned;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.soloud.R;
import com.android.soloud.ServiceGenerator;
import com.android.soloud.apiCalls.LoginService;
import com.android.soloud.models.User;
import com.android.soloud.utils.SharedPrefsHelper;
import com.android.soloud.wizard.WizardActivity;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.pnikosis.materialishprogress.ProgressWheel;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LoginActivity extends Activity {

    public static final String TAG = "LoginActivity";
    private CallbackManager callbackManager;

    public static final String FACEBOOK_PROVIDER = "facebook";
    private CoordinatorLayout coordinatorLayout;
    private int loginFailureRequestsCounter;
    private LoginButton loginButton;
    private com.pnikosis.materialishprogress.ProgressWheel progressWheel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        String fb_token = SharedPrefsHelper.getFromPrefs(LoginActivity.this, SharedPrefsHelper.FB_TOKEN);
        String userFbId = SharedPrefsHelper.getFromPrefs(LoginActivity.this, SharedPrefsHelper.USER_FB_ID);
        String soLoudToken = SharedPrefsHelper.getFromPrefs(LoginActivity.this, SharedPrefsHelper.SOLOUD_TOKEN);

        loginFailureRequestsCounter = 0;

        // TODO: 19/1/2017 Mipws na min xrisimopoiw katholou ta Prefs gia to fb token afou mporw na to exw apo to fb sdk apothikeumeno
        //String fbk_token = AccessToken.getCurrentAccessToken().getToken();

        if(fb_token != null && userFbId != null && soLoudToken != null){
            Intent intent = new Intent(this, WizardActivity.class);
            startActivity(intent);
            finish();
        }

        setContentView(R.layout.activity_login);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        loginButton = (LoginButton)findViewById(R.id.login_button);
        progressWheel = (ProgressWheel) findViewById(R.id.progress_wheel);
        TextView terms_and_policy_TV = (TextView) findViewById(R.id.terms_and_policy_TV);
        Spanned sp = Html.fromHtml(getString(R.string.terms_and_privacy_policy));
        terms_and_policy_TV.setText(sp);
        terms_and_policy_TV.setOnClickListener(clickListener);
        loginButton.setReadPermissions(Arrays.asList("public_profile", "email", "user_friends"));
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "onSuccess: " + "User ID:  " + loginResult.getAccessToken().getUserId() + "\n" +
                        "Auth Token: " + loginResult.getAccessToken().getToken());

                //final String token = loginResult.getAccessToken().getToken();

                // App code
                GraphRequest request = getGraphRequestMe(loginResult.getAccessToken());
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id, name, email, gender, birthday, picture.type(large)");
                request.setParameters(parameters);
                request.executeAsync();

                SharedPrefsHelper.storeInPrefs(LoginActivity.this, loginResult.getAccessToken().getToken(), SharedPrefsHelper.FB_TOKEN);
                SharedPrefsHelper.storeInPrefs(LoginActivity.this, loginResult.getAccessToken().getUserId(), SharedPrefsHelper.USER_FB_ID);
            }

            @Override
            public void onCancel() {
                //Log.d(TAG, "onCancel: Facebook login attempt cancelled.");
                //Snackbar.make(coordinatorLayout, "Login attempt cancelled", Snackbar.LENGTH_LONG).show();
                loginButton.setVisibility(View.VISIBLE);
            }

            @Override
            public void onError(FacebookException e) {
                //Log.d(TAG, "onError: Facebook login attempt failed.");
                loginButton.setVisibility(View.VISIBLE);
                Snackbar.make(coordinatorLayout, R.string.error_login_facebook, Snackbar.LENGTH_LONG).show();
            }
        });

        // Hash Key H3iwRkLWe23Sh+pZxlndHY+HtFg=
        //generateHashKey();
    }

    @NonNull
    private GraphRequest getGraphRequestMe(final AccessToken accessToken) {
        return GraphRequest.newMeRequest(
                            accessToken,
                            new GraphRequest.GraphJSONObjectCallback() {
                                @Override
                                public void onCompleted(JSONObject object, GraphResponse response) {
                                    Log.d(TAG, response.toString());

                                    // Application code
                                    try {
                                        String name = object.getString("name");
                                        String email = object.getString("email");
                                        //String picture = object.getString("picture");
                                        //String birthday = object.getString("birthday"); // 01/31/1980 format
                                        String profilePicUrl = object.getJSONObject("picture").getJSONObject("data").getString("url");

                                        SharedPrefsHelper.storeInPrefs(LoginActivity.this, name, SharedPrefsHelper.USER_NAME);
                                        SharedPrefsHelper.storeInPrefs(LoginActivity.this, profilePicUrl, SharedPrefsHelper.USER_PROFILE_PICTURE_URL);

                                        progressWheel.setVisibility(View.VISIBLE);
                                        progressWheel.spin();
                                        loginToBackend(accessToken.getToken());

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
    }

    private void openWithChromeCustomTabs(){
        String url = "https://www.google.com";
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        CustomTabsIntent customTabsIntent = builder.build();
        builder.setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        builder.setStartAnimations(this , R.anim.slide_in_right, R.anim.slide_out_left);
        builder.setExitAnimations(this, R.anim.slide_in_left, R.anim.slide_out_right);
        customTabsIntent.launchUrl(this, Uri.parse(url));
    }

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
                    SharedPrefsHelper.storeInPrefs(LoginActivity.this, soLoudToken, SharedPrefsHelper.SOLOUD_TOKEN);

                    Intent intent = new Intent(LoginActivity.this, WizardActivity.class);
                    startActivity(intent);
                    finish();

                } else {
                    // error response, no access to resource?
                    //Log.d(TAG, "Backend login error in response: " + response.toString());
                    handleResponseFailure(call);
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                // something went completely south (like no internet connection)
                //Log.d(TAG, "Backend login Failure: " + t.getMessage());
                handleResponseFailure(call);
            }

            private void handleResponseFailure(Call<User> call) {
                // Try 3 times to login
                loginFailureRequestsCounter ++;
                if (loginFailureRequestsCounter <3){
                    // Request reuse
                    Call<User> newCall = call.clone();
                    newCall.enqueue(this);
                }else{
                    LoginManager.getInstance().logOut();
                    Snackbar.make(coordinatorLayout, R.string.error_login, Snackbar.LENGTH_LONG).show();
                    loginButton.setVisibility(View.VISIBLE);
                    progressWheel.stopSpinning();
              }
            }
        };
        call.enqueue(loginCallback);
    }

    private void generateHashKey() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.android.soloud",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (NoSuchAlgorithmException | PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {

            loginButton.setVisibility(View.INVISIBLE);
        }
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.terms_and_policy_TV:
                    openWithChromeCustomTabs();
                    break;
                default:
                    break;
            }
        }
    };



}
