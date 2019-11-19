package com.example.fitnessjournal;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import static com.example.fitnessjournal.GoogleSignInActivity.EXTRA_MESSAGE_FIREBASEID;

public class UploadProgramActivity extends AppCompatActivity implements WorkoutSetFragment.OnFragmentDoneListener {

    private String firebaseID;
    private ArrayList<String> workoutDays;
    private RecyclerView recyclerView;
    private MyAdapter adapter;
    
    private ArrayList<String> workoutString;

    private Fragment workoutSetFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_program);
        Intent intent = getIntent();

        firebaseID = intent.getStringExtra(EXTRA_MESSAGE_FIREBASEID);

        recyclerView = (RecyclerView) findViewById(R.id.dayList);
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

        workoutSetFragment = (Fragment) getSupportFragmentManager().findFragmentById(R.id.workout_set_fragment);

        adapter = new MyAdapter(this, workoutDays,
                new MyAdapter.OnMyAdapterItemClickListener() {
                    @Override
                    public void onItemClicked(int position) {
                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                        ft.replace(R.id.placeholder, new WorkoutSetFragment(workoutString.get(position)));
                        ft.addToBackStack(null);
                        ft.commit();
                    }
                }
        );

    }

    @Override
    public void OnFragmentDone(String editText, boolean saveOrDiscard) {
        //TODO parse the workout text and save to db
    }
}
