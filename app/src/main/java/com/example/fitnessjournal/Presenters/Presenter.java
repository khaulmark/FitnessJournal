package com.example.fitnessjournal.Presenters;

import android.os.Bundle;

public interface Presenter {

    void onCreate();
    void onPause();
    void onResume();
    void onDestroy();
}