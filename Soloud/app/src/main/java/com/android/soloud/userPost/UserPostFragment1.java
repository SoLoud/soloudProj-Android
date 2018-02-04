package com.android.soloud.userPost;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.soloud.R;
import com.android.soloud.contests.ContestsActivity;
import com.android.soloud.facebookPlaces.model.Place;
import com.android.soloud.models.Contest;
import com.android.soloud.models.CurrentState;

import java.util.ArrayList;

/**
 * Created by f.stamopoulos on 4/2/2018.
 */

public class UserPostFragment1 extends Fragment {

    private Toolbar toolbar;

    public static UserPostFragment1 newInstance(Contest contest, CurrentState currentState, ArrayList<String> hashTagList, Place selectedPlace) {
        Bundle args = new Bundle();
        args.putSerializable(ContestsActivity.CONTEST, contest);
        args.putSerializable(ContestsActivity.CURRENT_STATE, currentState);
        args.putStringArrayList("hashTagsList", hashTagList);
        args.putParcelable("selectedPlace", selectedPlace);
        UserPostFragment1 fragment = new UserPostFragment1();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.user_post_fragment1, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        toolbar.setTitle(R.string.post_preview);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }
}
