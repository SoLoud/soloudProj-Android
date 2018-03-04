package com.android.soloud.userPost;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.soloud.R;
import com.android.soloud.ServiceGenerator;
import com.android.soloud.login.LoginActivity;
import com.android.soloud.login.LoginApi;
import com.android.soloud.contests.ContestsActivity;
import com.android.soloud.dialogs.ImagePreviewDialog;
import com.android.soloud.dialogs.ProgressDialog;
import com.android.soloud.dialogs.UserPostDialog;
import com.android.soloud.facebookPlaces.model.Place;
import com.android.soloud.facebookPlaces.model.PlaceTextUtils;
import com.android.soloud.models.Contest;
import com.android.soloud.models.CurrentState;
import com.android.soloud.models.User;
import com.android.soloud.utils.ImageHelper;
import com.android.soloud.utils.LogoutHelper;
import com.android.soloud.utils.MyStringHelper;
import com.android.soloud.utils.NetworkStatusHelper;
import com.android.soloud.utils.SharedPrefsHelper;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.android.soloud.utils.MyStringHelper.isNoE;
import static com.android.soloud.utils.SharedPrefsHelper.POST_POP_UP_DISPLAYED;
import static com.android.soloud.utils.SharedPrefsHelper.SOLOUD_TOKEN;

/**
 * Created by f.stamopoulos on 3/12/2017.
 */

public class UserPostFragment extends Fragment {

    private Button shareButton;
    private ImageView photo_IV;
    private int loginFailureRequestsCounter;
    private int postFailureRequestsCounter;
    private int orientationButtonCounter;
    private Bitmap orientatedImage;
    private TextView post_info_TV;
    private String tagsWithoutHashString;
    private CoordinatorLayout coordinatorLayout;
    //private RelativeLayout checkIn_RL;
    private TextView location_TV, address_TV;

    @BindView(R.id.address_TV) TextView addressTV;
    //@BindView(R.id.clearIV) ImageView clearIV;

    private CallbackManager mCallbackManager;
    private String postText;

    private String imageName = "";
    private ImageHelper imageHelper;
    private CurrentState currentState;
    private Contest contest;
    private Toolbar toolbar;
    private Place mPlace;

    /*public interface OnLocationPressedListener {
        void onLocationPressed();
    }

    private OnLocationPressedListener mListener;*/


    public static UserPostFragment newInstance(Contest contest, CurrentState currentState, Place selectedPlace) {

        Bundle args = new Bundle();
        args.putSerializable(ContestsActivity.CONTEST, contest);
        args.putSerializable(ContestsActivity.CURRENT_STATE, currentState);
        args.putParcelable("selectedPlace", selectedPlace);
        UserPostFragment fragment = new UserPostFragment();
        fragment.setArguments(args);
        return fragment;
    }

