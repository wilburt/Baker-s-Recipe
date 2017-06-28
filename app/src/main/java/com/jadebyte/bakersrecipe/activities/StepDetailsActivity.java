package com.jadebyte.bakersrecipe.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
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
import com.jadebyte.bakersrecipe.pojos.Recipe;
import com.jadebyte.bakersrecipe.pojos.Step;
import com.jadebyte.bakersrecipe.utils.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;


public class StepDetailsActivity extends AppCompatActivity implements View.OnClickListener{

    public static final String POSITION = "position";

    @BindView(R.id.simple_player_view) SimpleExoPlayerView playerView;
    @Nullable @BindView(R.id.step_description) TextView descriptionTv;
    @Nullable @BindView(R.id.step_thumb_nail) ImageView stepThumbNail;
    @Nullable @BindView(R.id.step_next) Button buttonNext;
    @Nullable @BindView(R.id.step_previous) Button buttonPrevious;
    @BindView(R.id.fullscreen_content)
    FrameLayout contentView;

    private Recipe recipe;
    private int position;
    private Step step;
    private SimpleExoPlayer exoPlayer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_details);
        ButterKnife.bind(this);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            recipe = bundle.getParcelable(Constants.Keys.RECIPE_OBJECT);
            position = bundle.getInt(POSITION);
            step = recipe.getSteps().get(position);
        } else {
            finish();
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(recipe.getName());
        }



        hideButtons();

        if (descriptionTv != null) {
            descriptionTv.setText(step.getDescription());
            hideFullScreen();
            Log.d("StepDetailsActivity", "onCreate: Hide full screen");
        } else {
            Log.d("StepDetailsActivity", "onCreate: SHow full Screen");
            makeFullScreen();
        }

        loadThumbNail();
    }



    private void loadThumbNail() {
        if (stepThumbNail != null) {
            Glide.with(this).load(step.getThumbnailUrl()).into(stepThumbNail);
        }
    }

    private void hideButtons() {
        if (buttonPrevious != null) {
            buttonPrevious.setOnClickListener(this);
            if (position == 0) {
                buttonPrevious.setVisibility(View.GONE);
            }
        }

        if (buttonNext != null) {
            buttonNext.setOnClickListener(this);

            if (position == recipe.getSteps().size() - 1) {
                buttonNext.setVisibility(View.GONE);
            }
        }
    }

    private void initializePlayer() {
        if (exoPlayer == null) {
            TrackSelector selector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();
            RenderersFactory renderersFactory = new DefaultRenderersFactory(this);
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
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.step_previous:
                showNextOrPrevious(false);
                break;

            case R.id.step_next:
                showNextOrPrevious(true);
                break;
        }
    }

    private void showNextOrPrevious(boolean isNext) {
        int position;
        int animEnter;
        int animExit;
        if (isNext) {
            position = this.position + 1; // We shouldn't worry about IndexOutOfBoundE since the button will be hidden if position is the last item
            animEnter = R.anim.slide_in_left;
            animExit = R.anim.slide_out_right;

        } else {
            position = this.position - 1; //  We shouldn't worry about negative numbers since the button will be hidden if position is the first item
            animEnter = R.anim.slide_in_right;
            animExit = R.anim.slide_out_left;
        }

        Intent intent = new Intent(this, StepDetailsActivity.class);
        intent.putExtra(Constants.Keys.RECIPE_OBJECT, recipe);
        intent.putExtra(POSITION, position);
        startActivity(intent);
        finish();
        overridePendingTransition(animEnter, animExit);
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
        Log.d("StepDetailsActivity", "preparePlayer: " + step.getVideoUrl());
        final Uri videoUrl = Uri.parse(step.getVideoUrl());
        final DefaultDataSourceFactory dataFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, getString(R
                .string.app_name)));
        MediaSource mediaSource = new ExtractorMediaSource(videoUrl, dataFactory, new DefaultExtractorsFactory(), null, null);
        exoPlayer.prepare(mediaSource);
    }


    private void makeFullScreen() {ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        contentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    private void hideFullScreen() {
        contentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.show();
        }
    }

}
