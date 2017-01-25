package com.android.soloud.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.util.Base64;
import android.util.Log;

import com.android.soloud.R;
import com.android.soloud.ServiceGenerator;
import com.android.soloud.apiCalls.LoginService;
import com.android.soloud.models.User;
import com.android.soloud.utils.SharedPrefsHelper;
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
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        setContentView(R.layout.activity_login);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        LoginButton loginButton = (LoginButton)findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList("public_profile", "email", "user_friends"));
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "onSuccess: " + "User ID:  " + loginResult.getAccessToken().getUserId() + "\n" +
                        "Auth Token: " + loginResult.getAccessToken().getToken());

                final String token = loginResult.getAccessToken().getToken();

                // App code
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
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
                                    
                                    loginToBackend(token);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
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
            }

            @Override
            public void onError(FacebookException e) {
                //Log.d(TAG, "onError: Facebook login attempt failed.");
                Snackbar.make(coordinatorLayout, R.string.error_login_facebook, Snackbar.LENGTH_LONG).show();
            }
        });

        // Hash Key 5SmyNCWXqgooB+tJ5v3GcjG4xC0=
        //generateHashKey();
    }

    private void loginToBackend(String token) {
        // Create a very simple REST adapter which points the API endpoint.
        LoginService client = ServiceGenerator.createService(LoginService.class);

        // Post the user's Facebook Token
        Call<User> call = client.login(FACEBOOK_PROVIDER, token, "password");
        Callback<User> loginCallback = new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    User soLoudUser = response.body();
                    String soLoudToken = soLoudUser.getSoloudToken();
                    SharedPrefsHelper.storeInPrefs(LoginActivity.this, soLoudToken, SharedPrefsHelper.SOLOUD_TOKEN);

                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
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
            /*Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();*/
        }
    }

}
