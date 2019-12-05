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
    private String exerciseName;
    private int exercise;

    public VideoViewerFragment(FollowProgramPresenter presenter, String videoPath, int exercise, String exerciseName) {
        this.presenter = presenter;
        this.videoPath = videoPath;
        this.exercise = exercise;
        this.exerciseName = exerciseName;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate (R.layout.view_video_fragment, container, false);

        //Creates the videoView and gives it the videoPath we initialized the framgent with
        VideoView videoView = (VideoView) rootView.findViewById(R.id.video_view);
        videoView.setVideoPath(videoPath);

        //Creates a mediaController for the user to rewind, pause, etc. the video with
        MediaController mediaController = new MediaController(getActivity());
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);

        //Start the video
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
                //Exits the video
                getActivity().onBackPressed();
                break;
            case R.id.option_favorite_fragment:
                //Sets the video to a favorite
                presenter.setFavorite(exercise, exerciseName);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }
}