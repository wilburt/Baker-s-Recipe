package com.jadebyte.bakersrecipe.listeners;

import com.jadebyte.bakersrecipe.pojos.Ingredient;
import com.jadebyte.bakersrecipe.pojos.Recipe;

import java.util.ArrayList;

public interface RecipeClickedListener {
    void onWholeRecipeClicked(Recipe recipe);
    void onIngredientClicked(ArrayList<Ingredient> ingredients);
}
