
package com.jadebyte.bakersrecipe.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.jadebyte.bakersrecipe.R;
import com.jadebyte.bakersrecipe.listeners.RecipeClickedListener;
import com.jadebyte.bakersrecipe.pojos.Recipe;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    @BindView(R.id.recipe_image) ImageView recipeImage;
    @BindView(R.id.ripple_view) View view;
    @BindView(R.id.recipe_ingredients) TextView tvIngredient;
    @BindView(R.id.recipe_steps) TextView tvSteps;
    @BindView(R.id.recipe_serving) TextView tvServing;
    @BindView(R.id.recipe_name) TextView tvTitle;

    private RecipeClickedListener clickedListener;
    private List<Recipe> recipesList;
    private boolean mIsInTheMiddle = false;

    public RecipeViewHolder(final View View, RecipeClickedListener clickedListener, List<Recipe> recipesList) {
        super(View);
        ButterKnife.bind(this, View);
        this.clickedListener = clickedListener;
        this.recipesList = recipesList;
        view.setOnClickListener(this);
        tvIngredient.setOnClickListener(this);
        tvSteps.setOnClickListener(this);
        tvServing.setOnClickListener(this);
    }

    public void bindModel(final Recipe recipe) {
        Glide.with(itemView.getContext())
                .load(recipe.getImage())
                .asBitmap()
                .error(R.drawable.ic_recipe)
                .placeholder(R.drawable.ic_recipe)
                .skipMemoryCache(true) // Just to be on the save path, don't cache images in memory since many images will be loaded in RecyclerView
                // This is to avoid the popular plague: OutOfMemoryException. Disk cache should be enough.
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .into(recipeImage);
        tvTitle.setText(recipe.getName());
        tvIngredient.setText(String.valueOf(recipe.getIngredients().size()));
        tvSteps.setText(String.valueOf(recipe.getSteps().size()));
        tvServing.setText(String.valueOf(recipe.getServings()));
    }

    @Override
    public void onClick(View view) {
        final Recipe recipe = recipesList.get(getAdapterPosition());

        switch (view.getId()) {
            case R.id.ripple_view:
                clickedListener.onWholeRecipeClicked(recipe);
                break;

            case R.id.recipe_ingredients:
                clickedListener.onIngredientClicked(recipe.getIngredients());
                break;
            
            case R.id.recipe_steps:
                Toast.makeText(itemView.getContext(), itemView.getContext().getString(R.string.steps_in_recipe, recipe.getSteps().size()), Toast
                        .LENGTH_SHORT).show();
                break;

            case R.id.recipe_serving:
                Toast.makeText(itemView.getContext(), itemView.getContext().getString(R.string.servings_in_recipe, recipe.getServings()), Toast
                        .LENGTH_SHORT).show();
                break;
        }
    }

    public boolean getIsInTheMiddle() {
        return mIsInTheMiddle;
    }

    public void setIsInTheMiddle(boolean isInTheMiddle) {
        mIsInTheMiddle = isInTheMiddle;
    }
}