    /*@Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mListener = (OnLocationPressedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnLocationPressedListener");
        }
    }*/

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        currentState = (CurrentState) getArguments().getSerializable(ContestsActivity.CURRENT_STATE);
        contest = (Contest) getArguments().getSerializable(ContestsActivity.CONTEST);
        postText = "";
        mPlace = getArguments().getParcelable("selectedPlace");
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.user_post_fragment1, container, false);
        ButterKnife.bind(this, view);
        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        post_info_TV = (TextView) view.findViewById(R.id.hashTags_TV);
        photo_IV = (ImageView) view.findViewById(R.id.post_photo_IV);
        //ImageButton rotate_Btn = (ImageButton) view.findViewById(R.id.rotate_btn);
        shareButton = (Button) view.findViewById(R.id.share_button);
        coordinatorLayout = (CoordinatorLayout) view.findViewById(R.id.coordinatorLayout);
        /*checkIn_RL = (RelativeLayout) view.findViewById(R.id.check_in_RL);*/
        location_TV = (TextView) view.findViewById(R.id.location_TV);
        addressTV =  (TextView) view.findViewById(R.id.address_TV);
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        shareButton.setOnClickListener(onClickListener);
        //rotate_Btn.setOnClickListener(onClickListener);
        photo_IV.setOnClickListener(onClickListener);

        loginFailureRequestsCounter =0;
        postFailureRequestsCounter = 0;
        orientationButtonCounter = 0;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        toolbar.setTitle(R.string.post_preview);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        displayTagsAndDescription();
        displayUserPhoto();

        if (mPlace != null) {
            location_TV.setText(mPlace.get(Place.NAME));
            String address = PlaceTextUtils.getAddress(mPlace);
            if (!MyStringHelper.isNoE(address)) {
                addressTV.setVisibility(View.VISIBLE);
                addressTV.setText(PlaceTextUtils.getAddress(mPlace));
                //clearIV.setVisibility(View.VISIBLE);
            } else {
                addressTV.setVisibility(View.GONE);
            }
        } else {
            addressTV.setVisibility(View.GONE);
            //clearIV.setVisibility(View.GONE);
        }

    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();

    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.share_button:
                    onSharePressed();
                    break;
                /*case R.id.rotate_btn:
                    onRotatePressed();
                    break;*/
                case R.id.post_photo_IV:
                    showFullScreenImageDialog();
                    break;
                /*case R.id.check_in_RL:
                    mListener.onLocationPressed();
                    break;*/
                default:
                    break;
            }
        }
    };

    private void onRotatePressed(){
        orientationButtonCounter ++;
        if (orientationButtonCounter >3){
            orientationButtonCounter = 0;
        }
        float degrees;
        if (orientationButtonCounter == 0){
            degrees = 0f;
        }
        else if (orientationButtonCounter == 1){
            degrees = 90f;
        }else if (orientationButtonCounter == 2){
            degrees = 180f;
        }else {
            degrees = 270f;
        }
        rotateBitmap(degrees);
    }

    private void rotateBitmap(float degrees){
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(orientatedImage,orientatedImage.getWidth(),orientatedImage.getHeight(),true);
        Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap , 0, 0, scaledBitmap .getWidth(), scaledBitmap .getHeight(), matrix, true);
        photo_IV.setImageBitmap(rotatedBitmap);
    }

    private void onSharePressed(){
        if (NetworkStatusHelper.isNetworkAvailable(getActivity())){
            boolean popUpDisplayed = SharedPrefsHelper.getBooleanFromPrefs(getActivity(), POST_POP_UP_DISPLAYED);
            if (!popUpDisplayed) {
                showShareDialog();
            }else{
                checkForPublishPermissions();
            }
        }else{
            displayNoConnectionMessage();
        }
    }

    private void displayNoConnectionMessage() {
        Snackbar.make(coordinatorLayout, getResources().
                getString(R.string.error_no_internet_connection), Snackbar.LENGTH_LONG).
                setAction(R.string.retry, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (NetworkStatusHelper.isNetworkAvailable(getActivity())){
                            showShareDialog();
                        }else{
                            displayNoConnectionMessage();
                        }
                    }
                }).setActionTextColor(ContextCompat.getColor(getActivity(), R.color.mySecondary)).show();
    }

    private void showShareDialog() {
        UserPostDialog alertDialog = UserPostDialog.newInstance();
        alertDialog.show(getActivity().getSupportFragmentManager(), "post_dialog");
    }

    private void showFullScreenImageDialog(){
        DialogFragment dialogFragment = ImagePreviewDialog.newInstance(currentState.getPhotoUri());
        dialogFragment.show(getActivity().getSupportFragmentManager(),"imagePreview");
    }



    private void checkForPublishPermissions() {
        String fb_token_from_prefs = SharedPrefsHelper.getFromPrefs(getActivity(), SharedPrefsHelper.FB_TOKEN);
        // TODO: 15/1/2017 prepei na elegxw an exw parei token gia publish kai apo ton Kwsta!!
        // TODO: 2/3/2017 An patisei o xristis NOT NOW kai pali exei allo fb token kai den bainei mesa se auto to IF
        if (fb_token_from_prefs.equals(AccessToken.getCurrentAccessToken().getToken())){
            showProgressDialog();
            shareButton.setEnabled(false);
            Set<String> permissions_set = AccessToken.getCurrentAccessToken().getPermissions();
            if (!permissions_set.contains("publish_actions")){
                askFacebookPublishPermissions();
                LoginManager.getInstance().logInWithPublishPermissions(getActivity(), Arrays.asList("publish_actions"));
            }else{
                if (!isNoE(currentState.getPhotoUri())){
                    initPostUserPhotoService(currentState.getPhotoUri(), postText);
                }
            }
        }
    }

    private void showProgressDialog(){
        ProgressDialog progressDialog = new ProgressDialog();
        progressDialog.setCancelable(false);
        progressDialog.show(getActivity().getSupportFragmentManager(), ProgressDialog.class.getSimpleName());
    }

    private void askFacebookPublishPermissions() {
        FacebookSdk.sdkInitialize(getActivity().getApplicationContext());
        mCallbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(mCallbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Log.d("UserPostFragment", "Login to FB: success with new token which has publish permissions");

                        SharedPrefsHelper.storeInPrefs(getActivity(), loginResult.getAccessToken().getToken(), SharedPrefsHelper.FB_TOKEN);
                        SharedPrefsHelper.storeInPrefs(getActivity(), loginResult.getAccessToken().getUserId(), SharedPrefsHelper.USER_FB_ID);

                        loginToBackend(loginResult.getAccessToken().getToken());
                    }

                    @Override
                    public void onCancel() {
                        Log.d("UserPostFragment", "Login to FB: canceled");
                        //Toast.makeText(PostActivity.this, "Login Cancel", Toast.LENGTH_LONG).show();
                        shareButton.setEnabled(true);
                        hideProgressDialog();
                        Snackbar.make(coordinatorLayout, R.string.cancel_login_facebook, Snackbar.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Log.d("UserPostFragment", "Facebook Login attempt failed");
                        Snackbar.make(coordinatorLayout, R.string.error_login_facebook, Snackbar.LENGTH_LONG).show();
                        LogoutHelper logoutHelper = new LogoutHelper(getActivity());
                        logoutHelper.logOut();
                    }
                });
    }

    private void hideProgressDialog() {
        DialogFragment dialog = (DialogFragment) getActivity().getSupportFragmentManager().findFragmentByTag(ProgressDialog.class.getSimpleName());
        if(dialog != null){
            dialog.dismiss();
        }
    }


    private void initPostUserPhotoService(String filePath, String description){

        PostUserPhoto service = ServiceGenerator.createService(PostUserPhoto.class);

        imageHelper = new ImageHelper(getActivity());
        //Bitmap bitmap = imageHelper.getBitmapFromUri(Uri.parse(filePath));
        //Bitmap resizedImage = imageHelper.getResizedImage(imageHelper.getBitmapFromUri(Uri.parse(filePath)));
        File imageFile = imageHelper.saveToInternalStorage(((BitmapDrawable)photo_IV.getDrawable()).getBitmap());
        if (imageFile != null){
            imageName = imageFile.getName();
        }


        String imageType = getMimeType(imageFile.toString());

        RequestBody reqFile = RequestBody.create(MediaType.parse(imageType), imageFile);
        //RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), imageFile);
        MultipartBody.Part body = MultipartBody.Part.createFormData("upload", imageFile.getName(), reqFile);
        RequestBody desc = RequestBody.create(MediaType.parse("text/plain"), description);
        RequestBody contestId = RequestBody.create(MediaType.parse("text/plain"), contest.getmId());
        RequestBody placeId = RequestBody.create(MediaType.parse("text/plain"), mPlace.get(Place.ID));
        RequestBody hashTags = RequestBody.create(MediaType.parse("text/plain"), tagsWithoutHashString);

        String soLoudToken = "Bearer " + SharedPrefsHelper.getFromPrefs(getActivity(), SOLOUD_TOKEN);
        Call<ResponseBody> request = service.postImage(soLoudToken, body, desc, contestId, hashTags, placeId);
        Callback<ResponseBody> postImageCallback = new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()){

                    imageHelper.deleteImageFromInternalStorage(imageName);

                    hideProgressDialog();

                    Snackbar.make(coordinatorLayout, getResources().getString(R.string.success_post_for_revision), Snackbar.LENGTH_LONG).
                            setCallback(new Snackbar.Callback() {
                                @Override
                                public void onDismissed(Snackbar snackbar, int event) {
                                    super.onDismissed(snackbar, event);

                                    Intent intent = new Intent(getActivity(), ContestsActivity.class);
                                    startActivity(intent);
                                    getActivity().finish();
                                }
                            })
                            .show();

                }else{
                    if (response.code() == 401){
                        LogoutHelper logoutHelper = new LogoutHelper(getActivity());
                        logoutHelper.logOut();
                    }else{
                        handleResponseFailure(call);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("UserPostFragment", "onFailure: " + t.toString());
                handleResponseFailure(call);
            }

            private void handleResponseFailure(Call<ResponseBody> call) {
                // Try 3 times to login
                postFailureRequestsCounter ++;
                if (postFailureRequestsCounter <3){
                    // Request reuse
                    Call<ResponseBody> newCall = call.clone();
                    newCall.enqueue(this);
                }else{
                    hideProgressDialog();
                    shareButton.setEnabled(true);
                    //LoginManager.getInstance().logOut();
                    Snackbar.make(coordinatorLayout, R.string.error_login, Snackbar.LENGTH_LONG).show();
                    LogoutHelper logoutHelper = new LogoutHelper(getActivity());
                    logoutHelper.logOut();
                }
            }
        };
        request.enqueue(postImageCallback);
    }

    private String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }


    private void loginToBackend(String facebookToken) {

        // Create a very simple REST adapter which points the API endpoint.
        LoginApi client = ServiceGenerator.createService(LoginApi.class);

        // Post the user's Facebook Token
        Call<User> call = client.login(LoginActivity.FACEBOOK_PROVIDER, facebookToken, "password");
        final Callback<User> loginCallback = new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    User soLoudUser = response.body();
                    String soLoudToken = soLoudUser.getSoloudToken();
                    SharedPrefsHelper.storeInPrefs(getActivity(), soLoudToken, SharedPrefsHelper.SOLOUD_TOKEN);

                    initPostUserPhotoService(currentState.getPhotoUri(), postText);

                } else {
                    // error response, no access to resource?
                    Log.d("UserPostFragment", "Backend login error in response: " + response.toString());
                    handleResponseFailure(call);
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                // something went completely south (like no internet connection)
                Log.d("UserPostFragment", "Backend login Failure: " + t.getMessage());
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
                    Snackbar.make(coordinatorLayout, R.string.error_login, Snackbar.LENGTH_LONG).show();
                    LogoutHelper logoutHelper = new LogoutHelper(getActivity());
                    logoutHelper.logOut();
                }
            }
        };
        call.enqueue(loginCallback);
    }


    private void displayTagsAndDescription() {
        String description;
        description = currentState.getUserPostDescription();
        ArrayList<String> tagsList = currentState.getUserHashTagsList();
        String tags = convertTagsListToStringWithHashes(tagsList);
        tagsWithoutHashString = convertTagsListToStringWithoutHash(tagsList);
        postText = description + " " + tags;
        String sourceString = description +" " +"<b>" + tags + "</b> ";
        post_info_TV.setText(Html.fromHtml(sourceString));
    }

    private void displayUserPhoto() {

        imageHelper = new ImageHelper(getActivity());
        //int orientation = getOrientation(this, Uri.parse(currentState.getPhotoUri()));

        Bitmap resizedImage = imageHelper.getResizedImage(imageHelper.getBitmapFromUri(Uri.parse(currentState.getPhotoUri())), 960);

        orientatedImage = Bitmap.createBitmap(resizedImage, 0, 0, resizedImage.getWidth(),
                resizedImage.getHeight(), imageHelper.getImageOrientation(currentState.getPhotoUri()), true);
        resizedImage.recycle();
        photo_IV.setImageBitmap(orientatedImage);

        /*Picasso.with(this).load(currentState.getPhotoUri()).placeholder(R.drawable.ic_account_circle_white_24dp).
                    error(R.drawable.ic_account_circle_white_24dp).into(photo_IV);*/
    }


    private String convertTagsListToStringWithHashes(ArrayList<String> tagsList) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < tagsList.size(); i++) {
            if (i < tagsList.size() - 1) {
                sb.append("#").append(tagsList.get(i)).append(" ");
            } else {
                sb.append("#").append(tagsList.get(i));
            }
        }
        return sb.toString();
    }


    private String convertTagsListToString(ArrayList<String> tagsList){
        StringBuilder sb = new StringBuilder();
        for (String tag : tagsList) {
            String text = tag + " ";
            sb.append(text);
        }
        return sb.toString();
    }

    private String convertTagsListToStringWithoutHash(ArrayList<String> tagsList){
        StringBuilder sb = new StringBuilder();
        for (int i=0; i < tagsList.size(); i++){
            if (i == tagsList.size() -1){
                String text = tagsList.get(i).replace("#","");
                sb.append(text);
            }else{
                String text = tagsList.get(i).replace("#","") + ",";
                sb.append(text);
            }
        }
        return sb.toString();
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        Log.d("UserPostFragment", "onActivityResult: " + data.toString());
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Log.d("UserPostFragment", "home pressed");
                return true;
            default:
                Log.d("UserPostFragment", "default");
                return super.onOptionsItemSelected(item);
        }
    }


    /*@OnClick(R.id.clearIV)
    public void onClearPressed() {
        addLocation_TV.setText(R.string.check_in);
        addressTV.setVisibility(View.GONE);
        clearIV.setVisibility(View.GONE);
    }*/

}
