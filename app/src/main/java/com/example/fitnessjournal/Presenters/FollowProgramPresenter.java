package com.example.fitnessjournal.Presenters;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
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
import com.example.fitnessjournal.Models.VideoProvider;
import com.example.fitnessjournal.R;
import com.example.fitnessjournal.Views.DatePickerFragment;
import com.example.fitnessjournal.Views.FollowProgramActivity;
import com.example.fitnessjournal.Views.VideoViewerFragment;
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
    private FragmentManager fm;

    //Variables used to populate layouts
    private String firebaseID;
    private String program;

    //Used for TodaysWorkout Fragment
    private String[][] exerciseInfo = new String[3][4];
    private int dayOfWeek;

    //Used for ViewOldWorkout Fragment
    private String oldSetNumbers;
    private String oldRepsWeight;
    private String oldExercises;
    private int numOldExercises;

    //Used for both
    private String[] setNumber = new String[3];
    private TextView[] exerciseRepsWeight = new TextView[3];
    private String date;
    private boolean todayOrOld;

    private static final int REQUEST_VIDEO_CAPTURE = 1;


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

        String selectionArgs[] = { ID };

        //TODO Update this to use FirebaseID... should send FirebaseID in intent extra rather than ID
        //Queries the DB with local ID to retrieve FirebaseID and program
        Cursor myCursor = view.getContentResolver().query(JournalProvider.CONTENT_URI,projection,"_ID = ?",selectionArgs,null);
        if (myCursor != null && myCursor.getCount() > 0){
            myCursor.moveToFirst();
            firebaseID = myCursor.getString(1);
            program = myCursor.getString(2);
        }
        myCursor.close();
        fm = view.getSupportFragmentManager();
    }

    //Runs when user selects Todays Workout
    public void goToTodaysWorkout() {
        if (!program.equals("None")) {
            //Sets the date
            Calendar cal = Calendar.getInstance();
            parseDate(cal);

            //Loads a ViewWorkoutFragment with true flag
            loadFragment(true);
        }
        else {
            //If the user has not uploaded a program, they receive message and nothing happens
            Toast.makeText(view, "No program uploaded!", Toast.LENGTH_LONG).show();
        }
    }

    //Runs when user selects View Previous Workout
    public void goToPreviousWorkout() {
        //Opens the calendar dialog and sets the date for the alarm
        DialogFragment datePicker = new DatePickerFragment(this);
        datePicker.show(view.getSupportFragmentManager(), "date picker");
    }

    //Date from calendar dialog is sent here.. will run automatically after datePicker fragment is used
    @Override
    public void onDateSet(DatePicker dateView, int year, int month, int day) {
        //Sets the date (using input from datePicker)
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day);
        parseDate(cal);

        String[] projection = {
                VideoProvider.VIDEO_TABLE_COL_ID,
                VideoProvider.VIDEO_TABLE_COL_FIREBASE_ID,
                VideoProvider.VIDEO_TABLE_COL_FILENAME,
                VideoProvider.VIDEO_TABLE_COL_DATE,
                VideoProvider.VIDEO_TABLE_COL_EXERCISE,
                VideoProvider.VIDEO_TABLE_COL_SETNUMBER,
                VideoProvider.VIDEO_TABLE_COL_REPSWEIGHT };

        final String selectionArgs[] = { firebaseID, date };

        //Queries the VideoDB with FirebaseID to find all video entries for this user on the set date
        Cursor myCursor = view.getContentResolver().query(VideoProvider.CONTENT_URI,projection,"FIREBASE_ID = ? AND DATE = ?",selectionArgs,null);

        //Retrieves the set exercise names, set numbers, and reps/weight data from the cursor and sets them to method variables
        if (myCursor != null && myCursor.getCount() > 0) {
            ArrayList<String> oldExercisesTemp = new ArrayList<>();
            StringBuilder oldSetNumbersTemp = new StringBuilder();
            StringBuilder oldRepsWeightTemp = new StringBuilder();

            //Exercise name is myCursor(4).. fills oldExercises string with all exercises that have a recorded video attached
            for (myCursor.moveToFirst(); !myCursor.isAfterLast(); myCursor.moveToNext()) {
                if (oldExercisesTemp.isEmpty()) {
                    oldExercisesTemp.add(myCursor.getString(4));
                }
                else {
                    if (!oldExercisesTemp.contains(myCursor.getString(4))) {
                        oldExercisesTemp.add(myCursor.getString(4));
                    }
                }
            }

            //Each exercise is delimited with an ;
            oldExercises = android.text.TextUtils.join(";", oldExercisesTemp);

            //SetNumber is myCursor(5) and repsWeight is myCursor(6)
            ArrayList<String> tempSetList = new ArrayList<>();
            ArrayList<String> tempRepsWeightList = new ArrayList<>();
            for (String s : oldExercisesTemp) {
                    tempSetList.clear();
                    tempRepsWeightList.clear();

                    //Adds setNumber and repsWeight data to corresponding position in list that matches with exercise name
                    for (myCursor.moveToFirst(); !myCursor.isAfterLast(); myCursor.moveToNext()) {
                        if (myCursor.getString(4).equals(s)) {
                            tempSetList.add(myCursor.getString(5));
                            tempRepsWeightList.add(myCursor.getString(6));
                        }
                    }

                    //Each item is delimited with a , per exercise and separated by ; between exercises
                    oldSetNumbersTemp.append(android.text.TextUtils.join(",", tempSetList) + ";");
                    oldRepsWeightTemp.append(android.text.TextUtils.join(",", tempRepsWeightList) + ";");
            }
            oldSetNumbers = oldSetNumbersTemp.toString();
            oldRepsWeight = oldRepsWeightTemp.toString();

            myCursor.close();

            //Loads ViewWorkoutFragment with false flag
            loadFragment(false);
        }
        else {
            Toast.makeText(view, "No videos recorded for that day.", Toast.LENGTH_LONG).show();
        }
    }

    //Gets the current date and sets the date string
    private void parseDate(Calendar cal) {
        DateFormat df = new SimpleDateFormat("u d MM");
        String temp = df.format(cal.getTime());
        String[] dateSplit = temp.split(" ");

        dayOfWeek = Integer.parseInt(dateSplit[0]) - 1;
        date = dateSplit[2] + "/" + dateSplit[1];
    }

    //Called by ViewWorkoutFragment for TodaysWorkout
    public void onViewWorkoutFragmentCreated(ViewWorkoutFragment fragment) {
        this.fragment = fragment;
        todayOrOld = true;

        //Parses the program string from the DB to set the fields in the layout
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

            //Sets the adapters for the spinners in the fragment
            for (int i = 0; i < exercises.length; i++) {
                int setNumber = Integer.parseInt(exerciseInfo[i][1]);
                String[] items = new String[setNumber];

                //The spinner shows each set up to the number of the sets for the exercise
                for (int j = 0; j < setNumber; j++) {
                    items[j] = String.valueOf(j + 1);
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(view, R.layout.spinner_text, items);
                adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown);
                spinners[i].setAdapter(adapter);

                //Sets the title of the exercises
                exerciseTitleTextViews[i].setText(exerciseInfo[i][0]);
            }

            //Sets the UI to be the rest UI or not
            fragment.restUI(false);
        }
        else {
            fragment.restUI(true);
        }
    }

    //Called by ViewWorkoutFragment for ViewOldWOrkout
    public void onViewOldWorkoutFragmentCreated(ViewWorkoutFragment fragment) {
        this.fragment = fragment;
        todayOrOld = false;

        //Uses the strings set by onDateSet to fill out layout fields
        String[] exercises = oldExercises.split(";");
        numOldExercises = exercises.length;

        Spinner[] spinners = fragment.getSpinners();
        TextView[] exerciseTitleTextViews = fragment.getTitleTextViews();
        exerciseRepsWeight = fragment.getRepsWeightTextViews();

        String[] setInfo = oldSetNumbers.split(";");

        //Sets the adapters for the spinners in the fragment
        for (int i = 0; i < numOldExercises; i++) {
            String[] items = setInfo[i].split(",");
            ArrayAdapter<String> adapter = new ArrayAdapter<>(view, R.layout.spinner_text, items);
            adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown);
            spinners[i].setAdapter(adapter);
            exerciseTitleTextViews[i].setText(exercises[i]);
        }

        //Only show exercises that have videos attached
        fragment.hideLayouts(numOldExercises);
    }

    //Starts the ViewWorkout fragment
    private void loadFragment(boolean todayOrOld) {
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.placeholder_follow, new ViewWorkoutFragment(this, todayOrOld));

        //Clears other fragment from BackStack before adding new fragment
        fm.popBackStack();
        ft.addToBackStack(null);
        ft.commit();
    }

    //Runs when spinner item is selected via dropdown
    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)

        //TODO Eventually add a way for user to enter different weight/reps for each set

        //Runs when TodaysWorkout is selected... set number is just equal to the position of the spinner item
        if (todayOrOld) {
            switch (parent.getId()) {
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
        //Runs when ViewOldWorkouts is selected.. set number must be equal to the proper set of the video
        else {
            String[] repsWeightInfo = oldRepsWeight.split(";");
            String[] setInfo = oldSetNumbers.split(";");

            switch (parent.getId()) {
                case R.id.exercise1_spinner:
                    String[] temp1 = repsWeightInfo[0].split(",");
                    exerciseRepsWeight[0].setText(temp1[pos]);

                    String[] items1 = setInfo[0].split(",");
                    setNumber[0] = items1[pos];
                    break;
                //Only runs when 2 or more exercises have videos attached
                case R.id.exercise2_spinner:
                    if (numOldExercises > 1) {
                        String[] temp2 = repsWeightInfo[1].split(",");
                        exerciseRepsWeight[1].setText(temp2[pos]);

                        String[] items2 = setInfo[1].split(",");
                        setNumber[1] = items2[pos];
                    }
                    break;
                //Only runs when 3 or more exercises have videos attached
                case R.id.exercise3_spinner:
                    if (numOldExercises > 2) {
                        String[] temp3 = repsWeightInfo[2].split(",");
                        exerciseRepsWeight[2].setText(temp3[pos]);

                        String[] items3 = setInfo[2].split(",");
                        setNumber[2] = items3[pos];
                    }
                    break;
                default:
                    break;
            }
        }
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    //Runs when the user clicks the Add button in ViewWorkout fragment during after TodaysWorkout
    public void addVideo(final int exercise) {
        String[] projection = {
                VideoProvider.VIDEO_TABLE_COL_ID,
                VideoProvider.VIDEO_TABLE_COL_FIREBASE_ID,
                VideoProvider.VIDEO_TABLE_COL_FILENAME,
                VideoProvider.VIDEO_TABLE_COL_DATE,
                VideoProvider.VIDEO_TABLE_COL_EXERCISE,
                VideoProvider.VIDEO_TABLE_COL_SETNUMBER };

        final String selectionArgs[] = { firebaseID, date, exerciseInfo[exercise][0], setNumber[exercise] };

        //Video must be uniquely selected by using the FirebaseID, date, exercise name, and set number
        Cursor myCursor = view.getContentResolver().query(VideoProvider.CONTENT_URI,projection,"FIREBASE_ID = ? AND DATE = ? AND EXERCISE = ? AND SETNUMBER = ?",selectionArgs,null);
        if (myCursor.getCount() == 0) {
            //Create and store new video
            dispatchVideoIntent(exercise);
        }
        else {
            //Create a pop-up dialog that the user can choose whether to overwrite the video or not
            AlertDialog.Builder builder = new AlertDialog.Builder(view);
            builder.setTitle("Video already exists! Overwrite?");

            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //If the user wishes to overwrite, it will delete the existing video first
                    view.getContentResolver().delete(VideoProvider.CONTENT_URI, "FIREBASE_ID = ? AND DATE = ? AND EXERCISE = ? AND SETNUMBER = ?", selectionArgs);

                    //Create and store new video
                    dispatchVideoIntent(exercise);
                }
            });

            //Does nothing
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

    //Creates a video file, stores it, and then launches the camera activity to record a video
    private void dispatchVideoIntent(int exercise) {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        //Ensure that there's a camera activity to handle the intent
        if (takeVideoIntent.resolveActivity(view.getPackageManager()) != null) {
            //Create a file where the photo should go
            File videoFile = null;
            try {
                //Create file and store it in video DB
                videoFile = createVideoFile(exercise);
            } catch (IOException ex) {
                //Error occurred while creating the file
            }
            //Continue only if the file was successfully created
            if (videoFile != null) {
                //Launch the camera activity
                Uri videoURI = FileProvider.getUriForFile(view, "com.example.fitnessjournal.android.fileprovider", videoFile);
                takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, videoURI);
                view.startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
            }
        }
    }

    //Create file for video and stores it
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
        String currentVideoPath = video.getAbsolutePath();

        //FirebaseID, date, exercise name, and reps/weight info must be stored with video file
        ContentValues myCV = new ContentValues();
        myCV.put(VideoProvider.VIDEO_TABLE_COL_FILENAME, currentVideoPath);
        myCV.put(VideoProvider.VIDEO_TABLE_COL_FIREBASE_ID, firebaseID);
        myCV.put(VideoProvider.VIDEO_TABLE_COL_DATE, date);
        myCV.put(VideoProvider.VIDEO_TABLE_COL_EXERCISE, exerciseInfo[exercise][0]);
        myCV.put(VideoProvider.VIDEO_TABLE_COL_REPSWEIGHT, exerciseRepsWeight[exercise].getText().toString());

        //Determines which set the video belongs to.. also depends on the particular exercise name
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

    //Runs when user clicks View button
    public void viewVideo(int exercise, String exerciseName) {
        String[] projection = {
                VideoProvider.VIDEO_TABLE_COL_ID,
                VideoProvider.VIDEO_TABLE_COL_FIREBASE_ID,
                VideoProvider.VIDEO_TABLE_COL_FILENAME,
                VideoProvider.VIDEO_TABLE_COL_DATE,
                VideoProvider.VIDEO_TABLE_COL_EXERCISE,
                VideoProvider.VIDEO_TABLE_COL_SETNUMBER };

        String selectionArgs[] = { firebaseID, date, exerciseName, setNumber[exercise] };

        //Video must be uniquely selected by using the FirebaseID, date, exercise name, and set number
        Cursor myCursor = view.getContentResolver().query(VideoProvider.CONTENT_URI,projection,"FIREBASE_ID = ? AND DATE = ? AND EXERCISE = ? AND SETNUMBER = ?",selectionArgs,null);
        if (myCursor != null && myCursor.getCount() > 0) {
            myCursor.moveToFirst();
            String videoPath = myCursor.getString(2);

            //Creates a fragment, VideoViewer, that replaces the whole screen with the video.. exercise and name are passed so that it can be set as favorite
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.placeholder_view_video, new VideoViewerFragment(this, videoPath, exercise, exerciseName));
            ft.addToBackStack(null);
            ft.commit();
        }
        else {
            //If no video exists, let the user know... it should always exist for when ViewOldWorkouts button is used
            Toast.makeText(view, "No video exists!", Toast.LENGTH_LONG).show();
        }
        myCursor.close();
    }

    //Sets the video as a favorite by updating its entry in the Video DB
    public void setFavorite(int exercise, String exerciseName) {
        String selectionArgs[] = { firebaseID, date, exerciseName, setNumber[exercise] };
        ContentValues myCV = new ContentValues();
        myCV.put(VideoProvider.VIDEO_TABLE_COL_FAVORITE, "true");

        //Video must be uniquely selected by using the FirebaseID, date, exercise name, and set number
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
