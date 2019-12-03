package com.example.fitnessjournal.Presenters;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.fitnessjournal.Models.JournalProvider;
import com.example.fitnessjournal.R;
import com.example.fitnessjournal.Views.DatePickerFragment;
import com.example.fitnessjournal.Views.FollowProgramActivity;
import com.example.fitnessjournal.Views.ViewWorkoutFragment;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static com.example.fitnessjournal.Presenters.HomePresenter.EXTRA_MESSAGE_ID;

public class FollowProgramPresenter implements Presenter, DatePickerDialog.OnDateSetListener, AdapterView.OnItemSelectedListener {
    private FollowProgramActivity view;
    private ViewWorkoutFragment fragment;
    private TextView[] exerciseRepsWeight = new TextView[3];
    private String username;
    private String program;
    private String setNumber;
    private String[][] exerciseInfo = new String[3][4];
    private int month;
    private int dayOfMonth;
    private int dayOfWeek;
    private FragmentManager fm;
    private String currentVideoPath;

    static final int REQUEST_VIDEO_CAPTURE = 1;


    public FollowProgramPresenter(FollowProgramActivity view) {
        this.view = view;
    }

    @Override
    public void onCreate() {
        Intent intent = view.getIntent();
        String ID = intent.getStringExtra(EXTRA_MESSAGE_ID);

        //Query the DB to get the workout string
        String projection[] = {
                JournalProvider.JOURNAL_TABLE_COL_ID,
                JournalProvider.JOURNAL_TABLE_COL_USERNAME,
                JournalProvider.JOURNAL_TABLE_COL_PROGRAM };

        String selectionArgs[] = {ID };

        Cursor myCursor = view.getContentResolver().query(JournalProvider.CONTENT_URI,projection,"_ID = ?",selectionArgs,null);
        if (myCursor != null && myCursor.getCount() > 0){
            myCursor.moveToFirst();
            username = myCursor.getString(1);
            program = myCursor.getString(2);
        }

        fm = view.getSupportFragmentManager();
    }

    public void goToTodaysWorkout() {
        if (!program.equals("None")) {
            Calendar cal = Calendar.getInstance();
            parseDate(cal);
            loadFragment();
        }
        else {
            Toast.makeText(view, "No program uploaded!", Toast.LENGTH_LONG).show();
        }
    }

    public void goToPreviousWorkout() {
        //Opens the calendar dialog and sets the date for the alarm
        DialogFragment datePicker = new DatePickerFragment(this);
        datePicker.show(view.getSupportFragmentManager(), "date picker");
        //loadFragment();
    }

    //Date from calendar dialog is sent here
    @Override
    public void onDateSet(DatePicker dateView, int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day);
        parseDate(cal);
        loadFragment();
    }

    private void parseDate(Calendar cal) {
        DateFormat df = new SimpleDateFormat("u d MM");
        String date = df.format(cal.getTime());
        String[] dateSplit = date.split(" ");

        dayOfWeek = Integer.parseInt(dateSplit[0]);
        dayOfMonth = Integer.parseInt(dateSplit[1]);
        month = Integer.parseInt(dateSplit[2]);
    }

    public void onFragmentCreated(ViewWorkoutFragment fragment) {
        this.fragment = fragment;

        String[] workouts = program.split("\n");
        String currentWorkout = workouts[dayOfWeek-1];

        if (!currentWorkout.equals("Rest")) {
            String[] exercises = currentWorkout.split(";");
            Spinner[] spinners = fragment.getSpinners();
            TextView[] exerciseTitleTextViews = fragment.getTitleTextViews();
            exerciseRepsWeight = fragment.getRepsWeightTextViews();

            exerciseInfo[0] = exercises[0].split(",");
            exerciseInfo[1] = exercises[1].split(",");
            exerciseInfo[2] = exercises[2].split(",");

            for (int i = 0; i < exercises.length; i++) {
                //String[] exerciseInfo = exercises[i].split(",");
                int setNumber = Integer.parseInt(exerciseInfo[i][1]);
                String[] items = new String[setNumber];
                for (int j = 0; j < setNumber; j++) {
                    items[j] = String.valueOf(j + 1);
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(view, R.layout.spinner_text, items);
                adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown);
                spinners[i].setAdapter(adapter);
                exerciseTitleTextViews[i].setText(exerciseInfo[i][0]);
            }

            fragment.restUI(false);
        }
        else {
            fragment.restUI(true);
        }
    }

    public void loadFragment() {
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.placeholder_follow, new ViewWorkoutFragment(this));

        //Clears other fragment from BackStack before adding new fragment
        fm.popBackStack();
        ft.addToBackStack(null);
        ft.commit();
    }

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)

        //TODO Eventually add a way for user to enter different weight/reps for each set

        //exerciseRepsWeight.setText(exercise1Info[3] + " for " + exercise1Info[2]);
        //Log.d("booty", "ID_ONITEMSELECTED = " + id + " POS = " + pos + " PARENTID = " + parent.getId());

        switch(parent.getId()) {
            case R.id.exercise1_spinner:
                exerciseRepsWeight[0].setText(exerciseInfo[0][3] + " for " + exerciseInfo[0][2]);
                setNumber = exerciseInfo[0][1];
                break;
            case R.id.exercise2_spinner:
                exerciseRepsWeight[1].setText(exerciseInfo[1][3] + " for " + exerciseInfo[1][2]);
                setNumber = exerciseInfo[1][1];
                break;
            case R.id.exercise3_spinner:
                exerciseRepsWeight[2].setText(exerciseInfo[2][3] + " for " + exerciseInfo[2][2]);
                setNumber = exerciseInfo[2][1];
                break;
            default:
                break;
        }

    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    public void addVideo(String exercise, String setNumberFilename) {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        //Ensure that there's a camera activity to handle the intent
        if (takeVideoIntent.resolveActivity(view.getPackageManager()) != null) {
            //Create a file where the photo should go
            File videoFile = null;
            try {
                videoFile = createVideoFile(exercise, setNumberFilename);
            } catch (IOException ex) {
                //Error occurred while creating the file
            }
            //Continue only if the file was successfully created
            if (videoFile != null) {
                Uri videoURI = FileProvider.getUriForFile(view, "com.example.fitnessjournal.android.fileprovider", videoFile);
                takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, videoURI);
                view.startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
            }
        }
    }

    private File createVideoFile(String exercise, String setNumberFilename) throws IOException {
        //Create an image file name
        //String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        //String imageFileName = "JPEG_" + timeStamp + "_";
        String videoFileName = username + "_" + exercise + "_" + setNumberFilename + "_" + month + "/" + dayOfMonth;
        File storageDir = view.getExternalFilesDir(Environment.DIRECTORY_MOVIES);
        File video = File.createTempFile(
                videoFileName,
                ".mp4",
                storageDir
        );

        //Save a file: path for use with ACTION_VIEW intents
        currentVideoPath = video.getAbsolutePath();
        Log.d("booty", currentVideoPath.toString());
        return video;
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
