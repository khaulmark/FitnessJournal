package com.example.fitnessjournal;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import static com.example.fitnessjournal.GoogleSignInActivity.EXTRA_MESSAGE_FIREBASEID;

public class HomeScreenActivity extends AppCompatActivity implements View.OnClickListener {

    private String firebaseID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        Intent intent = getIntent();

        firebaseID = intent.getStringExtra(EXTRA_MESSAGE_FIREBASEID);

        initializeComponents();

        //Get display name
        String displayName = intent.getStringExtra(EXTRA_MESSAGE_FIREBASEID);
        Toast.makeText(this, displayName, Toast.LENGTH_LONG).show();

    }

    //Set the OnClick Listener for buttons
    void initializeComponents() {
        findViewById(R.id.btn_favorites).setOnClickListener(this);
        findViewById(R.id.btn_follow_program).setOnClickListener(this);
        findViewById(R.id.btn_holder).setOnClickListener(this);
        findViewById(R.id.btn_howto).setOnClickListener(this);
        findViewById(R.id.btn_sign_out).setOnClickListener(this);
        findViewById(R.id.btn_track_progress).setOnClickListener(this);
        findViewById(R.id.btn_upload_program).setOnClickListener(this);
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.btn_upload_program:
                Intent intent = new Intent(HomeScreenActivity.this, UploadProgramActivity.class);
                intent.putExtra(EXTRA_MESSAGE_FIREBASEID, firebaseID);
                startActivity(intent);
            default:
                break;
        }
    }







}