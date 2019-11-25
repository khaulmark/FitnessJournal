package com.example.fitnessjournal.Views;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.fitnessjournal.Presenters.HomePresenter;
import com.example.fitnessjournal.R;

public class HomeScreenActivity extends AppCompatActivity implements View.OnClickListener {

    protected HomePresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        //No back button in ActionBar
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        //Create new HomePresenter for HomeActivity
        presenter = new HomePresenter(this);
        presenter.onCreate();

        initializeComponents();
    }

    //Set the OnClick Listener for buttons
    void initializeComponents() {
        //findViewById(R.id.btn_favorites).setOnClickListener(this);
        //findViewById(R.id.btn_follow_program).setOnClickListener(this);
        //findViewById(R.id.btn_holder).setOnClickListener(this);
        //findViewById(R.id.btn_howto).setOnClickListener(this);
        //findViewById(R.id.btn_sign_out).setOnClickListener(this);
        //findViewById(R.id.btn_track_progress).setOnClickListener(this);
        findViewById(R.id.btn_upload_program).setOnClickListener(this);
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.btn_upload_program:
                presenter.startUploadActivity();
            default:
                break;
        }
    }







}