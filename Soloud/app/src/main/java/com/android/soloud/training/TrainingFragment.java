package com.android.soloud.training;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.soloud.R;
import com.android.soloud.activities.TutorialActivity;
import com.android.soloud.models.Training;
import java.util.ArrayList;

/**
 * Created by f.stamopoulos on 26/3/2017.
 */

public class TrainingFragment extends Fragment implements TrainingContract.View {

    private GridView gridView;
    private TrainingContract.UserActionsListener mActionsListener;
    private TrainingAdapter trainingAdapter;
    private RelativeLayout trainingGallery_RL;

    public TrainingFragment(){
        // Empty Costructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mActionsListener = new TrainingPresenter(this);




    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.training_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        trainingGallery_RL = (RelativeLayout) view.findViewById(R.id.training_RL);
        gridView = (GridView) view.findViewById(R.id.training_grid_view);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ArrayList<Training> trainingArrayList = new ArrayList<>();
        trainingArrayList.add(new Training(getString(R.string.training_title_framing_composition), R.drawable.bazo, 100));
        trainingArrayList.add(new Training(getString(R.string.training_title_colors_1), R.drawable.bazo1, 200));
        trainingArrayList.add(new Training(getString(R.string.training_title_colors_2), R.drawable.bazo, 300));
        trainingArrayList.add(new Training(getString(R.string.training_title_editing_basics), R.drawable.bazo, 300));
        trainingArrayList.add(new Training(getString(R.string.training_title_typography), R.drawable.bazo1, 300));
        trainingArrayList.add(new Training(getString(R.string.training_title_hash_tags_1), R.drawable.bazo, 300));
        trainingArrayList.add(new Training(getString(R.string.training_title_hash_tags_2), R.drawable.bazo, 300));
        trainingArrayList.add(new Training(getString(R.string.training_title_story), R.drawable.bazo1, 300));
        trainingArrayList.add(new Training(getString(R.string.training_narration_1), R.drawable.bazo, 300));
        trainingArrayList.add(new Training(getString(R.string.training_narration_2), R.drawable.bazo1, 300));

        trainingAdapter = new TrainingAdapter(getActivity(), trainingArrayList);
        gridView.setAdapter(trainingAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int userPoints = 150;
                if (userPoints > trainingAdapter.getItem(position).getRequiredPoints()){
                    Intent intent = new Intent(getActivity(), TutorialActivity.class);
                    startActivity(intent);
                }else{
                    Snackbar.make(trainingGallery_RL, getString(R.string.unlock_trainings), Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
    }


    @Override
    public void showTrainings(ArrayList<Training> trainingArrayList) {

    }

    @Override
    public void showTrainingDetails(@NonNull Training training) {

    }




}
