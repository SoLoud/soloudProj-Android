package com.android.soloud.wizard;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.soloud.R;
import com.android.soloud.activities.MainActivity;
import com.github.clans.fab.FloatingActionButton;

/**
 * Created by f.stamopoulos on 1/7/2017.
 */

public class SlideFragment extends Fragment {

    private TextView descriptionTV;
    private ImageView imageIV;
    private int resourceImageId;
    private String description;
    private RelativeLayout relativeLayout;
    private int backgroundColorId;
    private com.github.clans.fab.FloatingActionButton fab;
    private int fabImageResourceId;
    private boolean showFab;

    public SlideFragment() {
        // Empty Constructor
    }

    public static SlideFragment newInstance(int resourceImageId, String description, int backgroundColorId, int fabImageResourceId, boolean showFab) {
        Bundle args = new Bundle();
        args.putInt("resourceImageId", resourceImageId);
        args.putString("description", description);
        args.putInt("backgroundColorId", backgroundColorId);
        args.putInt("fabImageResourceId", fabImageResourceId);
        args.putBoolean("showFab", showFab);
        SlideFragment fragment = new SlideFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        resourceImageId = getArguments().getInt("resourceImageId");
        description = getArguments().getString("description");
        backgroundColorId = getArguments().getInt("backgroundColorId");
        fabImageResourceId = getArguments().getInt("fabImageResourceId");
        showFab = getArguments().getBoolean("showFab");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.slide_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        relativeLayout = (RelativeLayout) view.findViewById(R.id.relativeLayout);
        descriptionTV = (TextView) view.findViewById(R.id.description_TV);
        imageIV = (ImageView) view.findViewById(R.id.image_IV);
        fab = (FloatingActionButton) view.findViewById(R.id.fab);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        relativeLayout.setBackgroundResource(backgroundColorId);
        descriptionTV.setText(description);
        imageIV.setImageResource(resourceImageId);
        fab.setImageResource(fabImageResourceId);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        if (!showFab){
            fab.setVisibility(View.INVISIBLE);
        }
    }
}
