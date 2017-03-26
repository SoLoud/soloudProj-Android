package com.android.soloud.training;

import android.support.annotation.NonNull;

import com.android.soloud.models.Training;



/**
 * Created by f.stamopoulos on 26/3/2017.
 */

public class TrainingPresenter implements TrainingContract.UserActionsListener {

    private final TrainingContract.View mTrainingView;

    public TrainingPresenter(TrainingContract.View trainingView) {
        //mTrainingView = checkNotNull(trainingView, "notesView cannot be null!");
        mTrainingView = trainingView;
    }

    @Override
    public void loadTraining() {

    }

    @Override
    public void openTraining(@NonNull Training training) {

    }
}
