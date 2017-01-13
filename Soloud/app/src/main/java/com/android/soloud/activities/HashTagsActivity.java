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
    private ArrayList<TagClass> defaultTagsList;
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

        if(savedInstanceState != null){
            contest = (Contest) savedInstanceState.getSerializable("contest");
            photoUri = savedInstanceState.getString("photoUri");
        }

        Contest.HashTag[] hashTags = null;
        if (getIntent().getSerializableExtra("contest") != null &&
                getIntent().getStringExtra("photoUri") != null){
            contest = (Contest) getIntent().getSerializableExtra("contest");

            photoUri = getIntent().getStringExtra("photoUri");
            hashTags = contest.getHashTags();
        }

        hashTag_ET = (EditText) findViewById(R.id.hashTag_ET);
        tagGroup = (TagView)findViewById(R.id.tag_group);
        description_ET = (EditText) findViewById(R.id.description_ET);

        //userTagsList = new ArrayList<>();

        if (hashTags != null){
            prepareDefaultTags(hashTags);
            showDefaultTags();
        }

        //You can add one tag
        //tagGroup.addTag(Tag tag);
        //You can add multiple tag via ArrayList
        //tagGroup.addTags(ArrayList<Tag> tags);
        //Via string array
        //addTags(String[] tags);

        hashTag_ET.setOnEditorActionListener(this);

        //set click listener
        tagGroup.setOnTagClickListener(new TagView.OnTagClickListener() {
            @Override
            public void onTagClick(Tag tag, int position) {
                /*hashTag_ET.setText(tag.text);
                hashTag_ET.setSelection(tag.text.length());*/ //to set cursor position
            }
        });

        //set delete listener
        tagGroup.setOnTagDeleteListener(new TagView.OnTagDeleteListener() {
            @Override
            public void onTagDeleted(final TagView view, final Tag tag, final int position) {
                view.remove(position);
            }
        });

        //set long click listener
        tagGroup.setOnTagLongClickListener(new TagView.OnTagLongClickListener() {
            @Override
            public void onTagLongClick(Tag tag, int position) {
            }
        });

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


    private void setTags(CharSequence cs) {
        /**
         * for empty edittext
         */
        if (cs.toString().equals("")) {
            tagGroup.addTags(new ArrayList<Tag>());
            return;
        }

        String text = cs.toString();
        ArrayList<Tag> tags = new ArrayList<>();
        Tag tag;


        for (int i = 0; i < defaultTagsList.size(); i++) {
            if (defaultTagsList.get(i).getName().toLowerCase().startsWith(text.toLowerCase())) {
                tag = new Tag(defaultTagsList.get(i).getName());
                tag.radius = 10f;
                tag.layoutColor = Color.parseColor(defaultTagsList.get(i).getColor());
                if (i % 2 == 0) // you can set deletable or not
                    tag.isDeletable = true;
                tags.add(tag);
            }
        }
        tagGroup.addTags(tags);

    }

    private void prepareTags() {
        defaultTagsList = new ArrayList<>();
        JSONArray jsonArray;
        JSONObject temp;
        try {
            jsonArray = new JSONArray(DEFAULT_TAGS);
            for (int i = 0; i < jsonArray.length(); i++) {
                temp = jsonArray.getJSONObject(i);
                defaultTagsList.add(new TagClass(temp.getString("code"),temp.getString("name")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void prepareDefaultTags(Contest.HashTag[] hashTagsArray){
        defaultTagsList = new ArrayList<>();
        for (int i = 0; i < hashTagsArray.length; i++) {
            String hashTag = "#" + hashTagsArray[i].getName();
            defaultTagsList.add(new TagClass(hashTag));
        }
    }

    private void showDefaultTags() {
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

    public static final String DEFAULT_TAGS = "[ \n" +
            "{\"name\": \"#Lorem\", \"code\": \"AF\"}, \n" +
            "{\"name\": \"#Ipsum\", \"code\": \"AX\"}, \n" +
            "{\"name\": \"#Dolor\", \"code\": \"AL\"}, \n" +
            "{\"name\": \"#Amet\", \"code\": \"DZ\"}, \n" +
            "{\"name\": \"#Elit\", \"code\": \"AS\"} \n" + "]";

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (EditorInfo.IME_ACTION_DONE == actionId) {
            addUserTagToList(hashTag_ET.getText().toString());
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
