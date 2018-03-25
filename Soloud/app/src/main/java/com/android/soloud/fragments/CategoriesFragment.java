package com.android.soloud.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.soloud.R;
import com.android.soloud.contests.ContestsActivity;
import com.android.soloud.adapters.CategoryAdapter;
import com.android.soloud.materialnavigationdrawer.MaterialNavigationDrawer;
import com.android.soloud.models.Category;
import com.android.soloud.utils.NetworkStatusHelper;
import com.android.soloud.utils.SharedPrefsHelper;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.android.soloud.utils.SharedPrefsHelper.CATEGORIES_WIZARD_DISPLAYED;

/**
 * Created by f.stamopoulos on 9/10/2016.
 */

public class CategoriesFragment extends Fragment {

    public static final String CONTEST_NAME = "ContestName";
    private ListView listView;
    private ArrayList<Category> categoriesList;
    private RelativeLayout wizardRL;
    private RelativeLayout bubbleRL;
    private View triangleView;


    public CategoriesFragment(){
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        initializeList();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_categories, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //mRecyclerView = (RecyclerView) view.findViewById(R.id.categories_recycler_view);
        listView = (ListView) view.findViewById(R.id.listView);
        wizardRL = (RelativeLayout) view.findViewById(R.id.wizard_RL);
        bubbleRL = (RelativeLayout) view.findViewById(R.id.bubble_RL);
        triangleView = view.findViewById(R.id.triangle_View);

    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initializeToolbar();

        listView.setAdapter(new CategoryAdapter(getActivity(), categoriesList));
        listView.setOnItemClickListener(onItemClickListener);

        displayWizard();
    }

    AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String contestName = categoriesList.get(position).getName();
            Intent intent = new Intent(getActivity(), ContestsActivity.class);
            intent.putExtra(CONTEST_NAME, contestName);
            startActivity(intent);
            getActivity().finish();
        }
    };

    private void initializeToolbar() {
        Toolbar toolbar = ((MaterialNavigationDrawer)this.getActivity()).getToolbar();
        ((MaterialNavigationDrawer)this.getActivity()).setSupportActionBar(toolbar);
        ((MaterialNavigationDrawer)this.getActivity()).getSupportActionBar().show();
        ((MaterialNavigationDrawer)this.getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onResume() {
        super.onResume();

        testMethod();
    }


    @Override
    public void onPause() {

        /*((MaterialNavigationDrawer) getActivity()).unlockSlideMenu();
        myWebView.onPause();*/
        super.onPause();
    }



    private void testMethod(){
        if (NetworkStatusHelper.isNetworkAvailable(getActivity()) && AccessToken.getCurrentAccessToken() != null){
            String oldAccessToken = AccessToken.getCurrentAccessToken().getToken();
            getGraphRequestMe(AccessToken.getCurrentAccessToken());
            String newAccessToken = AccessToken.getCurrentAccessToken().getToken();
            if (!oldAccessToken.equals(newAccessToken)){
                Toast.makeText(getActivity(), "The token has changed!!!", Toast.LENGTH_SHORT).show();
                Log.d("CategoriesFragment", "The token has changed!!!");
            }else{
                Toast.makeText(getActivity(), "Same token", Toast.LENGTH_SHORT).show();
                Log.d("CategoriesFragment", "Same token");
            }
        }
    }

    @NonNull
    private GraphRequest getGraphRequestMe(final AccessToken accessToken) {
        return GraphRequest.newMeRequest(
                accessToken,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.d("CategoriesFragment", response.toString());

                        // Application code
                        try {
                            String name = object.getString("name");
                            String email = object.getString("email");
                            //String picture = object.getString("picture");
                            //String birthday = object.getString("birthday"); // 01/31/1980 format
                            String profilePicUrl = object.getJSONObject("picture").getJSONObject("data").getString("url");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void initializeList() {
        categoriesList = new ArrayList<>();
        categoriesList.add(new Category("Charity", R.drawable.charity));
        categoriesList.add(new Category("Cosmetics", R.drawable.cosmetics));
        categoriesList.add(new Category("Home Decoration", R.drawable.decoration));
        categoriesList.add(new Category("Entertainment", R.drawable.entertainment));
        categoriesList.add(new Category("Fashion", R.drawable.fashion));
        categoriesList.add(new Category("Fitness", R.drawable.fitness));
        categoriesList.add(new Category("Food", R.drawable.food));
        categoriesList.add(new Category("Pets", R.drawable.pets));
        categoriesList.add(new Category("Travel", R.drawable.travel));
    }


    private void displayWizard() {
        boolean wizardDisplayed = SharedPrefsHelper.getBooleanFromPrefs(getActivity(), CATEGORIES_WIZARD_DISPLAYED);
        if (!wizardDisplayed) {
            wizardRL.setVisibility(View.VISIBLE);
            displayWithAnimation(bubbleRL);
            displayWithAnimation(triangleView);
            wizardRL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.setVisibility(View.GONE);
                    //SharedPrefsHelper.storeBooleanInPrefs(getActivity(), true, CATEGORIES_WIZARD_DISPLAYED);
                }
            });
        }
    }


    private void displayWithAnimation(View view) {
        AlphaAnimation animation1 = new AlphaAnimation(0.0f, 1.0f);
        animation1.setDuration(1000);
        animation1.setStartOffset(100);
        animation1.setFillAfter(true);
        view.startAnimation(animation1);
    }


}
