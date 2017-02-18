package com.android.soloud.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import java.util.List;

import static com.android.soloud.activities.ContestsActivity.CONTEST;
import static com.android.soloud.activities.ContestsActivity.CURRENT_STATE;
import static com.android.soloud.activities.MainActivity.TAGS_SN;
import static com.android.soloud.utils.MyStringHelper.isNoE;

public class HashTagsActivity extends AppCompatActivity implements TextView.OnEditorActionListener {

    private static final String TAG = "HashTagsActivity";
    private TagView tagGroup;
    private EditText hashTag_ET;
    private EditText description_ET;
    //private ArrayList<TagClass> defaultTagsList;
    //private ArrayList<TagClass> userTagsList;
    private Tracker mTracker;
    private Contest contest;
    private CurrentState currentState;


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

        hashTag_ET = (EditText) findViewById(R.id.hashTag_ET);
        tagGroup = (TagView)findViewById(R.id.tag_group);
        description_ET = (EditText) findViewById(R.id.description_ET);

        if(savedInstanceState != null){
            contest = (Contest) savedInstanceState.getSerializable(CONTEST);
            currentState = (CurrentState) savedInstanceState.getSerializable(CURRENT_STATE);
        }

        String hashTags = "";
        if (getIntent() != null && getIntent().getSerializableExtra(CONTEST) != null &&
                getIntent().getSerializableExtra(CURRENT_STATE) != null){
            currentState = (CurrentState) getIntent().getSerializableExtra(CURRENT_STATE);
            contest = (Contest) getIntent().getSerializableExtra(CONTEST);
            String requiredTags = contest.getmRequiredHashTags();
            String optionalTags = contest.getmOptionalHashTags();
            String userHashTags = currentState.getUserHashTags();

            if (!isNoE(requiredTags)){
                ArrayList<TagClass> tags = prepareRequiredTags(requiredTags);
                showRequiredTags(tags);
            }

            if (!isNoE(optionalTags)){
                String[] parts = optionalTags.split(",");
                for (String tag : parts) {
                    addUserTagToList("#" + tag);
                }
            }

            // TODO: 16/2/2017 Na arxikopoiw sta hashtags kai auta pou exei balei o xristis se periptwsi pou proerxetai apo tin post activity
            if (!isNoE(userHashTags)){
                String[] parts = userHashTags.split(",");
                for (String tag : parts) {
                    addUserTagToList("#" + tag);
                }
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
            }
        });

        googleAnalyticsTrack();
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

        }
        tagGroup.addTags(tags);
    }

    private void addUserTagToList(String userInput){
        Tag userTag = new Tag(userInput.trim());
        userTag.radius = 10f;
        userTag.layoutColor = Color.parseColor("#0e94a5");
        userTag.isDeletable = true;
        tagGroup.addTag(userTag);
    }


    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (EditorInfo.IME_ACTION_DONE == actionId) {
            String userInput = hashTag_ET.getText().toString().trim();
            if (!userInput.startsWith("#")){
                addUserTagToList("#" + userInput);
                String userTags = currentState.getUserHashTags();
                if (MyStringHelper.isNoE(userTags)){
                    userTags = userInput + ",";
                }else{
                    userTags += userInput + ",";
                }
                currentState.setUserHashTags(userTags);
            }else{
                addUserTagToList(userInput);
                String userTags = currentState.getUserHashTags();
                if (MyStringHelper.isNoE(userTags)){
                    userTags = userInput.substring(1) + ",";
                }else{
                    userTags += userInput.substring(1) + ",";
                }
                currentState.setUserHashTags(userTags);
            }
            hashTag_ET.setText("#");
            hashTag_ET.setSelection(1);
            return true;
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
            case R.id.action_proceed:
                Intent intent = new Intent(HashTagsActivity.this, PostActivity.class);
                intent.putExtra(CONTEST, contest);
                intent.putStringArrayListExtra("hashTagsList",getHashTags());
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
        intent.putExtra(CURRENT_STATE, currentState);
        startActivity(intent);
        finish();
    }
}
