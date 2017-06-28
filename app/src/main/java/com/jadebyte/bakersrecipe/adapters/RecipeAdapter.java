package com.jadebyte.bakersrecipe.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.TranslateAnimation;

import com.jadebyte.bakersrecipe.listeners.RecipeClickedListener;
import com.jadebyte.bakersrecipe.pojos.Recipe;
import com.jadebyte.bakersrecipe.viewholders.RecipeViewHolder;

import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeViewHolder> {
    private RecipeClickedListener onRecipeClicked;
    private List<Recipe> recipeList;
    private int lastPosition = -1;
    private int recipeItemLayoutId;

    public RecipeAdapter(List<Recipe> recipeList, int recipeItemLayoutId) {
        super();
        this.recipeList = recipeList;
        this.onRecipeClicked = null;
        this.recipeItemLayoutId = recipeItemLayoutId;
    }

    public void setOnRecipeClickedListener (RecipeClickedListener onItemClickedListener) {
        this.onRecipeClicked = onItemClickedListener;
    }


    @Override
    public RecipeViewHolder onCreateViewHolder (ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(recipeItemLayoutId, parent, false);
        return new RecipeViewHolder(view, onRecipeClicked, recipeList);
    }


    @Override
    public void onBindViewHolder(final RecipeViewHolder holder, int position) {
        holder.bindModel(recipeList.get(position));
        setAnimation(holder.itemView, position);
    }

    private void setAnimation(View viewToAnimate, int position) {
        //if the item wasn't already displayed on screen. it's animated
        if (position > lastPosition) {
            TranslateAnimation animation = new TranslateAnimation(0, 0, 80, 0);
            animation.setDuration(400);
            animation.setInterpolator(new AccelerateDecelerateInterpolator());
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    @Override
    public int getItemCount(){
        //Return the number of items in the data set
        return recipeList == null ? (0) : recipeList.size();
    }

    @Override
    public void onViewDetachedFromWindow(final RecipeViewHolder holder) {
        holder.itemView.clearAnimation();
    }
}
