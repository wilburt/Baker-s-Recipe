package com.jadebyte.bakersrecipe.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jadebyte.bakersrecipe.R;
import com.jadebyte.bakersrecipe.pojos.Ingredient;
import com.jadebyte.bakersrecipe.viewholders.IngredientViewHolder;

import java.util.ArrayList;

/**
 * Created by wilbur on 6/18/17 at 1:20 PM.
 */

public class IngredientAdapter extends RecyclerView.Adapter<IngredientViewHolder> {
    private ArrayList<Ingredient> ingredients;

    public IngredientAdapter(ArrayList<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    @Override
    public IngredientViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ingredient_item, parent, false);
        return new IngredientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(IngredientViewHolder holder, int position) {
        holder.bindModel(ingredients.get(position));
    }

    @Override
    public int getItemCount() {
        return ingredients  == null ? 0 : ingredients.size();
    }
}
