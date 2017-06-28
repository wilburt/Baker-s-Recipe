package com.jadebyte.bakersrecipe.listeners;

import com.jadebyte.bakersrecipe.pojos.Recipe;

/**
 * Created by wilbur on 6/25/17 at 12:00 PM.
 */

public interface StepClickListener {
    void showFullStep(Recipe recipe, int position);
}
