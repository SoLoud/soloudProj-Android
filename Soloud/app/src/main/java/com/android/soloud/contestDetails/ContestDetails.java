package com.android.soloud.contestDetails;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.soloud.R;
import com.android.soloud.SoLoudApplication;
import com.android.soloud.activities.HashTagsActivity;
import com.android.soloud.contests.ContestsActivity;
import com.android.soloud.dialogs.ImagePreviewDialog;
import com.android.soloud.dialogs.ProgressDialog;
import com.android.soloud.models.Contest;
import com.android.soloud.models.CurrentState;
import com.android.soloud.utils.SharedPrefsHelper;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.enums.SnackbarType;
import com.nispok.snackbar.listeners.ActionClickListener;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.android.soloud.activities.MainActivity.CONTEST_DETAILS_SN;
import static com.android.soloud.contests.ContestsActivity.CONTEST;
import static com.android.soloud.contests.ContestsActivity.CURRENT_STATE;
import static com.android.soloud.utils.MyStringHelper.isNoE;
import static com.android.soloud.utils.SharedPrefsHelper.CONTEST_DETAILS_WIZARD_DISPLAYED;

public class ContestDetails extends AppCompatActivity {

    public static final int REQUEST_IMAGE_CAPTURE = 1;
    public static final int PICK_IMAGE_FROM_GAL = 2;
    public static final String TAG = "AdvertisementDetails";
    private static final int REQUEST_CAMERA_AND_STORAGE = 3;
    private static final int REQUEST_READ_STORAGE = 4;
    //private static final String PHOTO_URI = "photoUri";
    private static final String PHOTO_FILE = "photoFileString";

    //private String photoUri;
    private String  photoFileString;

    private Tracker mTracker;
    private static Contest contest;
    private CurrentState currentState;
    private ImageView prize_IV;
    private TextView prizeDescription_TV;
    private boolean mShowDialog = false;
    private RelativeLayout wizardRL;
    private RelativeLayout message1RL;
    private RelativeLayout message2RL;
    private RelativeLayout bubble1RL;
    private View triangle1View;
    private RelativeLayout bubble2RL;
    private View triangle2View;

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        //outState.putString(PHOTO_URI, photoUri);

        outState.putString(PHOTO_FILE, photoFileString);

        outState.putSerializable(CONTEST, contest);

