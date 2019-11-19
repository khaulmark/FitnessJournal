package com.example.fitnessjournal;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

public class WorkoutSetFragment extends Fragment implements View.OnClickListener {

    private EditText workoutEditText;
    private String workoutSet;
    private int fragmentPosition;

    private OnFragmentDoneListener listener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public WorkoutSetFragment(String workout, int position) {
        workoutSet = workout;
        fragmentPosition = position;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate (R.layout.workout_set_fragment, container, false);
        workoutEditText = (EditText) rootView.findViewById(R.id.fragment_workout_edit);
        workoutEditText.setText(workoutSet);

        Button saveButton = (Button) rootView.findViewById(R.id.fragment_btn_save_workout);
        Button discardButton = (Button) rootView.findViewById(R.id.fragment_btn_discard_workout);
        saveButton.setOnClickListener(this);
        discardButton.setOnClickListener(this);

        return rootView;
    }

    public interface OnFragmentDoneListener {
        void OnFragmentDone(String editText, int position, boolean saveOrDiscard);
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
                listener.OnFragmentDone(workoutEditText.getText().toString(), fragmentPosition, true);
                break;

            case R.id.fragment_btn_discard_workout:
                listener.OnFragmentDone(workoutEditText.getText().toString(), fragmentPosition, false);
                break;

            default:
                break;
        }
    }
}
