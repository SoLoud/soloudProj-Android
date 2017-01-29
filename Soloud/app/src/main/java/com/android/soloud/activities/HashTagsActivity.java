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
import com.android.soloud.models.TagClass;
import com.cunoraz.tagview.Tag;
import com.cunoraz.tagview.TagView;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.android.soloud.activities.MainActivity.TAGS_SN;

public class HashTagsActivity extends AppCompatActivity implements TextView.OnEditorActionListener {

    private static final String TAG = "HashTagsActivity";
    private TagView tagGroup;
    private EditText hashTag_ET;
    private EditText description_ET;
    //private ArrayList<TagClass> defaultTagsList;
    //private ArrayList<TagClass> userTagsList;
    private String photoUri;
    private Tracker mTracker;
    private Contest contest;

    //private Contest.HashTag[] hashTags;


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable("contest", contest);
        outState.putString("photoUri", photoUri);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hash_tags);

        hashTag_ET = (EditText) findViewById(R.id.hashTag_ET);
        tagGroup = (TagView)findViewById(R.id.tag_group);
        description_ET = (EditText) findViewById(R.id.description_ET);

        if(savedInstanceState != null){
            contest = (Contest) savedInstanceState.getSerializable("contest");
            photoUri = savedInstanceState.getString("photoUri");
        }

        String hashTags = "";
        if (getIntent().getSerializableExtra("contest") != null &&
                getIntent().getStringExtra("photoUri") != null){
            contest = (Contest) getIntent().getSerializableExtra("contest");

            photoUri = getIntent().getStringExtra("photoUri");
            String requiredTags = contest.getmRequiredHashTags();
            String optionalTags = contest.getmOtionalHashTags();

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


        }


        hashTag_ET.setOnEditorActionListener(this);

        //set delete listener
        tagGroup.setOnTagDeleteListener(new TagView.OnTagDeleteListener() {
            @Override
            public void onTagDeleted(final TagView view, final Tag tag, final int position) {
                view.remove(position);
            }
        });

        // Obtain the shared Tracker instance.
        SoLoudApplication application = (SoLoudApplication) getApplication();
        mTracker = application.getDefaultTracker();
    }

    private boolean isNoE( final String s ) {
        // Null-safe, short-circuit evaluation.
        return s == null || s.trim().isEmpty();
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
        Tag userTag = new Tag(userInput);
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
            }else{
                addUserTagToList(userInput);
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
                intent.putStringArrayListExtra("hashTagsList",getHashTags());
                intent.putExtra("description",description_ET.getText().toString());
                intent.putExtra("photoUri", photoUri);
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
}
