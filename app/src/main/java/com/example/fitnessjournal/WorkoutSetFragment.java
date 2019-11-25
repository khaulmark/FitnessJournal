package com.example.fitnessjournal;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

public class WorkoutSetFragment extends Fragment implements View.OnClickListener {

    //Edit texts for exercises
    private EditText[][] exerciseEditText = new EditText[3][4];

    private String workoutSet;
    private int fragmentPosition;
    private InputMethodManager inputManager;
    private OnFragmentDoneListener listener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public WorkoutSetFragment(String workout, int position) {
        workoutSet = workout;
        Log.d("booty", "WORKOUTSET " + workoutSet);
        fragmentPosition = position;
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

        //Load the workoutSet string into all the EditTexts unless it contains Rest
        if (!workoutSet.equals("Rest")) {
            //Each exercise is split with a |
            String[] workoutSplit = workoutSet.split(";");

            //Each item within the exercise (sets, reps, weight) split by ,
            for (int i = 0; i < workoutSplit.length; i++) {
                Log.d("booty", "WORKOUTSPLIT[" + i + "] = " + workoutSplit[i]);
                String[] exerciseSplit = workoutSplit[i].split(",");
                for (int j = 0; j < exerciseSplit.length; j++) {
                    Log.d("booty", "EXERCISESPLIT[" + j + "] = " + exerciseSplit[j]);
                    exerciseEditText[i][j].setText(exerciseSplit[j]);
                }
            }
        }

        Button saveButton = (Button) rootView.findViewById(R.id.fragment_btn_save_workout);
        Button discardButton = (Button) rootView.findViewById(R.id.fragment_btn_discard_workout);
        saveButton.setOnClickListener(this);
        discardButton.setOnClickListener(this);

        inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        return rootView;
    }

    public interface OnFragmentDoneListener {
        //Listener for UploadProgramActivity to handle things when fragment is done
        void OnFragmentDone(String editText, int position);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentDoneListener) {
            listener = (OnFragmentDoneListener) context;
        }
        else {
            throw new ClassCastException(context.toString() + " must implement WorkoutSetFragment.OnFragmentDoneListener");
        }
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.fragment_btn_save_workout:
                StringBuilder stringBuilder = new StringBuilder();

                //TODO Do error handling when user leaves some fields blank or with improper inputs

                //Add all the EditTexts to a single string
                for (int i = 0; i < exerciseEditText.length; i++) {
                    for (int j = 0; j < exerciseEditText[i].length; j++) {
                        //Add a semicolon to denote the separation between exercises in the string
                        if (j == 3) {
                            stringBuilder.append(exerciseEditText[i][j].getText() + ";");
                        }
                        else {
                            stringBuilder.append(exerciseEditText[i][j].getText() + ",");
                        }
                    }
                }
                String workoutSaveTextTrue = stringBuilder.toString();
                listener.OnFragmentDone(workoutSaveTextTrue, fragmentPosition);
                break;

            case R.id.fragment_btn_discard_workout:
                //If the user discards the workout, set it to a Rest day
                String workoutSaveTextFalse = "Rest";
                listener.OnFragmentDone(workoutSaveTextFalse, fragmentPosition);
                break;

            default:
                break;
        }

        View focusedView = getActivity().getCurrentFocus();

        if (focusedView != null) {
            inputManager.hideSoftInputFromWindow(focusedView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}
