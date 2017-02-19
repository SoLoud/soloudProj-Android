package com.android.soloud.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.android.soloud.R;
import com.android.soloud.SoLoudApplication;
import com.android.soloud.models.Contest;
import com.android.soloud.models.CurrentState;
import com.android.soloud.models.TagClass;
import com.android.soloud.utils.MyStringHelper;
import com.cunoraz.tagview.Tag;
import com.cunoraz.tagview.TagView;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.android.soloud.activities.ContestsActivity.CONTEST;
import static com.android.soloud.activities.ContestsActivity.CURRENT_STATE;
import static com.android.soloud.activities.MainActivity.TAGS_SN;
import static com.android.soloud.utils.MyStringHelper.isNoE;

public class HashTagsActivity extends AppCompatActivity implements TextView.OnEditorActionListener {

    private static final String TAG = "HashTagsActivity";
    private static final String ALL_TAGS_LIST = "allTagsList";
    private TagView tagGroup;
    private EditText hashTag_ET;
    private EditText description_ET;
    //private ArrayList<TagClass> defaultTagsList;
    //private ArrayList<TagClass> userTagsList;
    private Tracker mTracker;
    private Contest contest;
    private CurrentState currentState;
    private ArrayList<String> allTagsList;


    @Override
    protected void onSaveInstanceState(Bundle outState) {

        outState.putSerializable(CONTEST, contest);
        outState.putSerializable(CURRENT_STATE, currentState);
        //outState.putStringArrayList(ALL_TAGS_LIST, allTagsList);

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hash_tags);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        hashTag_ET = (EditText) findViewById(R.id.hashTag_ET);
        tagGroup = (TagView)findViewById(R.id.tag_group);
        description_ET = (EditText) findViewById(R.id.description_ET);



        String hashTags = "";
        if (getIntent() != null && getIntent().getSerializableExtra(CONTEST) != null &&
                getIntent().getSerializableExtra(CURRENT_STATE) != null){
            currentState = (CurrentState) getIntent().getSerializableExtra(CURRENT_STATE);
            contest = (Contest) getIntent().getSerializableExtra(CONTEST);

            if(savedInstanceState != null){
                contest = (Contest) savedInstanceState.getSerializable(CONTEST);
                currentState = (CurrentState) savedInstanceState.getSerializable(CURRENT_STATE);
                //allTagsList = savedInstanceState.getStringArrayList(ALL_TAGS_LIST);

                Log.d(TAG, "savedInstanceState != null");
            }

            String requiredTags = contest.getmRequiredHashTags();
            String optionalTags = contest.getmOptionalHashTags();
            String userHashTags = currentState.getUserHashTags();
            Log.d(TAG, "code run");

            allTagsList = new ArrayList<>();

            // It means that the user comes from Post Activity
            if (userHashTags != null){
                if (!isNoE(requiredTags)){
                    ArrayList<TagClass> tags = prepareRequiredTags(requiredTags);
                    showRequiredTags(tags);
                }
                addTagsToList(userHashTags);

            }else{
                if (!isNoE(requiredTags)){
                    ArrayList<TagClass> tags = prepareRequiredTags(requiredTags);
                    showRequiredTags(tags);
                }
                addTagsToList(optionalTags);
                //addTagsToList(userHashTags);
            }

            String description = currentState.getUserPostDescription();
            description_ET.setText(description);
        }

        hashTag_ET.setOnEditorActionListener(this);

        //set delete listener
        tagGroup.setOnTagDeleteListener(new TagView.OnTagDeleteListener() {
            @Override
            public void onTagDeleted(final TagView view, final Tag tag, final int position) {
                view.remove(position);
                allTagsList.remove(position);
            }
        });

        description_ET.addTextChangedListener(textWatcher);

