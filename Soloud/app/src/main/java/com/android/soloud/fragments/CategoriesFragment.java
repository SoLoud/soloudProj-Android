package com.android.soloud.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.soloud.R;
import com.android.soloud.activities.ContestsActivity;
import com.android.soloud.adapters.CategoryAdapter;
import com.android.soloud.materialnavigationdrawer.MaterialNavigationDrawer;
import com.android.soloud.models.Category;

import java.util.ArrayList;

/**
 * Created by f.stamopoulos on 9/10/2016.
 */

public class CategoriesFragment extends Fragment {

    public static final String CONTEST_NAME = "ContestName";
    private ListView listView;
    private ArrayList<Category> categoriesList;


    public CategoriesFragment(){
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

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

    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initializeToolbar();

        //String[] categoriesArray = {"Charity", "Cosmetics", "Home Decoration", "Entertainment", "Fashion", "Fitness", "Food", "Pets", "Travel"};

        listView.setAdapter(new CategoryAdapter(getActivity(), categoriesList));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String contestName = categoriesList.get(position).getName();
                Intent intent = new Intent(getActivity(), ContestsActivity.class);
                intent.putExtra(CONTEST_NAME,contestName);
                startActivity(intent);
                getActivity().finish();
            }
        });

    }

    private void initializeToolbar() {
        Toolbar toolbar = ((MaterialNavigationDrawer)this.getActivity()).getToolbar();
        ((MaterialNavigationDrawer)this.getActivity()).setSupportActionBar(toolbar);
        ((MaterialNavigationDrawer)this.getActivity()).getSupportActionBar().show();
        ((MaterialNavigationDrawer)this.getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    public void onPause() {

        /*((MaterialNavigationDrawer) getActivity()).unlockSlideMenu();
        myWebView.onPause();*/
        super.onPause();
    }

    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK));
                getActivity().dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BACK));
                break;
            default:
                break;
        }
        return false;
    }*/

    private Boolean isConnected(){
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }
}
