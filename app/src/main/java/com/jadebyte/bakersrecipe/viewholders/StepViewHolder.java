package com.jadebyte.bakersrecipe.viewholders;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.jadebyte.bakersrecipe.R;
import com.jadebyte.bakersrecipe.listeners.StepVideoClickListener;
import com.jadebyte.bakersrecipe.pojos.Step;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by wilbur on 6/18/17 at 4:20 PM.
 */

public class StepViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    @BindView(R.id.step_short_description) TextView tvShortDescription;
    @BindView(R.id.step_description) TextView tvDescription;
    @BindView(R.id.step_video_play) ImageView ivPlay;
    @BindView(R.id.step_image) ImageView imageView;

    private StepVideoClickListener videoClickListener;

    public StepViewHolder(View itemView, StepVideoClickListener videoClickListener) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.videoClickListener = videoClickListener;
        ivPlay.setOnClickListener(this);
    }

    public void bindModel(Step step) {
        tvShortDescription.setText(step.getShortDescription());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            tvDescription.setText(Html.fromHtml(step.getDescription(), Html.FROM_HTML_MODE_COMPACT));
        } else {
            tvDescription.setText(Html.fromHtml(step.getDescription()));

        }
        Glide.with(itemView.getContext())
                .load(step.getThumbnailUrl())
                .into(simpleTarget);

    }

    private SimpleTarget<GlideDrawable> simpleTarget = new SimpleTarget<GlideDrawable>() {
        @Override
        public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
            imageView.setVisibility(View.VISIBLE);
            imageView.setImageDrawable(resource);
        }

        @Override
        public void onLoadFailed(Exception e, Drawable errorDrawable) {
            super.onLoadFailed(e, errorDrawable);
            imageView.setVisibility(View.GONE);
        }
    };


    @Override
    public void onClick(View view) {
        if ((view.getId() == ivPlay.getId())) {
            videoClickListener.play(null, getAdapterPosition());
        }
    }
}
