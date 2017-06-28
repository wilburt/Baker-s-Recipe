package com.jadebyte.bakersrecipe.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.jadebyte.bakersrecipe.R;
import com.jadebyte.bakersrecipe.listeners.StepClickListener;
import com.jadebyte.bakersrecipe.pojos.Step;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by wilbur on 6/18/17 at 4:20 PM.
 */

public class StepViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    @BindView(R.id.step_short_description) TextView tvShortDescription;

    private StepClickListener videoClickListener;

    public StepViewHolder(View itemView, StepClickListener videoClickListener) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.videoClickListener = videoClickListener;
        tvShortDescription.setOnClickListener(this);
    }

    public void bindModel(Step step) {
        tvShortDescription.setText(step.getShortDescription());

    }

    @Override
    public void onClick(View view) {
        videoClickListener.showFullStep(null, getAdapterPosition());
    }
}