        outState.putSerializable(CURRENT_STATE, currentState);

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advertisement_details);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        prize_IV = (ImageView) findViewById(R.id.prize_IV);
        prizeDescription_TV = (TextView) findViewById(R.id.prize_description);
        wizardRL = (RelativeLayout) findViewById(R.id.wizard_RL);
        message1RL = (RelativeLayout) findViewById(R.id.message_1_RL);
        message2RL = (RelativeLayout) findViewById(R.id.message_2_RL);
        bubble1RL = (RelativeLayout) findViewById(R.id.bubble_1_RL);
        triangle1View = findViewById(R.id.triangle_1_View);
        bubble2RL = (RelativeLayout) findViewById(R.id.bubble_2_RL);
        triangle2View = findViewById(R.id.triangle_2_View);
        TextView hashTags_TV = (TextView) findViewById(R.id.hashTags_TV);
        FloatingActionButton fab_camera = (FloatingActionButton) findViewById(R.id.menu_item_camera);
        FloatingActionButton fab_gallery = (FloatingActionButton) findViewById(R.id.menu_item_gallery);

        prize_IV.setOnClickListener(clickListener);
        fab_camera.setOnClickListener(clickListener);
        fab_gallery.setOnClickListener(clickListener);

        if (getIntent() != null) {

            currentState = (CurrentState) getIntent().getSerializableExtra(CURRENT_STATE);
            if (currentState != null) {
                if (!isNoE(currentState.getCompanyName())){
                    getSupportActionBar().setTitle(currentState.getCompanyName());
                }
            }
            contest = (Contest) getIntent().getSerializableExtra(CONTEST);

            displayExampleImage();
            displayPrizeDescription();
            String hashTags = prepareHashTags();
            if (!isNoE(hashTags)){
                hashTags_TV.setText(hashTags);
            }
        }

        if (savedInstanceState != null){
            //photoUri = savedInstanceState.getString(PHOTO_URI);
            contest = (Contest) savedInstanceState.getSerializable(CONTEST);
            currentState = (CurrentState) savedInstanceState.getSerializable(CURRENT_STATE);
            photoFileString = savedInstanceState.getString(PHOTO_FILE);
        }

        displayWizard();
        googleAnalyticsTrack();
    }


    private void googleAnalyticsTrack() {
        // Obtain the shared Tracker instance.
        SoLoudApplication application = (SoLoudApplication) getApplication();
        mTracker = application.getDefaultTracker();
    }

    private void showFullScreenImageDialog() {
        Contest.Photo[] photosArray = contest.getmExamplePhotos();
        if (photosArray.length > 0 && !isNoE(photosArray[0].getmUrl())){
            DialogFragment dialogFragment = ImagePreviewDialog.newInstance(photosArray[0].getmUrl());
            dialogFragment.show(getSupportFragmentManager(), "imagePreview");
        }
    }

    private void displayExampleImage() {
        Contest.Photo[] photosArray = contest.getmExamplePhotos();
        if (photosArray.length > 0 && !isNoE(photosArray[0].getmUrl())){
            Picasso.with(ContestDetails.this).load(photosArray[0].getmUrl()).placeholder(R.drawable.ic_view_list_white_24dp).
                    error(R.drawable.ic_view_list_white_24dp).into(prize_IV);
        }else{
            prize_IV.setImageResource(R.drawable.ic_view_list_white_24dp);
        }
    }

    private void displayPrizeDescription(){
        if (contest != null && !isNoE(contest.getmDescription())){
                prizeDescription_TV.setText(contest.getmDescription());
        }
    }

    private String prepareHashTags() {
        if (contest != null){
            //String requiredTags = contest.getmRequiredHashTags();
            String optionalTags = contest.getSuggestedHashTags();

            //String requiredHashTags = splitInputAndAddHashTags(requiredTags);
            String optionalHashTags = splitInputAndAddHashTags(optionalTags);

            String hashTags = "";
            /*if (requiredHashTags != null){
                hashTags += requiredHashTags;
            }*/
            if (optionalHashTags != null){
                if (!hashTags.isEmpty()){
                    hashTags += " " + optionalHashTags;
                }else{
                    hashTags += optionalHashTags;
                }
            }
            return hashTags;
        }
        return null;
    }

    private String splitInputAndAddHashTags(String input){
        StringBuilder sb;
        if (!isNoE(input)){
        String[] parts = input.split(",");
            sb = new StringBuilder();
            for(int i=0; i< parts.length; i++){
                if (i == parts.length -1){
                    parts[i] = "#" + parts[i].trim();
                    sb.append(parts[i]);
                    break;
                }
                parts[i] = "#" + parts[i].trim() + " ";
                sb.append(parts[i]);
            }
            return sb.toString();
        }
        return null;
    }


    @Override
    protected void onResume() {
        super.onResume();

        Log.i(TAG, "Setting screen name: " + CONTEST_DETAILS_SN);
        mTracker.setScreenName("Screen: " + CONTEST_DETAILS_SN);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.menu_item_camera:
                    checkForCameraAndWriteToStoragePermissions();
                    break;
                case R.id.menu_item_gallery:
                    // Open Gallery to pick image
                    checkForReadStoragePermission();
                    //openGallery();
                    break;
                case R.id.prize_IV:
                    showFullScreenImageDialog();
                    break;
            }
        }
    };


    private void saveImageInPicures(Bitmap finalBitmap) {
        String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
        /*File myDir = new File(root);
        myDir.mkdirs();*/
        Date d = new Date();
        CharSequence photo_name  = DateFormat.format("MM-dd-yy hh-mm-ss", d.getTime());
        String fname = photo_name +".jpg";
        File file = new File (root, fname);
        if (file.exists ()) file.delete ();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();

            // Tell the media scanner about the new file so that it is
            // immediately available to the user.
            MediaScannerConnection.scanFile(this,
                    new String[] { file.toString() }, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                            Log.i("ExternalStorage", "Scanned " + path + ":");
                            Log.i("ExternalStorage", "-> uri=" + uri);
                        }
                    });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    private void openCameraAndCreateFile() {
        File photoFile = null;
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            try {
                photoFile = createImageFile();

                photoFileString = photoFile.toString();

                //photoUri = Uri.fromFile(photoFile).toString();
            } catch (IOException ex) {
                Log.d(TAG, "dispatchTakePictureIntent: " + ex.toString());
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File file = File.createTempFile(
                timeStamp,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        //mCurrentPhotoPath = "file:" + image.getAbsolutePath();

        // Tell the media scanner about the new file so that it is
        // immediately available to the user.
        /*MediaScannerConnection.scanFile(this,
                new String[] { file.toString() }, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        Log.i("ExternalStorage", "Scanned " + path + ":");
                        Log.i("ExternalStorage", "-> uri=" + uri);

                        wreUri = uri.toString();
                    }
                });*/
        return file;
    }

    private void openGallery(){
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, PICK_IMAGE_FROM_GAL);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            if(photoFileString != null){
                mShowDialog = true;
                MediaScannerConnection.scanFile(this,
                        new String[] { photoFileString }, null,
                        new MediaScannerConnection.OnScanCompletedListener() {
                            public void onScanCompleted(String path, Uri uri) {
                                ///Log.i("ExternalStorage", "Scanned " + path + ":");
                                //Log.i("ExternalStorage", "-> uri=" + uri);

                                hideProgressDialog();
                                goToHashTagsActivity(uri);
                            }
                        });
            }else{
                Toast.makeText(this, getString(R.string.please_try_again), Toast.LENGTH_LONG).show();
            }
        }

        if (requestCode == PICK_IMAGE_FROM_GAL && resultCode == RESULT_OK) {
            Uri selectedImageUri = data.getData();

            goToHashTagsActivity(selectedImageUri);
        }
    }

    private void goToHashTagsActivity(Uri uri) {
        Intent intent = new Intent(ContestDetails.this, HashTagsActivity.class);
        intent.putExtra(CONTEST, contest);
        currentState.setPhotoUri(uri.toString());
        intent.putExtra(CURRENT_STATE, currentState);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();

        if (mShowDialog){
            showProgressDialog();
        }
    }

    private void showProgressDialog(){
        ProgressDialog progressDialog = new ProgressDialog();
        progressDialog.setCancelable(false);
        progressDialog.show(getSupportFragmentManager(), "progressDialog");
    }

    private void hideProgressDialog() {
        DialogFragment dialog = (DialogFragment) getSupportFragmentManager().findFragmentByTag("progressDialog");
        if(dialog != null){
            dialog.dismiss();
        }
    }

    public Uri addImageToGallery(final String filePath, final Context context) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.MediaColumns.DATA, filePath);

        return context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }

    private void checkForReadStoragePermission(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                SnackbarManager.show(
                        com.nispok.snackbar.Snackbar.with(ContestDetails.this)
                                .type(SnackbarType.SINGLE_LINE)
                                .text(R.string.permission_read_storage_rationale)
                                .duration(com.nispok.snackbar.Snackbar.SnackbarDuration.LENGTH_INDEFINITE)
                                .animation(false) // don't animate it
                                .swipeToDismiss(true)
                                .actionLabel(R.string.ok)
                                .actionListener(new ActionClickListener() {
                                    @Override
                                    public void onActionClicked(com.nispok.snackbar.Snackbar snackbar) {
                                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                                REQUEST_READ_STORAGE);
                                    }
                                })
                        , ContestDetails.this);

            } else {
                // No explanation needed, we can request the permission.
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_READ_STORAGE);
            }
        }else{
            openGallery();
        }
    }

    private void checkForCameraAndWriteToStoragePermissions(){
        String[] PERMISSIONS = {Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (!hasPermissions(ContestDetails.this, PERMISSIONS)) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                SnackbarManager.show(
                        com.nispok.snackbar.Snackbar.with(ContestDetails.this)
                                .type(SnackbarType.SINGLE_LINE)
                                .text(R.string.permission_camera_and_storage_rationale)
                                .duration(com.nispok.snackbar.Snackbar.SnackbarDuration.LENGTH_INDEFINITE)
                                .animation(false) // don't animate it
                                .swipeToDismiss(true)
                                .actionLabel(R.string.ok)
                                .actionListener(new ActionClickListener() {
                                    @Override
                                    public void onActionClicked(com.nispok.snackbar.Snackbar snackbar) {
                                        requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                                REQUEST_CAMERA_AND_STORAGE);
                                    }
                                })
                        , ContestDetails.this);

            } else {
                // No explanation needed, we can request the permission.
                requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_CAMERA_AND_STORAGE);
            }
        }else{
            openCameraAndCreateFile();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CAMERA_AND_STORAGE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    displayMessage(SnackbarType.SINGLE_LINE, getResources().getString(R.string.permission_granted), Color.parseColor("#ff323232"));
                    openCameraAndCreateFile();
                } else {
                    displayMessage(SnackbarType.SINGLE_LINE, getResources().getString(R.string.permission_not_granted), Color.parseColor("#ff323232"));
                }
            }
            break;
            case REQUEST_READ_STORAGE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    displayMessage(SnackbarType.SINGLE_LINE, getResources().getString(R.string.permission_granted), Color.parseColor("#ff323232"));
                    openGallery();
                } else {
                    displayMessage(SnackbarType.SINGLE_LINE, getResources().getString(R.string.permission_not_granted), Color.parseColor("#ff323232"));
                }
                break;
        }
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    private void displayMessage(SnackbarType type, String message, int color){
        SnackbarManager.show(
                com.nispok.snackbar.Snackbar.with(ContestDetails.this)
                        .type(type)
                        .text(message)
                        .color(color)
                        .duration(com.nispok.snackbar.Snackbar.SnackbarDuration.LENGTH_LONG)
                        .animation(false) // don't animate it
                        .swipeToDismiss(true)
                , ContestDetails.this);
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ContestDetails.this, ContestsActivity.class);
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

    private void displayWizard() {
        boolean wizardDisplayed = SharedPrefsHelper.getBooleanFromPrefs(this, CONTEST_DETAILS_WIZARD_DISPLAYED);
        if (!wizardDisplayed) {
            wizardRL.setVisibility(View.VISIBLE);
            displayWithAnimation(bubble1RL);
            displayWithAnimation(triangle1View);
            message1RL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.setVisibility(View.GONE);
                    message2RL.setVisibility(View.VISIBLE);
                    displayWithAnimation(bubble2RL);
                    displayWithAnimation(triangle2View);
                }
            });
        }
        message2RL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wizardRL.setVisibility(View.GONE);
                //SharedPrefsHelper.storeBooleanInPrefs(ContestDetails.this, true, CONTEST_DETAILS_WIZARD_DISPLAYED);
            }
        });
    }

    private void displayWithAnimation(View view) {
        AlphaAnimation animation1 = new AlphaAnimation(0.0f, 1.0f);
        animation1.setDuration(1000);
        animation1.setStartOffset(100);
        animation1.setFillAfter(true);
        view.startAnimation(animation1);
    }
}
