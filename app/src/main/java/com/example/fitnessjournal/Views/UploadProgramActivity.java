package com.example.fitnessjournal.Views;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import com.example.fitnessjournal.Presenters.UploadProgramPresenter;
import com.example.fitnessjournal.R;


public class UploadProgramActivity extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView recyclerView;
    protected UploadProgramPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_program);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        //This recycler view defines the list of program days in the program
        recyclerView = (RecyclerView) findViewById(R.id.dayList);

        //Create new HomePresenter for HomeActivity
        presenter = new UploadProgramPresenter(this);
        presenter.onCreate();

        initializeComponents();
    }

    //Set the OnClick Listener for buttons
    void initializeComponents() {
        findViewById(R.id.btn_upload_program_inner).setOnClickListener(this);
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.btn_upload_program_inner:
                presenter.onUploadProgram();
            default:
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.onResume();
    }

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }
}
