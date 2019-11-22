package com.example.fitnessjournal;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitnessjournal.Models.JournalProvider;

import java.util.ArrayList;

import static com.example.fitnessjournal.GoogleSignInActivity.EXTRA_MESSAGE_FIREBASEID;
import static com.example.fitnessjournal.HomeScreenActivity.EXTRA_MESSAGE_ID;

public class UploadProgramActivity extends AppCompatActivity implements View.OnClickListener, WorkoutSetFragment.OnFragmentDoneListener {

    private String ID;
    private ArrayList<String> workoutDays = new ArrayList<>();
    private ArrayList<String> workoutString = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_program);
        initializeComponents();
        Intent intent = getIntent();
        ID = intent.getStringExtra(EXTRA_MESSAGE_ID);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.dayList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        workoutDays.add("Sunday");
        workoutDays.add("Monday");
        workoutDays.add("Tuesday");
        workoutDays.add("Wednesday");
        workoutDays.add("Thursday");
        workoutDays.add("Friday");
        workoutDays.add("Saturday");
        
        for (int i = 0; i < workoutDays.size(); i++) {
            workoutString.add("Rest");
        }

        MyAdapter adapter = new MyAdapter(this, workoutDays,
                new MyAdapter.OnMyAdapterItemClickListener() {
                    @Override
                    public void onItemClicked(int position) {
                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                        ft.replace(R.id.placeholder, new WorkoutSetFragment(workoutString.get(position), position));
                        ft.addToBackStack(null);
                        ft.commit();
                    }
                }
        );

        recyclerView.setAdapter(adapter);
    }

    @Override
    public void OnFragmentDone(String editText, int position) {
        workoutString.set(position, editText);

        FragmentManager fm = getSupportFragmentManager();
        for(int i = 0; i < fm.getBackStackEntryCount(); i++) {
            fm.popBackStack();
        }
    }

    //Set the OnClick Listener for buttons
    void initializeComponents() {
        findViewById(R.id.btn_upload_program_inner).setOnClickListener(this);
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.btn_upload_program_inner:
                StringBuilder stringBuilder = new StringBuilder();
                for (String s : workoutString) {
                    stringBuilder.append(s + "\n");
                }
                ContentValues myCV = new ContentValues();
                myCV.put(JournalProvider.JOURNAL_TABLE_COL_PROGRAM, stringBuilder.toString());
                getContentResolver().update(Uri.parse(JournalProvider.CONTENT_URI + "/" + ID), myCV, null, null);

                //Error Check
                // TODO Add a cursor to do some error checking... we want to see if the program added to db correctly
                String[] projection = {
                        JournalProvider.JOURNAL_TABLE_COL_ID,
                        JournalProvider.JOURNAL_TABLE_COL_PROGRAM };

                String[] selectionArgs = {
                        ID };

                Cursor myCursor = getContentResolver().query(JournalProvider.CONTENT_URI,projection,"_ID = ?",selectionArgs,null);
                myCursor.moveToFirst();
                Log.d("booty", DatabaseUtils.dumpCurrentRowToString(myCursor));

            default:
                break;
        }
    }
}
