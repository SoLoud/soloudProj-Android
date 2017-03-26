package com.android.soloud.training;

import android.support.annotation.NonNull;

import com.android.soloud.models.Training;

import java.util.ArrayList;

/**
 * Created by f.stamopoulos on 26/3/2017.
 */

public class TrainingContract {

    interface View{

        void showTrainings(ArrayList<Training> trainingArrayList);

        void showTrainingDetails(@NonNull Training training);
    }

    interface UserActionsListener {

        void loadTraining();

        void openTraining(@NonNull Training training);
    }

}
