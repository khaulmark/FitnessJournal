package com.example.fitnessjournal.Presenters;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitnessjournal.Models.JournalProvider;
import com.example.fitnessjournal.Models.MyAdapter;
import com.example.fitnessjournal.R;
import com.example.fitnessjournal.Views.UploadProgramActivity;
import com.example.fitnessjournal.Views.WorkoutSetFragment;

import java.util.ArrayList;

import static com.example.fitnessjournal.Presenters.HomePresenter.EXTRA_MESSAGE_ID;
import static com.example.fitnessjournal.Presenters.HomePresenter.EXTRA_MESSAGE_PROGRAM;


public class UploadProgramPresenter implements Presenter {
    private UploadProgramActivity view;
    private WorkoutSetFragment fragment;
    private String ID;
    private ArrayList<String> workoutDays = new ArrayList<>();
    private ArrayList<String> workoutString = new ArrayList<>();
    private FragmentManager fm;
    private int fragmentPosition;

    public UploadProgramPresenter(UploadProgramActivity view) {
        this.view = view;
    }

    @Override
    public void onCreate() {
        Intent intent = view.getIntent();
        ID = intent.getStringExtra(EXTRA_MESSAGE_ID);
        String program = intent.getStringExtra(EXTRA_MESSAGE_PROGRAM);

        fm = view.getSupportFragmentManager();

        //For now, the days in the program are just the days of the week
        workoutDays.add("Sunday");
        workoutDays.add("Monday");
        workoutDays.add("Tuesday");
        workoutDays.add("Wednesday");
        workoutDays.add("Thursday");
        workoutDays.add("Friday");
        workoutDays.add("Saturday");
        //TODO Add more days to the program and/or make it more dynamic

        //Each day is initialized as a Rest day
        for (int i = 0; i < workoutDays.size(); i++) {
            workoutString.add("Rest");
        }

        RecyclerView recyclerView = view.getRecyclerView();
        recyclerView.setLayoutManager(new LinearLayoutManager(view));

        final UploadProgramPresenter presenter = this;
        //The adapter lets the user launch a new fragment to upload the program for that day
        MyAdapter adapter = new MyAdapter(view, workoutDays,
                new MyAdapter.OnMyAdapterItemClickListener() {
                    @Override
                    public void onItemClicked(int position) {
                        FragmentTransaction ft = view.getSupportFragmentManager().beginTransaction();
                        ft.replace(R.id.placeholder, new WorkoutSetFragment(presenter));
                        fragmentPosition = position;
                        //Clears other fragment from BackStack before adding new fragment
                        fm.popBackStack();
                        ft.addToBackStack(null);
                        ft.commit();
                    }
                }
        );

        recyclerView.setAdapter(adapter);

        //Uses program from HomeActivity to set the existing program in fragment options
        if (!program.equals("None")) {
            String[] programDays = program.split("\n");
            for (int i = 0; i < programDays.length; i++) {
                workoutString.set(i, programDays[i]);
            }
        }
    }

    public void onCreateFragmentView(WorkoutSetFragment fragment) {
        this.fragment = fragment;
        String workout = workoutString.get(fragmentPosition);
        //Load the workoutSet string into all the EditTexts unless it contains Rest
        if (workout.equals("Rest")) {
            //Each exercise is split with a |
            String[] workoutSplit = workout.split(";");

            //Each item within the exercise (sets, reps, weight) split by ,
            for (int i = 0; i < workoutSplit.length; i++) {
                Log.d("booty", "WORKOUTSPLIT[" + i + "] = " + workoutSplit[i]);
                String[] exerciseSplit = workoutSplit[i].split(",");
                for (int j = 0; j < exerciseSplit.length; j++) {
                    Log.d("booty", "EXERCISESPLIT[" + j + "] = " + exerciseSplit[j]);
                    //exerciseEditText[i][j].setText(exerciseSplit[j]);
                    fragment.setExerciseEditText(i, j, exerciseSplit[j]);
                }
            }
        }
    }

    public void onSaveWorkout(EditText[][] exerciseEditText, boolean saveOrDiscard) {
        if (!saveOrDiscard) {
            workoutString.set(fragmentPosition, "Rest");
        }
        else {
            StringBuilder stringBuilder = new StringBuilder();

            //TODO Do error handling when user leaves some fields blank or with improper inputs

            //Add all the EditTexts to a single string
            for (int i = 0; i < exerciseEditText.length; i++) {
                for (int j = 0; j < exerciseEditText[i].length; j++) {
                    //Add a semicolon to denote the separation between exercises in the string
                    if (j == 3) {
                        stringBuilder.append(exerciseEditText[i][j].getText() + ";");
                    } else {
                        stringBuilder.append(exerciseEditText[i][j].getText() + ",");
                    }
                }
            }
            workoutString.set(fragmentPosition, stringBuilder.toString());
        }

        InputMethodManager inputManager = (InputMethodManager) view.getSystemService(Context.INPUT_METHOD_SERVICE);
        View focusedView = view.getCurrentFocus();

        if (focusedView != null) {
            inputManager.hideSoftInputFromWindow(focusedView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }

        fm.popBackStack();
    }

    public void onUploadProgram() {
        StringBuilder stringBuilder = new StringBuilder();
        for (String s : workoutString) {
            stringBuilder.append(s + "\n");
        }

        //Uploads the current program to the DB
        ContentValues myCV = new ContentValues();
        myCV.put(JournalProvider.JOURNAL_TABLE_COL_PROGRAM, stringBuilder.toString());
        view.getContentResolver().update(Uri.parse(JournalProvider.CONTENT_URI + "/" + ID), myCV, null, null);

        //Finish the activity
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