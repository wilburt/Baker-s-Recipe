package com.jadebyte.bakersrecipe.utils;

import android.net.Uri;

public final class Constants {

    public static final class Keys {
        public static final String RECIPE_OBJECT = "recipeObject";
        public static final String SAVED_INGREDIENTS_SET = "savedIngredientsSet";
        public static final String SAVED_INGREDIENTS_TITLE = "savedIngredientsTitle";
        public static final String SAVED_INGREDIENTS = "savedIngredients";
    }

    public static final class URLS {

        public static String getRecipeUrl() {
            return new Uri.Builder().scheme("https").authority("d17h27t6h515a5.cloudfront.net").appendPath("topher").appendPath("2017")
                    .appendPath("May").appendPath("59121517_baking").appendPath("baking.json").build().toString();
        }
    }

}
