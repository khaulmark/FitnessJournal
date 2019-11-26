package com.example.fitnessjournal.Presenters;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.widget.DatePicker;

import androidx.fragment.app.DialogFragment;

import com.example.fitnessjournal.Models.JournalProvider;
import com.example.fitnessjournal.Views.DatePickerFragment;
import com.example.fitnessjournal.Views.FollowProgramActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static com.example.fitnessjournal.Presenters.HomePresenter.EXTRA_MESSAGE_ID;

public class FollowProgramPresenter implements Presenter, DatePickerDialog.OnDateSetListener {
    private FollowProgramActivity view;

    private String ID;
    private String program;
    private int month;
    private int dayOfMonth;
    private int dayOfWeek;

    public FollowProgramPresenter(FollowProgramActivity view) {
        this.view = view;
    }

    @Override
    public void onCreate() {
        Intent intent = view.getIntent();
        ID = intent.getStringExtra(EXTRA_MESSAGE_ID);

        //Query the DB to get the workout string
        String projection[] = {
                JournalProvider.JOURNAL_TABLE_COL_ID,
                JournalProvider.JOURNAL_TABLE_COL_PROGRAM };

        String selectionArgs[] = {ID };

        Cursor myCursor = view.getContentResolver().query(JournalProvider.CONTENT_URI,projection,"_ID = ?",selectionArgs,null);
        if (myCursor != null && myCursor.getCount() > 0){
            myCursor.moveToFirst();
            program = myCursor.getString(1);
        }
    }

    public void goToTodaysWorkout() {
        Calendar cal = Calendar.getInstance();
        parseDate(cal);
    }

    public void goToPreviousWorkout() {
        //Opens the calendar dialog and sets the date for the alarm
        DialogFragment datePicker = new DatePickerFragment(this);
        datePicker.show(view.getSupportFragmentManager(), "date picker");
    }

    //Date from calendar dialog is sent here
    @Override
    public void onDateSet(DatePicker dateView, int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day);
        parseDate(cal);
    }

    private void parseDate(Calendar cal) {
        DateFormat df = new SimpleDateFormat("u d MM");
        String date = df.format(cal.getTime());
        String[] dateSplit = date.split(" ");

        dayOfWeek = Integer.parseInt(dateSplit[0]);
        dayOfMonth = Integer.parseInt(dateSplit[1]);
        month = Integer.parseInt(dateSplit[2]);
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
