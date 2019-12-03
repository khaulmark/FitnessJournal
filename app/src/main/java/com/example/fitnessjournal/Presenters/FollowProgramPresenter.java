package com.example.fitnessjournal.Presenters;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.VoiceInteractor;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.fitnessjournal.Models.JournalProvider;
import com.example.fitnessjournal.Models.VideoProvider;
import com.example.fitnessjournal.R;
import com.example.fitnessjournal.Views.DatePickerFragment;
import com.example.fitnessjournal.Views.FollowProgramActivity;
import com.example.fitnessjournal.Views.VideoViewerFragment;
import com.example.fitnessjournal.Views.ViewOldWorkoutFragment;
import com.example.fitnessjournal.Views.ViewWorkoutFragment;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static com.example.fitnessjournal.Presenters.HomePresenter.EXTRA_MESSAGE_ID;

public class FollowProgramPresenter implements Presenter, DatePickerDialog.OnDateSetListener, AdapterView.OnItemSelectedListener {
    private FollowProgramActivity view;
    private ViewWorkoutFragment fragment;
    private ViewOldWorkoutFragment fragmentOldWorkout;
    private TextView[] exerciseRepsWeight = new TextView[3];
    private String firebaseID;
    private String program;
    private String[] setNumber = new String[3];
    private String[][] exerciseInfo = new String[3][4];
    private int dayOfWeek;
    private String date;
    private FragmentManager fm;
    private String currentVideoPath;

