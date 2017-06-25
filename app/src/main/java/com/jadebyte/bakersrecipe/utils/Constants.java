package com.jadebyte.bakersrecipe.utils;

import android.net.Uri;

public final class Constants {

    public static final class Keys {
        public static final String RECIPE_OBJECT = "recipeObject";
    }

    public static final class URLS {

        public static String getRecipeUrl() {
            // https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/baking.json
            return new Uri.Builder().scheme("https").authority("d17h27t6h515a5.cloudfront.net").appendPath("topher").appendPath("2017")
                    .appendPath("May").appendPath("59121517_baking").appendPath("baking.json").build().toString();
        }
    }

}
