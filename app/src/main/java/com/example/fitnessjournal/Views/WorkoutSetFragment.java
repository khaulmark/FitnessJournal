package com.example.fitnessjournal.Views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import androidx.fragment.app.Fragment;
import com.example.fitnessjournal.Presenters.UploadProgramPresenter;
import com.example.fitnessjournal.R;

public class WorkoutSetFragment extends Fragment implements View.OnClickListener {

    //Edit texts for exercises
    private EditText[][] exerciseEditText = new EditText[3][4];

    //Presenter for UploadProgramActivity
    private UploadProgramPresenter presenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public WorkoutSetFragment(UploadProgramPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate (R.layout.workout_set_fragment, container, false);

        //Bind all the EditTexts to their layout counteparts
        exerciseEditText[0][0] = (EditText) rootView.findViewById(R.id.exercise1_edittext);
        exerciseEditText[0][1] = (EditText) rootView.findViewById(R.id.exercise1_sets_edittext);
        exerciseEditText[0][2] = (EditText) rootView.findViewById(R.id.exercise1_reps_edittext);
        exerciseEditText[0][3] = (EditText) rootView.findViewById(R.id.exercise1_weight_edittext);

        exerciseEditText[1][0] = (EditText) rootView.findViewById(R.id.exercise2_edittext);
        exerciseEditText[1][1] = (EditText) rootView.findViewById(R.id.exercise2_sets_edittext);
        exerciseEditText[1][2] = (EditText) rootView.findViewById(R.id.exercise2_reps_edittext);
        exerciseEditText[1][3] = (EditText) rootView.findViewById(R.id.exercise2_weight_edittext);

        exerciseEditText[2][0] = (EditText) rootView.findViewById(R.id.exercise3_edittext);
        exerciseEditText[2][1] = (EditText) rootView.findViewById(R.id.exercise3_sets_edittext);
        exerciseEditText[2][2] = (EditText) rootView.findViewById(R.id.exercise3_reps_edittext);
        exerciseEditText[2][3] = (EditText) rootView.findViewById(R.id.exercise3_weight_edittext);

        Button saveButton = (Button) rootView.findViewById(R.id.fragment_btn_save_workout);
        Button discardButton = (Button) rootView.findViewById(R.id.fragment_btn_discard_workout);
        saveButton.setOnClickListener(this);
        discardButton.setOnClickListener(this);

        presenter.onFragmentCreated(this);
        return rootView;
    }

    public void setExerciseEditText(int i, int j, String text) {
        exerciseEditText[i][j].setText(text);
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.fragment_btn_save_workout:
                //If the user saves the workout
                presenter.onSaveWorkout(exerciseEditText, true);
                break;

            case R.id.fragment_btn_discard_workout:
                //If the user discards the workout... will set it to a Rest day
                presenter.onSaveWorkout(exerciseEditText, false);
                break;

            default:
                break;
        }
    }
}
