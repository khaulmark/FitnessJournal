package com.example.fitnessjournal.Views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.fitnessjournal.Presenters.FollowProgramPresenter;
import com.example.fitnessjournal.R;


public class ViewWorkoutFragment extends Fragment implements View.OnClickListener {

    private FollowProgramPresenter presenter;
    private boolean todayOrOld;

    //Arrays of layout components for better organization
    private LinearLayout[] exerciseFollow = new LinearLayout[3];
    private Button[] addVideo = new Button[3];
    private Spinner[] spinners = new Spinner[3];
    private TextView[] exerciseTitle = new TextView[3];
    private TextView[] exerciseRepsWeight = new TextView[3];

    private ImageView restIcon;
    private TextView restText;

    public ViewWorkoutFragment(FollowProgramPresenter presenter, boolean todayOrOld) {
        this.presenter = presenter;
        this.todayOrOld = todayOrOld;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate (R.layout.view_workout_fragment, container, false);

        exerciseFollow[0] = (LinearLayout) rootView.findViewById(R.id.exercise1_follow);
        exerciseFollow[1] = (LinearLayout) rootView.findViewById(R.id.exercise2_follow);
        exerciseFollow[2] = (LinearLayout) rootView.findViewById(R.id.exercise3_follow);

        restIcon = (ImageView) rootView.findViewById(R.id.restIcon);
        restText = (TextView) rootView.findViewById(R.id.restText);

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

        //Chooses which presenter method to run depending if from today or older
        if (todayOrOld) {
            presenter.onViewWorkoutFragmentCreated(this);
        }
        else {
            presenter.onViewOldWorkoutFragmentCreated(this);
        }
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
                presenter.viewVideo(0, exerciseTitle[0].getText().toString());
                break;
            case R.id.exercise2_view_video:
                presenter.viewVideo(1, exerciseTitle[1].getText().toString());
                break;
            case R.id.exercise3_view_video:
                presenter.viewVideo(2, exerciseTitle[2].getText().toString());
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

    //Updates the UI to show that the day's workout is a Rest day or not
    public void restUI(Boolean hide) {
        if (hide) {
            exerciseFollow[0].setVisibility(View.GONE);
            exerciseFollow[1].setVisibility(View.GONE);
            exerciseFollow[2].setVisibility(View.GONE);
        } else {
            restIcon.setVisibility(View.GONE);
            restText.setVisibility(View.GONE);
        }
    }

    //Hides the exercises that do not contain videos when the user selects to view an older workout
    public void hideLayouts(int numOfLayouts) {
        switch(numOfLayouts) {
            case 1:
                exerciseFollow[1].setVisibility(View.GONE);
                exerciseFollow[2].setVisibility(View.GONE);
                break;
            case 2:
                exerciseFollow[2].setVisibility(View.GONE);
                break;
            default:
                break;
        }

        //User does not have the ability to add a video on previous days
        for (int i = 0; i < addVideo.length; i++) {
            addVideo[i].setVisibility(View.GONE);
        }
        restIcon.setVisibility(View.GONE);
        restText.setVisibility(View.GONE);
    }
}
