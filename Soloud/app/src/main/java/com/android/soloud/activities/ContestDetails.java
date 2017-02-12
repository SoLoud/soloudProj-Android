package com.android.soloud.activities;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.soloud.R;
import com.android.soloud.SoLoudApplication;
import com.android.soloud.dialogs.ImagePreviewDialog;
import com.android.soloud.models.Contest;
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

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

import static com.android.soloud.activities.ContestsActivity.COMPANY_NAME;
import static com.android.soloud.activities.ContestsActivity.CONTEST;
import static com.android.soloud.activities.MainActivity.CONTEST_DETAILS_SN;

public class ContestDetails extends AppCompatActivity {

    public static final int REQUEST_IMAGE_CAPTURE = 1;
    public static final int PICK_IMAGE_FROM_GAL = 2;
    public static final String TAG = "AdvertisementDetails";
    private static final int REQUEST_CAMERA_AND_STORAGE = 3;
    private static final int REQUEST_READ_STORAGE = 4;

    private String photoUri;
    //private View mLayout;

    private Tracker mTracker;
    private static Contest contest;
    private ImageView prize_IV;
    private TextView prizeDescription_TV;
    String wreUri;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if(photoUri != null){
            outState.putString("photoUri", photoUri);
        }
        outState.putSerializable(CONTEST, contest);

        outState.putString("wreUri" , wreUri);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advertisement_details);

        prize_IV = (ImageView) findViewById(R.id.prize_IV);
        prizeDescription_TV = (TextView) findViewById(R.id.prize_description);
        TextView hashTags_TV = (TextView) findViewById(R.id.hashTags_TV);
        FloatingActionButton fab_camera = (FloatingActionButton) findViewById(R.id.menu_item_camera);
        FloatingActionButton fab_gallery = (FloatingActionButton) findViewById(R.id.menu_item_gallery);

        prize_IV.setOnClickListener(clickListener);
        fab_camera.setOnClickListener(clickListener);
        fab_gallery.setOnClickListener(clickListener);

        if (getIntent() != null) {
            String companyName = getIntent().getStringExtra(COMPANY_NAME);
            if (companyName != null){
                getSupportActionBar().setTitle(companyName);
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
            photoUri = savedInstanceState.getString("photoUri");
            contest = (Contest) savedInstanceState.getSerializable(CONTEST);
            wreUri = savedInstanceState.getString("wreUri");
        }

        //mLayout = findViewById(R.id.activity_advertisement_details);

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
            String requiredTags = contest.getmRequiredHashTags();
            String optionalTags = contest.getmOtionalHashTags();

            String requiredHashTags = splitInputAndAddHashTags(requiredTags);
            String optionalHashTags = splitInputAndAddHashTags(optionalTags);

            String hashTags = "";
            if (requiredHashTags != null){
                hashTags += requiredHashTags;
            }
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

    private boolean isNoE( final String s ) {
        // Null-safe, short-circuit evaluation.
        return s == null || s.trim().isEmpty();
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

                photoUri = Uri.fromFile(photoFile).toString();
            } catch (IOException ex) {
                Log.d(TAG, "dispatchTakePictureIntent: " + ex.toString());
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File file = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        //mCurrentPhotoPath = "file:" + image.getAbsolutePath();

        // Tell the media scanner about the new file so that it is
        // immediately available to the user.
        MediaScannerConnection.scanFile(this,
                new String[] { file.toString() }, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        Log.i("ExternalStorage", "Scanned " + path + ":");
                        Log.i("ExternalStorage", "-> uri=" + uri);

                        wreUri = uri.toString();
                    }
                });
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
            if(photoUri != null){
                //Uri imageToGalleryUri = addImageToGallery(photoUri, this);
                /*File file = new File(Uri.parse(photoUri).getPath());
                boolean deleted = file.delete();*/
                Intent intent = new Intent(this, HashTagsActivity.class);
                intent.putExtra("photoUri" ,wreUri);
                intent.putExtra("contest", contest);
                startActivity(intent);
                finish();
            }else{
                Toast.makeText(this, getString(R.string.please_try_again), Toast.LENGTH_LONG).show();
            }
        }

        if (requestCode == PICK_IMAGE_FROM_GAL && resultCode == RESULT_OK) {
            Uri selectedImage = data.getData();
            Intent intent = new Intent(this, HashTagsActivity.class);
            intent.putExtra("photoUri" ,selectedImage.toString());
            intent.putExtra("contest", contest);
            startActivity(intent);
            finish();

            /*InputStream imageStream = null;
            try {
                imageStream = getContentResolver().openInputStream(selectedImage);
                Bitmap yourSelectedImage = BitmapFactory.decodeStream(imageStream);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }*/
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


}
