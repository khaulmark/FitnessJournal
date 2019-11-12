package com.example.fitnessjournal;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import static com.example.fitnessjournal.GoogleSignInActivity.EXTRA_MESSAGE_DISPLAYNAME;

public class HomeScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        Intent intent = getIntent();

        //Get display name
        String displayName = intent.getStringExtra(EXTRA_MESSAGE_DISPLAYNAME);

        TextView textView = (TextView) findViewById(R.id.displayNameText);
        textView.setText(displayName);
        Toast.makeText(this, displayName, Toast.LENGTH_LONG).show();
    }
}
