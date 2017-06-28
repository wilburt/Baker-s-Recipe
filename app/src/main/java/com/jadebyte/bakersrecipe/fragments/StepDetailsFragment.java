package com.jadebyte.bakersrecipe.fragments;


import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.RenderersFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.jadebyte.bakersrecipe.R;
import com.jadebyte.bakersrecipe.pojos.Step;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class StepDetailsFragment extends Fragment {
    private final String TAG = "StepDetailsFragment";
    private static final String STEPS = "steps";
    private static final String POSITION = "position";

    @BindView(R.id.simple_player_view) SimpleExoPlayerView playerView;
    @BindView(R.id.step_description) TextView descriptionTv;
    @BindView(R.id.step_thumb_nail) ImageView stepThumbNail;

    private ArrayList<Step> stepsList;
    private int position;
    private Step step;
    private SimpleExoPlayer exoPlayer;
    private Unbinder unbinder;

    public StepDetailsFragment() {
        // Required empty public constructor
    }


    public static StepDetailsFragment newInstance(ArrayList<Step> steps, int position) {
        StepDetailsFragment fragment = new StepDetailsFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(STEPS, steps);
        args.putInt(POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            stepsList = getArguments().getParcelableArrayList(STEPS);
            position = getArguments().getInt(POSITION);
            step = stepsList.get(position);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_step_details, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);

        descriptionTv.setText(step.getDescription());
        loadThumbNail();
    }


    private void loadThumbNail() {
        Glide.with(getActivity()).load(step.getThumbnailUrl()).into(stepThumbNail);
    }


    private void initializePlayer() {
        if (exoPlayer == null) {
            TrackSelector selector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();
            RenderersFactory renderersFactory = new DefaultRenderersFactory(getActivity());
            exoPlayer = ExoPlayerFactory.newSimpleInstance(renderersFactory, selector, loadControl);
            playerView.setPlayer(exoPlayer);
            preparePlayer();
            exoPlayer.setPlayWhenReady(true);
        }
    }


    private void releasePlayer() {
        exoPlayer.stop();
        exoPlayer.release();
        exoPlayer = null;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onStart() {
        super.onStart();
        initializePlayer();
    }

    @Override
    public void onStop() {
        super.onStop();
        releasePlayer();
    }

    private void preparePlayer() {
        final Uri videoUrl = Uri.parse(step.getVideoUrl());
        final DefaultDataSourceFactory dataFactory = new DefaultDataSourceFactory(getActivity(), Util.getUserAgent(getActivity(), getString(R
                .string.app_name)));
        MediaSource mediaSource = new ExtractorMediaSource(videoUrl, dataFactory, new DefaultExtractorsFactory(), null, null);
        exoPlayer.prepare(mediaSource);
    }
}


