package com.example.fitnessjournal;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

public class WorkoutSetFragment extends Fragment implements View.OnClickListener {

    private EditText workoutEditText;
    private Button saveButton;
    private Button discardButton;
    private String workoutSet;

    private OnFragmentDoneListener listener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public WorkoutSetFragment(String workout) {
        workoutSet = workout;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate (R.layout.workout_set_fragment, container, false);
        workoutEditText = (EditText) rootView.findViewById(R.id.fragment_workout_edit);
        saveButton = (Button) rootView.findViewById(R.id.fragment_btn_save_workout);
        discardButton = (Button) rootView.findViewById(R.id.fragment_btn_discard_workout);
        return rootView;
    }

    public interface OnFragmentDoneListener {
        public void OnFragmentDone(String editText, boolean saveOrDiscard);
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

    //Set the OnClick Listener for buttons
    void initializeComponents() {
        getActivity().findViewById(R.id.btn_favorites).setOnClickListener(this);
        getActivity().findViewById(R.id.btn_follow_program).setOnClickListener(this);
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.fragment_btn_save_workout:
                listener.OnFragmentDone(workoutEditText.getText().toString(), true);
                getActivity().onBackPressed();
                break;

            case R.id.fragment_btn_discard_workout:
                listener.OnFragmentDone(workoutEditText.getText().toString(), false);
                getActivity().onBackPressed();
                break;

            default:
                break;
        }
    }
}
