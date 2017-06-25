package com.jadebyte.bakersrecipe.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jadebyte.bakersrecipe.R;
import com.jadebyte.bakersrecipe.listeners.StepVideoClickListener;
import com.jadebyte.bakersrecipe.pojos.Step;
import com.jadebyte.bakersrecipe.viewholders.StepViewHolder;

import java.util.List;


public class StepAdapter extends RecyclerView.Adapter<StepViewHolder> {
    private List<Step> stepList;
    private StepVideoClickListener videoClickListener;

    public StepAdapter(List<Step> stepList) {
        this.stepList = stepList;
    }

    public void setVideoClickListener(StepVideoClickListener videoClickListener) {
        this.videoClickListener = videoClickListener;
    }

    @Override
    public StepViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.step_item, parent, false);
        return new StepViewHolder(view, videoClickListener);
    }

    @Override
    public void onBindViewHolder(StepViewHolder holder, int position) {
        holder.bindModel(stepList.get(position));
    }

    @Override
    public int getItemCount() {
        return stepList == null ? 0 : stepList.size();
    }
}
