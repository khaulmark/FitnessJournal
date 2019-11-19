package com.example.fitnessjournal;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fitnessjournal.Models.JournalProvider;

import static com.example.fitnessjournal.GoogleSignInActivity.EXTRA_MESSAGE_FIREBASEID;

public class HomeScreenActivity extends AppCompatActivity implements View.OnClickListener {

    private String username = "User";
    private String ID;
    private String program = "None";

    public static final String EXTRA_MESSAGE_ID = "com.example.fitnessjournal.id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        initializeComponents();
        Intent intent = getIntent();
        String firebaseID = intent.getStringExtra(EXTRA_MESSAGE_FIREBASEID);

        String[] projection = {
                JournalProvider.JOURNAL_TABLE_COL_ID,
                JournalProvider.JOURNAL_TABLE_COL_FIREBASE_ID,
                JournalProvider.JOURNAL_TABLE_COL_USERNAME,
                JournalProvider.JOURNAL_TABLE_COL_PROGRAM };

        String[] selectionArgs = {
                firebaseID };


        Cursor myCursor = getContentResolver().query(JournalProvider.CONTENT_URI,projection,"FIREBASE_ID = ?",selectionArgs,null);

        if (myCursor != null && myCursor.getCount() > 0){
            myCursor.moveToFirst();
            ID = myCursor.getString(0);
            username = myCursor.getString(2);
            program = myCursor.getString(3);
            Log.d("booty", "existing entry in db");
        }
        else {
            ContentValues myCV = new ContentValues();
            myCV.put(JournalProvider.JOURNAL_TABLE_COL_FIREBASE_ID, firebaseID);
            myCV.put(JournalProvider.JOURNAL_TABLE_COL_USERNAME, username);
            myCV.put(JournalProvider.JOURNAL_TABLE_COL_USERNAME, program);
            getContentResolver().insert(JournalProvider.CONTENT_URI, myCV);

            myCursor = getContentResolver().query(JournalProvider.CONTENT_URI,projection,"FIREBASE_ID = ?",selectionArgs,null);
            myCursor.moveToFirst();
            ID = myCursor.getString(0);
            Log.d("booty", "new entry in db");
        }

        Log.d("booty", ID + " " + firebaseID + " " + username + " " + program);
        Log.d("booty", DatabaseUtils.dumpCursorToString(myCursor));
        myCursor.close();
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
                Intent intent = new Intent(HomeScreenActivity.this, UploadProgramActivity.class);
                intent.putExtra(EXTRA_MESSAGE_ID, ID);
                startActivity(intent);
            default:
                break;
        }
    }







}