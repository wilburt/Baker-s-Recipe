package com.jadebyte.bakersrecipe.fragments;


import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.RenderersFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.jadebyte.bakersrecipe.R;
import com.jadebyte.bakersrecipe.pojos.Step;
import com.jadebyte.bakersrecipe.utils.Constants;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class VideoFragment extends Fragment implements ExoPlayer.EventListener {
    private static final String POSITION = "POSITION";
    private final String TAG = "VideoFragment";

    @BindView(R.id.simple_player_view)
    SimpleExoPlayerView playerView;

    private ArrayList<Step> steps;
    private int position;
    private SimpleExoPlayer exoPlayer;
    private Unbinder unbinder;

    public VideoFragment() {
        // Required empty public constructor
    }


    public static VideoFragment newInstance(ArrayList<Step> steps, int position) {
        VideoFragment fragment = new VideoFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(Constants.Keys.RECIPE_OBJECT, steps);
        args.putInt(POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: ");
        if (getArguments() != null) {
            steps = getArguments().getParcelableArrayList(Constants.Keys.RECIPE_OBJECT);
            position = getArguments().getInt(POSITION, 0);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        return inflater.inflate(R.layout.fragment_video, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        if (savedInstanceState != null) {
            position = savedInstanceState.getInt(POSITION, 0);
        }
        initializePlayer();
    }

    private void initializePlayer() {
        Log.d(TAG, "initializePlayer: ");
        if (exoPlayer == null) {
            TrackSelector selector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();
            RenderersFactory renderersFactory = new DefaultRenderersFactory(getActivity());
            exoPlayer = ExoPlayerFactory.newSimpleInstance(renderersFactory, selector, loadControl);
            playerView.setPlayer(exoPlayer);
            preparePlayer(position);
            exoPlayer.addListener(this);
            exoPlayer.setPlayWhenReady(true);
        }
    }


    private void releasePlayer() {
        Log.d(TAG, "releasePlayer: ");
        exoPlayer.stop();
        exoPlayer.release();
        exoPlayer.removeListener(this);
        exoPlayer = null;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(POSITION, position);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        releasePlayer();
        unbinder.unbind();
    }

    private void preparePlayer(int position) {
        Log.d(TAG, "preparePlayer: ");
        final Uri videoUrl = Uri.parse(steps.get(position).getVideoUrl());
        final DefaultDataSourceFactory dataFactory = new DefaultDataSourceFactory(getActivity(), Util.getUserAgent(getActivity(), getString(R
                .string.app_name)));
        MediaSource mediaSource = new ExtractorMediaSource(videoUrl, dataFactory, new DefaultExtractorsFactory(), null, null);
        exoPlayer.prepare(mediaSource);
    }

    private boolean stepIsAtEnd() {
        return position == steps.size() - 1;

    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if (playbackState == ExoPlayer.STATE_ENDED && !stepIsAtEnd()) {
            preparePlayer(position+1);
        }
    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity() {

    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

    }
}
