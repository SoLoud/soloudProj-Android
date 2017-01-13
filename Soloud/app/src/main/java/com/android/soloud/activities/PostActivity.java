package com.android.soloud.activities;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentManager;
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
import com.android.soloud.apiCalls.PostService;
import com.android.soloud.apiCalls.PostUserPhoto;
import com.android.soloud.dialogs.UserPostDialog;
import com.android.soloud.utils.SharedPrefsHelper;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.util.ArrayList;

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

    private Button shareButton;
    private Tracker mTracker;

    private Target mTarget;
    private ImageView photo_IV;
    private Bitmap bitmap1;
    private String photoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        TextView post_info_TV = (TextView) findViewById(R.id.post_info_TV);
        photo_IV = (ImageView) findViewById(R.id.post_photo_IV);
        shareButton = (Button) findViewById(R.id.share_button);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showShareDialog();
            }
        });

        ArrayList<String> tagsList = new ArrayList<>();
        String description = "";
        photoUri = "";

        if(getIntent() != null && getIntent().getStringExtra("description") !=null &&
                getIntent().getStringArrayListExtra("hashTagsList") != null &&
                getIntent().getStringExtra("photoUri") != null){

            description = getIntent().getStringExtra("description");
            tagsList = getIntent().getStringArrayListExtra("hashTagsList");
            photoUri = getIntent().getStringExtra("photoUri");
            Log.d(TAG, "onCreate: " + description + ", " + tagsList.toString());


            String tags = tagsList.toString();
            int tagsSize = tags.length();
            String tagsWithoutBrackets = tags.substring(1,tagsSize-1);
            /*hashTags_TV.setText(tagsWithoutBrackets);
            description_TV.setText(description);*/

            String sourceString = description +" " +"<b>" + tagsWithoutBrackets + "</b> ";
            post_info_TV.setText(Html.fromHtml(sourceString));

            Picasso.with(this).load(photoUri).placeholder(R.drawable.ic_account_circle_white_24dp).
                    error(R.drawable.ic_account_circle_white_24dp).into(photo_IV);

            //loadImage(this, photoUri);
        }

        // Obtain the shared Tracker instance.
        SoLoudApplication application = (SoLoudApplication) getApplication();
        mTracker = application.getDefaultTracker();

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
                    Toast.makeText(PostActivity.this, "Successful post!",Toast.LENGTH_SHORT).show();
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
        //initPostsService("test post2");
        initPostUserPhotoService(photoUri, "test image post");
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


}