    static final int REQUEST_VIDEO_CAPTURE = 1;
    static final String EXTRA_EXERCISE_NUMBER = "com.example.fitnessjournal.Presenter.FollowProgramPresenter.EXTRA_EXERCISE_NUMBER";


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
                JournalProvider.JOURNAL_TABLE_COL_FIREBASE_ID,
                JournalProvider.JOURNAL_TABLE_COL_PROGRAM };

        String selectionArgs[] = {ID };

        Cursor myCursor = view.getContentResolver().query(JournalProvider.CONTENT_URI,projection,"_ID = ?",selectionArgs,null);
        if (myCursor != null && myCursor.getCount() > 0){
            myCursor.moveToFirst();
            firebaseID = myCursor.getString(1);
            program = myCursor.getString(2);
        }
        myCursor.close();
        fm = view.getSupportFragmentManager();
    }

    public void goToTodaysWorkout() {
        if (!program.equals("None")) {
            Calendar cal = Calendar.getInstance();
            parseDate(cal);
            loadViewWorkoutFragment();
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

        String[] projection = {
                VideoProvider.VIDEO_TABLE_COL_ID,
                VideoProvider.VIDEO_TABLE_COL_FIREBASE_ID,
                VideoProvider.VIDEO_TABLE_COL_FILENAME,
                VideoProvider.VIDEO_TABLE_COL_DATE,
                VideoProvider.VIDEO_TABLE_COL_EXERCISE,
                VideoProvider.VIDEO_TABLE_COL_SETNUMBER };

        final String selectionArgs[] = { firebaseID, date };

        Cursor myCursor = view.getContentResolver().query(VideoProvider.CONTENT_URI,projection,"FIREBASE_ID = ? AND DATE = ?",selectionArgs,null);

        if (myCursor != null && myCursor.getCount() > 0) {
            //loadViewOldWorkoutFragment();

            ArrayList<String> oldExercises = new ArrayList<>();
            ArrayList<String> oldSetNumbers = new ArrayList<>();
            myCursor.moveToFirst();
            oldExercises.add(myCursor.getString(4));

            for (myCursor.moveToNext(); !myCursor.isAfterLast(); myCursor.moveToNext()) {
                for (String s : oldExercises) {
                    if (!myCursor.getString(4).equals(s)) {
                        oldExercises.add(myCursor.getString(4));
                    }
                }
            }
            for (String s : oldExercises) {
                    ArrayList<String> setNumber = new ArrayList<>();
                    for (myCursor.moveToFirst(); !myCursor.isAfterLast(); myCursor.moveToNext()) {
                        if (myCursor.getString(4).equals(s)) {
                            setNumber.add(myCursor.getString(5));
                        }
                    }
                    oldSetNumbers.add(setNumber.toString());
            }
        }
        else {
            Toast.makeText(view, "No videos recorded for that day.", Toast.LENGTH_LONG).show();
        }
    }

    private void parseDate(Calendar cal) {
        DateFormat df = new SimpleDateFormat("u d MM");
        String temp = df.format(cal.getTime());
        String[] dateSplit = temp.split(" ");

        dayOfWeek = Integer.parseInt(dateSplit[0]) - 1;
        date = dateSplit[2] + "/" + dateSplit[1];
    }

    public void onViewWorkoutFragmentCreated(ViewWorkoutFragment fragment) {
        this.fragment = fragment;

        String[] workouts = program.split("\n");
        String currentWorkout = workouts[dayOfWeek];

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

    public void onViewOldWorkoutFragmentCreated(ViewOldWorkoutFragment fragment) {
        this.fragmentOldWorkout = fragment;

        String[] workouts = program.split("\n");
        String currentWorkout = workouts[dayOfWeek];

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

    public void loadViewWorkoutFragment() {
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.placeholder_follow, new ViewWorkoutFragment(this));

        //Clears other fragment from BackStack before adding new fragment
        fm.popBackStack();
        ft.addToBackStack(null);
        ft.commit();
    }

    public void loadViewOldWorkoutFragment() {
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.placeholder_follow, new ViewOldWorkoutFragment(this));

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
                setNumber[0] = String.valueOf(pos + 1);
                break;
            case R.id.exercise2_spinner:
                exerciseRepsWeight[1].setText(exerciseInfo[1][3] + " for " + exerciseInfo[1][2]);
                setNumber[1] = String.valueOf(pos + 1);
                break;
            case R.id.exercise3_spinner:
                exerciseRepsWeight[2].setText(exerciseInfo[2][3] + " for " + exerciseInfo[2][2]);
                setNumber[2] = String.valueOf(pos + 1);
                break;
            default:
                break;
        }
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    public void addVideo(final int exercise) {
        String[] projection = {
                VideoProvider.VIDEO_TABLE_COL_ID,
                VideoProvider.VIDEO_TABLE_COL_FIREBASE_ID,
                VideoProvider.VIDEO_TABLE_COL_FILENAME,
                VideoProvider.VIDEO_TABLE_COL_DATE,
                VideoProvider.VIDEO_TABLE_COL_EXERCISE,
                VideoProvider.VIDEO_TABLE_COL_SETNUMBER };

        final String selectionArgs[] = { firebaseID, date, exerciseInfo[exercise][0], setNumber[exercise] };
        Cursor myCursor = view.getContentResolver().query(VideoProvider.CONTENT_URI,projection,"FIREBASE_ID = ? AND DATE = ? AND EXERCISE = ? AND SETNUMBER = ?",selectionArgs,null);
        if (myCursor.getCount() == 0) {
            dispatchVideoIntent(exercise);
        }
        else {
            //Create a pop-up dialog that the user can specify the title of the item
            AlertDialog.Builder builder = new AlertDialog.Builder(view);
            builder.setTitle("Video already exists! Overwrite?");

            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    view.getContentResolver().delete(VideoProvider.CONTENT_URI, "FIREBASE_ID = ? AND DATE = ? AND EXERCISE = ? AND SETNUMBER = ?", selectionArgs);
                    dispatchVideoIntent(exercise);
                }
            });

            builder.setNeutralButton("NO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.show();
        }
        myCursor.close();
    }

    private void dispatchVideoIntent(int exercise) {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        //Ensure that there's a camera activity to handle the intent
        if (takeVideoIntent.resolveActivity(view.getPackageManager()) != null) {
            //Create a file where the photo should go
            File videoFile = null;
            try {
                videoFile = createVideoFile(exercise);
            } catch (IOException ex) {
                //Error occurred while creating the file
            }
            //Continue only if the file was successfully created
            if (videoFile != null) {
                Uri videoURI = FileProvider.getUriForFile(view, "com.example.fitnessjournal.android.fileprovider", videoFile);
                Log.d("booty", videoURI.toString());
                takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, videoURI);
                view.startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
            }
        }
    }

    private File createVideoFile(int exercise) throws IOException {
        //Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        //String imageFileName = "JPEG_" + timeStamp + "_";
        String videoFileName = "video_" + firebaseID + "_" + timeStamp + "_";
        File storageDir = view.getExternalFilesDir(Environment.DIRECTORY_MOVIES);
        File video = File.createTempFile(
                videoFileName,
                ".mp4",
                storageDir
        );

        //Save a file: path for use with ACTION_VIEW intents
        currentVideoPath = video.getAbsolutePath();

        ContentValues myCV = new ContentValues();
        myCV.put(VideoProvider.VIDEO_TABLE_COL_FILENAME, currentVideoPath);
        myCV.put(VideoProvider.VIDEO_TABLE_COL_FIREBASE_ID, firebaseID);
        myCV.put(VideoProvider.VIDEO_TABLE_COL_DATE, date);
        myCV.put(VideoProvider.VIDEO_TABLE_COL_EXERCISE, exerciseInfo[exercise][0]);

        switch (exercise) {
            case 0:
                myCV.put(VideoProvider.VIDEO_TABLE_COL_SETNUMBER, setNumber[0]);
                break;
            case 1:
                myCV.put(VideoProvider.VIDEO_TABLE_COL_SETNUMBER, setNumber[1]);
                break;
            case 2:
                myCV.put(VideoProvider.VIDEO_TABLE_COL_SETNUMBER, setNumber[2]);
                break;
            default:
                break;
        }

        view.getContentResolver().insert(VideoProvider.CONTENT_URI, myCV);
        return video;
    }

    public void viewVideo(int exercise) {
        String[] projection = {
                VideoProvider.VIDEO_TABLE_COL_ID,
                VideoProvider.VIDEO_TABLE_COL_FIREBASE_ID,
                VideoProvider.VIDEO_TABLE_COL_FILENAME,
                VideoProvider.VIDEO_TABLE_COL_DATE,
                VideoProvider.VIDEO_TABLE_COL_EXERCISE,
                VideoProvider.VIDEO_TABLE_COL_SETNUMBER };

        String selectionArgs[] = { firebaseID, date, exerciseInfo[exercise][0], setNumber[exercise] };

        Cursor myCursor = view.getContentResolver().query(VideoProvider.CONTENT_URI,projection,"FIREBASE_ID = ? AND DATE = ? AND EXERCISE = ? AND SETNUMBER = ?",selectionArgs,null);
        if (myCursor != null && myCursor.getCount() > 0) {
            myCursor.moveToFirst();
            String videoPath = myCursor.getString(2);

            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.placeholder_view_video, new VideoViewerFragment(this, videoPath, exercise));
            ft.addToBackStack(null);
            ft.commit();
        }
        else {
            Toast.makeText(view, "No video exists!", Toast.LENGTH_LONG).show();
        }
    }

    public void setFavorite(int exercise) {
        String selectionArgs[] = { firebaseID, date, exerciseInfo[exercise][0], setNumber[exercise] };
        ContentValues myCV = new ContentValues();
        myCV.put(VideoProvider.VIDEO_TABLE_COL_FAVORITE, "true");
        view.getContentResolver().update(VideoProvider.CONTENT_URI, myCV, "FIREBASE_ID = ? AND DATE = ? AND EXERCISE = ? AND SETNUMBER = ?", selectionArgs);
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