        googleAnalyticsTrack();
    }

    private void addTagsToList(String tags) {
        if (!isNoE(tags)){
            String[] parts = tags.split(",");
            for (String tag : parts) {
                addUserTagToList("#" + tag);
            }
        }
    }


    private List<String> convertStringHashTagsToArrayList(String tags){
        List<String> tagsList = null;
        if (!MyStringHelper.isNoE(tags)){
            String[] parts = tags.split(",");
            tagsList = Arrays.asList(parts);
        }
        return tagsList;
    }

    private String convertTagsListToString(ArrayList<String> tagsList){
        StringBuilder sb = new StringBuilder();
        for (int i=0; i <tagsList.size(); i++) {
            String text;
            if (i == tagsList.size() -1){
                text = tagsList.get(i);
            }else{
                text = tagsList.get(i) + ",";
            }
            sb.append(text);
        }
        return sb.toString();
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

    private ArrayList<TagClass> prepareRequiredTags(String hashTags){
        String[] hashTagsArray = hashTags.split(",");
        ArrayList<TagClass> defaultTagsList = new ArrayList<>();
        for (int i = 0; i < hashTagsArray.length; i++) {
            String hashTag = "#" + hashTagsArray[i];
            defaultTagsList.add(new TagClass(hashTag));
        }
        return defaultTagsList;
    }

    private void showRequiredTags(ArrayList<TagClass> defaultTagsList) {
        ArrayList<Tag> tags = new ArrayList<>();
        Tag tag;
        for (int j = 0; j < defaultTagsList.size(); j++) {
            tag = new Tag(defaultTagsList.get(j).getName());
            tag.radius = 10f;
            tag.layoutColor = Color.parseColor("#e6004c");
            if (j % 2 == 0) // you can set deletable or not
                tag.isDeletable = false;
            tags.add(tag);

            String text = tag.text;
            if (text.startsWith("#")){
                allTagsList.add(text.substring(1));
            }else{
                allTagsList.add(text);
            }
        }
        tagGroup.addTags(tags);
    }

    private void addUserTagToList(String userInput){
        Tag userTag = new Tag(userInput.trim());
        userTag.radius = 10f;
        userTag.layoutColor = Color.parseColor("#0e94a5");
        userTag.isDeletable = true;
        tagGroup.addTag(userTag);

        if (userInput.startsWith("#")){
            allTagsList.add(userInput.substring(1));
        }else{
            allTagsList.add(userInput);
        }
    }


    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (EditorInfo.IME_ACTION_DONE == actionId) {
            String userInput = hashTag_ET.getText().toString().trim();
            if (userInput.trim().isEmpty() || userInput.trim().equals("#")){
                return false;
            }
            else
            {
                if (!userInput.startsWith("#")){
                    addUserTagToList("#" + userInput);
                }else{
                    addUserTagToList(userInput);
                }
                hashTag_ET.setText("#");
                hashTag_ET.setSelection(1);
                return true;
            }
        }
        return false;
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
                Intent intent = new Intent(HashTagsActivity.this, PostActivity.class);
                intent.putExtra(CONTEST, contest);
                intent.putStringArrayListExtra("hashTagsList",getHashTags());
                removeRequiredHashTags();
                currentState.setUserHashTags(convertTagsListToString(allTagsList));
                currentState.setUserPostDescription(description_ET.getText().toString());
                intent.putExtra(CURRENT_STATE, currentState);
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void removeRequiredHashTags(){
        String requiredHashTags = contest.getmRequiredHashTags();
        if (!MyStringHelper.isNoE(requiredHashTags)){
            String[] parts = requiredHashTags.split(",");
            for (int i=0; i<parts.length; i++){
                allTagsList.remove(0);
            }
        }
    }

    private ArrayList<String> getHashTags(){
        List<Tag> tagList = tagGroup.getTags();
        ArrayList<String> tagsArrayList = new ArrayList<>();
        for (Tag tag : tagList) {
            tagsArrayList.add(tag.text);
        }
        return tagsArrayList;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(HashTagsActivity.this, ContestDetails.class);
        intent.putExtra(CONTEST, contest);
        removeRequiredHashTags();
        currentState.setUserHashTags(convertTagsListToString(allTagsList));
        currentState.setUserPostDescription(description_ET.getText().toString());
        intent.putExtra(CURRENT_STATE, currentState);
        startActivity(intent);
        finish();
    }

    private TextWatcher textWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            currentState.setUserPostDescription(s.toString());
        }
    };
}
