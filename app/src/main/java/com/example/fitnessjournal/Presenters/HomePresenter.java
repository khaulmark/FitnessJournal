package com.example.fitnessjournal.Presenters;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import com.example.fitnessjournal.Models.JournalProvider;
import com.example.fitnessjournal.Views.FollowProgramActivity;
import com.example.fitnessjournal.Views.HomeScreenActivity;
import com.example.fitnessjournal.Views.UploadProgramActivity;

import static android.app.Activity.RESULT_OK;
import static com.example.fitnessjournal.Views.GoogleSignInActivity.EXTRA_MESSAGE_FIREBASEID;

public class HomePresenter implements Presenter {
    private HomeScreenActivity view;
    private String username;
    private String ID;
    private String program;

    public static final String EXTRA_MESSAGE_ID = "com.example.fitnessjournal.ID";
    public static final String EXTRA_MESSAGE_PROGRAM = "com.example.fitnessjournal.program";
    public static final String EXTRA_MESSAGE_SIGNOUT = "com.example.fitnessjournal.signout";

    public HomePresenter(HomeScreenActivity view) {
        this.view = view;
    }

    @Override
    public void onCreate() {
        Intent intent = view.getIntent();
        String firebaseID = intent.getStringExtra(EXTRA_MESSAGE_FIREBASEID);

        String[] projection = {
                JournalProvider.JOURNAL_TABLE_COL_ID,
                JournalProvider.JOURNAL_TABLE_COL_FIREBASE_ID,
                JournalProvider.JOURNAL_TABLE_COL_USERNAME,
                JournalProvider.JOURNAL_TABLE_COL_PROGRAM };

        String[] selectionArgs = {
                firebaseID };

        Cursor myCursor = view.getContentResolver().query(JournalProvider.CONTENT_URI,projection,"FIREBASE_ID = ?",selectionArgs,null);

        if (myCursor != null && myCursor.getCount() > 0){
            myCursor.moveToFirst();
            ID = myCursor.getString(0);
            username = myCursor.getString(2);
            program = myCursor.getString(3);
            Log.d("login", "ID = " + ID + " FIREBASEID = " + firebaseID + " USERNAME = " + username + " PROGRAM = " + program);
        }
        else {
            ContentValues myCV = new ContentValues();
            username = "User";
            program = "None";
            myCV.put(JournalProvider.JOURNAL_TABLE_COL_FIREBASE_ID, firebaseID);
            myCV.put(JournalProvider.JOURNAL_TABLE_COL_USERNAME, username);
            myCV.put(JournalProvider.JOURNAL_TABLE_COL_PROGRAM, program);
            view.getContentResolver().insert(JournalProvider.CONTENT_URI, myCV);

            //Save the ID of the user that was just created
            myCursor = view.getContentResolver().query(JournalProvider.CONTENT_URI,projection,"FIREBASE_ID = ?",selectionArgs,null);
            myCursor.moveToFirst();
            ID = myCursor.getString(0);
        }
        myCursor.close();
    }

    public void startUploadActivity() {
        Intent intent = new Intent(view, UploadProgramActivity.class);
        intent.putExtra(EXTRA_MESSAGE_ID, ID);
        intent.putExtra(EXTRA_MESSAGE_PROGRAM, program);
        view.startActivity(intent);
    }

    public void startFollowProgramActivity() {
        Intent intent = new Intent(view, FollowProgramActivity.class);
        intent.putExtra(EXTRA_MESSAGE_ID, ID);
        intent.putExtra(EXTRA_MESSAGE_PROGRAM, program);
        view.startActivity(intent);
    }

    //Sign out button signs the user out of the application
    public void onSignOut(String signOutOrNo) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_MESSAGE_SIGNOUT, signOutOrNo);
        view.setResult(RESULT_OK, intent);
        view.finish();
    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onDestroy() {

    }
}
