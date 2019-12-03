package com.example.fitnessjournal.Views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.fragment.app.Fragment;

import com.example.fitnessjournal.Presenters.FollowProgramPresenter;
import com.example.fitnessjournal.R;



public class VideoViewerFragment extends Fragment {

    private FollowProgramPresenter presenter;
    private String videoPath;
    private int exercise;
    private MediaController mediaController;


    public VideoViewerFragment(FollowProgramPresenter presenter, String videoPath, int exercise) {
        this.presenter = presenter;
        this.videoPath = videoPath;
        this.exercise = exercise;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate (R.layout.view_video_fragment, container, false);
        VideoView videoView = (VideoView) rootView.findViewById(R.id.video_view);
        videoView.setVideoPath(videoPath);
        mediaController = new MediaController(getActivity());
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);
        videoView.start();
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.view_video_fragment_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
            case R.id.option_exit_fragment:
                //Delete the note
                getActivity().onBackPressed();
                break;
            case R.id.option_favorite_fragment:
                presenter.setFavorite(exercise);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }
}