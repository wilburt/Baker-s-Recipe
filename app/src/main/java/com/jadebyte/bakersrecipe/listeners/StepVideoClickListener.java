package com.jadebyte.bakersrecipe.listeners;

import com.jadebyte.bakersrecipe.pojos.Step;

import java.util.ArrayList;

/**
 * Created by wilbur on 6/25/17 at 12:00 PM.
 */

public interface StepVideoClickListener {
    void play(ArrayList<Step> steps, int position);
}
