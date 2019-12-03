package com.example.fitnessjournal.Views;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fitnessjournal.Presenters.FollowProgramPresenter;
import com.example.fitnessjournal.R;

public class FollowProgramActivity extends AppCompatActivity implements View.OnClickListener {

    protected FollowProgramPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follow_program);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        //No back button in ActionBar
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        initializeComponents();

        //Create a new FollowProgramPresenter for FollowProgramActivity
        presenter = new FollowProgramPresenter(this);
        presenter.onCreate();
    }

    //Set the OnClick Listener for buttons
    void initializeComponents() {
        findViewById(R.id.btn_date_picker).setOnClickListener(this);
        findViewById(R.id.btn_todays_workout).setOnClickListener(this);
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.btn_date_picker:
                presenter.goToPreviousWorkout();
                break;
            case R.id.btn_todays_workout:
                presenter.goToTodaysWorkout();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        /*if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {
            Uri videoUri = intent.getData();
            videoView.setVideoURI(videoUri);
        }*/
    }
}
