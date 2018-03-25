package com.android.soloud.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

import com.android.soloud.R;
import com.android.soloud.SoLoudApplication;
import com.android.soloud.contestDetails.ContestDetails;
import com.android.soloud.facebookPlaces.CheckInActivity;
import com.android.soloud.models.Contest;
import com.android.soloud.models.CurrentState;
import com.android.soloud.utils.MyStringHelper;
import com.cunoraz.tagview.Tag;
import com.cunoraz.tagview.TagView;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.android.soloud.contests.ContestsActivity.CONTEST;
import static com.android.soloud.contests.ContestsActivity.CURRENT_STATE;
import static com.android.soloud.activities.MainActivity.TAGS_SN;
import static com.android.soloud.utils.MyStringHelper.isNoE;

public class HashTagsActivity extends AppCompatActivity {

    private static final String TAG = "HashTagsActivity";
    private Tracker mTracker;
    private Contest contest;
    private CurrentState currentState;
    private int secondaryColor;

    @BindView(R.id.hashTag_ET) EditText hashTag_ET;
    @BindView(R.id.description_ET) EditText description_ET;
    @BindView(R.id.tag_group) TagView tagGroup;

    private String userInputHashTagsString;


    @Override
    protected void onSaveInstanceState(Bundle outState) {

        outState.putSerializable(CONTEST, contest);
        outState.putSerializable(CURRENT_STATE, currentState);

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hash_tags);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ButterKnife.bind(this);

        secondaryColor = ContextCompat.getColor(this, R.color.mySecondary);
        userInputHashTagsString = "";

        if (getIntent() != null && getIntent().getSerializableExtra(CONTEST) != null &&
                getIntent().getSerializableExtra(CURRENT_STATE) != null){
            currentState = (CurrentState) getIntent().getSerializableExtra(CURRENT_STATE);
            contest = (Contest) getIntent().getSerializableExtra(CONTEST);
        }

        if(savedInstanceState != null){
            contest = (Contest) savedInstanceState.getSerializable(CONTEST);
            currentState = (CurrentState) savedInstanceState.getSerializable(CURRENT_STATE);
            userInputHashTagsString = savedInstanceState.getString("userInputHashTagsString");
            Log.d(TAG, "savedInstanceState != null");
        }

        String suggestedHashTags = contest.getSuggestedHashTags();
        ArrayList<String> userHashTagsList = currentState.getUserHashTagsList();

        // It means that the user comes from Check in Activity and has already selected hash tags
        if (userHashTagsList != null && userHashTagsList.size()>0){
            userInputHashTagsString = getTagsTextFromList(userHashTagsList);
            hashTag_ET.setText(userInputHashTagsString);
            hashTag_ET.setSelection(hashTag_ET.getText().length());
        }
        else if (!MyStringHelper.isNoE(userInputHashTagsString)) {
            hashTag_ET.setText(userInputHashTagsString);
            hashTag_ET.setSelection(hashTag_ET.getText().length());
        }

        initializeTagsUI(suggestedHashTags);

        String description = currentState.getUserPostDescription();
        description_ET.setText(description);

        hashTag_ET.addTextChangedListener(editTextTextWatcher);

        tagGroup.setOnTagClickListener(new TagView.OnTagClickListener() {
            @Override
            public void onTagClick(Tag tag, int i) {
                updateEditText(tag.text);
            }
        });

        googleAnalyticsTrack();
    }


    private String getTagsTextFromList(ArrayList<String> list) {
        StringBuilder text = new StringBuilder("");
        for (String tag : list) {
            text.append("#").append(tag.trim()).append(" ");
        }
        return text.toString();
    }


    private void updateEditText(String input){
        if (userInputHashTagsString.length() > 0){
            char lastChar = userInputHashTagsString.charAt(userInputHashTagsString.length()-1) ;
            if (lastChar == ' '){
                userInputHashTagsString += input;
            }else{
                userInputHashTagsString += " " + input;
            }
        }else{
            userInputHashTagsString += input;
        }
        hashTag_ET.setText(userInputHashTagsString);
        hashTag_ET.setSelection(hashTag_ET.getText().length());
    }

    private void initializeTagsUI(String tags) {
        if (!isNoE(tags)){
            String[] parts = tags.split(",");
            for (String tag : parts) {
                addUserTagToList("#" + tag);
            }
        }
    }


    private void googleAnalyticsTrack() {
        // Obtain the shared Tracker instance.
        SoLoudApplication application = (SoLoudApplication) getApplication();
        mTracker = application.getDefaultTracker();
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.i(TAG, "Setting screen name: " + TAGS_SN);
        mTracker.setScreenName("Screen: " + TAGS_SN);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }


    private void addUserTagToList(String userInput){
        Tag userTag = new Tag(userInput.trim());
        userTag.radius = 10f;

        userTag.layoutColor = secondaryColor;
        userTag.isDeletable = false;
        tagGroup.addTag(userTag);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.hashtags_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_proceed:
                Intent intent = new Intent(HashTagsActivity.this, CheckInActivity.class);
                intent.putExtra(CONTEST, contest);
                currentState.setUserHashTagsList(getHashTags());
                currentState.setUserPostDescription(description_ET.getText().toString());
                intent.putExtra(CURRENT_STATE, currentState);
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private ArrayList<String> getHashTags(){
        ArrayList<String> tagsArrayList = new ArrayList<>();
        if (userInputHashTagsString.length() >0){
            String[] parts = userInputHashTagsString.split("#");
            if (parts.length > 0){
                for (int i=0; i<parts.length; i++){
                    String tag = parts[i];
                    if (!MyStringHelper.isNoE(tag))
                    tagsArrayList.add(tag);
                }
            }
        }
        return tagsArrayList;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(HashTagsActivity.this, ContestDetails.class);
        intent.putExtra(CONTEST, contest);
        currentState.setUserHashTagsList(getHashTags());
        currentState.setUserPostDescription(description_ET.getText().toString());
        intent.putExtra(CURRENT_STATE, currentState);
        startActivity(intent);
        finish();
    }


    private TextWatcher editTextTextWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            userInputHashTagsString = s.toString();
        }
    };
}
