package com.example.fitnessjournal.Views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.fitnessjournal.Presenters.FollowProgramPresenter;
import com.example.fitnessjournal.R;


public class ViewOldWorkoutFragment extends Fragment implements View.OnClickListener {

    //Presenter for UploadProgramActivity
    private FollowProgramPresenter presenter;

    View rootView;

    private Spinner[] spinners = new Spinner[3];
    private TextView[] exerciseTitle = new TextView[3];
    private TextView[] exerciseRepsWeight = new TextView[3];

    public ViewOldWorkoutFragment(FollowProgramPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate (R.layout.view_workout_fragment, container, false);

        spinners[0] = (Spinner) rootView.findViewById(R.id.exercise1_spinner);
        spinners[1] = (Spinner) rootView.findViewById(R.id.exercise2_spinner);
        spinners[2] = (Spinner) rootView.findViewById(R.id.exercise3_spinner);
        for (int i = 0; i < spinners.length; i++) {
            spinners[i].setOnItemSelectedListener(presenter);
        }

        exerciseTitle[0] = (TextView) rootView.findViewById(R.id.exercise1_title);
        exerciseRepsWeight[0] = (TextView) rootView.findViewById(R.id.exercise1_reps_weight);

        exerciseTitle[1] = (TextView) rootView.findViewById(R.id.exercise2_title);
        exerciseRepsWeight[1] = (TextView) rootView.findViewById(R.id.exercise2_reps_weight);

        exerciseTitle[2] = (TextView) rootView.findViewById(R.id.exercise3_title);
        exerciseRepsWeight[2] = (TextView) rootView.findViewById(R.id.exercise3_reps_weight);

        Button[] addVideo = new Button[3];
        Button[] viewVideo = new Button[3];

        addVideo[0] = (Button) rootView.findViewById(R.id.exercise1_add_video);
        addVideo[1] = (Button) rootView.findViewById(R.id.exercise2_add_video);
        addVideo[2] = (Button) rootView.findViewById(R.id.exercise3_add_video);

        viewVideo[0] = (Button) rootView.findViewById(R.id.exercise1_view_video);
        viewVideo[1] = (Button) rootView.findViewById(R.id.exercise2_view_video);
        viewVideo[2] = (Button) rootView.findViewById(R.id.exercise3_view_video);

        for (int i = 0; i < addVideo.length; i++) {
            addVideo[i].setOnClickListener(this);
            viewVideo[i].setOnClickListener(this);
        }

        //presenter.onViewOldWorkoutFragmentCreated(this);
        return rootView;
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.exercise1_add_video:
                presenter.addVideo(0);
                break;
            case R.id.exercise2_add_video:
                presenter.addVideo(1);
                break;
            case R.id.exercise3_add_video:
                presenter.addVideo(2);
                break;
            case R.id.exercise1_view_video:
                presenter.viewVideo(0);
                break;
            case R.id.exercise2_view_video:
                presenter.viewVideo(1);
                break;
            case R.id.exercise3_view_video:
                presenter.viewVideo(2);
                break;
            default:
                break;
        }
    }

    public Spinner[] getSpinners() {
        return spinners;
    }

    public TextView[] getTitleTextViews() {
        return exerciseTitle;
    }

    public TextView[] getRepsWeightTextViews() {
        return exerciseRepsWeight;
    }

    public void restUI(Boolean hide) {
        if (hide != null) {

        } else {

        }
    }
}
