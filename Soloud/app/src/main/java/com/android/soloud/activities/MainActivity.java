package com.android.soloud.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.soloud.R;
import com.android.soloud.SoLoudApplication;
import com.android.soloud.fragments.CategoriesFragment;
import com.android.soloud.fragments.GalleryFragment;
import com.android.soloud.materialnavigationdrawer.MaterialNavigationDrawer;
import com.android.soloud.materialnavigationdrawer.elements.MaterialSection;
import com.android.soloud.materialnavigationdrawer.elements.listeners.MaterialSectionListener;
import com.android.soloud.profile.ProfileActivity;
import com.android.soloud.profile.UserProfileActivity;
import com.android.soloud.training.TrainingFragment;
import com.android.soloud.utils.LogoutHelper;
import com.android.soloud.utils.SharedPrefsHelper;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class MainActivity extends MaterialNavigationDrawer{

    public static final String CATEGORIES = "Categories";
    public static final String PROFILE = "Profile";
    public static final String TRAINING = "Training";
    public static final String GALLERY = "Gallery";
    public static final String LOGOUT = "Logout";
    private static final String TAG = "MainActivity";

    private Tracker mTracker;

    public static final String CATEGORIES_SN = "Categories";
    public static final String CATEGORY_CONTESTS_SN = "Category Contests";
    public static final String CONTEST_DETAILS_SN = "Contest Details";
    public static final String TAGS_SN = "Tags";
    public static final String POST_SN = "Post Preview";
    public static final String USER_PROFILE_SN = "User Profile";
    public static final String TUTORIAL_SN = "Tutorial";

    @Override
    public void init(Bundle savedInstanceState) {

        View view = LayoutInflater.from(this).inflate(R.layout.custom_drawer, null);
        TextView name_TV = (TextView) view.findViewById(R.id.user_name);
        ImageView profilePicture_IV = (ImageView) view.findViewById(R.id.profile_IV);

        profilePicture_IV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        name_TV.setText(SharedPrefsHelper.getFromPrefs(this, SharedPrefsHelper.USER_NAME));
        Picasso.with(this).load(SharedPrefsHelper.getFromPrefs(this, SharedPrefsHelper.USER_PROFILE_PICTURE_URL)).placeholder(R.drawable.ic_account_circle_white_24dp).
                error(R.drawable.ic_account_circle_white_24dp).transform(new CropCircleTransformation()).into(profilePicture_IV);

        setDrawerHeaderCustom(view);
        this.allowArrowAnimation();

        int primaryColor = ContextCompat.getColor(this, R.color.colorPrimary);
        this.addSection(newSection(CATEGORIES, R.drawable.ic_view_list_white_24dp, new CategoriesFragment()).setSectionColor(primaryColor));

        Intent intent = new Intent(this, UserProfileActivity.class);
        this.addSection(newSection(PROFILE, R.drawable.ic_account_circle_white_24dp, intent));

        this.addSection(newSection(GALLERY, R.drawable.ic_collections_white_24, new GalleryFragment()).setSectionColor(primaryColor));

        this.addSection(newSection(TRAINING, R.drawable.ic_import_contacts_white_24dp, new TrainingFragment()).setSectionColor(primaryColor));


        /*MaterialSection training = this.newSection(TRAINING, R.drawable.ic_import_contacts_white_24dp, new MaterialSectionListener() {
            @Override
            public void onClick(MaterialSection section) {
                section.unSelect();
                Intent intent = new Intent(MainActivity.this, TutorialActivity.class);
                startActivity(intent);
            }
        });

        this.addSection(training);*/

        MaterialSection sign_out = this.newSection(LOGOUT, new MaterialSectionListener() {
            @Override
            public void onClick(MaterialSection section) {
                section.unSelect();

                LogoutHelper logoutHelper = new LogoutHelper(MainActivity.this);
                logoutHelper.logOut();
            }
        });
        this.addBottomSection(sign_out);

        this.enableToolbarElevation();
        this.setBackPattern(BACKPATTERN_BACK_TO_FIRST);

        /*MediaPlayer mPlayer = MediaPlayer.create(this, R.raw.sosoloud_sound);
        mPlayer.start();*/

        // Obtain the shared Tracker instance.
        SoLoudApplication application = (SoLoudApplication) getApplication();
        mTracker = application.getDefaultTracker();

    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.i(TAG, "Setting screen name: " + CATEGORIES_SN);
        mTracker.setScreenName("Screen: " + CATEGORIES_SN);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }



    private Bitmap getFacebookProfilePicture(String url){
        URL facebookProfileURL= null;
        try {
            facebookProfileURL = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(facebookProfileURL.openConnection().getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }
}
