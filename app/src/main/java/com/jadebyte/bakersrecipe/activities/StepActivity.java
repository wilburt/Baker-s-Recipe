package com.jadebyte.bakersrecipe.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.jadebyte.bakersrecipe.R;
import com.jadebyte.bakersrecipe.fragments.StepDetailsFragment;
import com.jadebyte.bakersrecipe.fragments.StepFragment;
import com.jadebyte.bakersrecipe.listeners.StepClickListener;
import com.jadebyte.bakersrecipe.pojos.Recipe;
import com.jadebyte.bakersrecipe.utils.Constants;

public class StepActivity extends AppCompatActivity implements StepClickListener {
    private final String TAG = "StepActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Bundle bundle = getIntent().getExtras();

        if (bundle == null) {
            finish();
        }
        Recipe recipe = bundle.getParcelable(Constants.Keys.RECIPE_OBJECT);

        startStepFragment(recipe);
        getSupportActionBar().setTitle(recipe.getName());
    }

    private void startStepFragment(Recipe recipe) {
        StepFragment detailsFragment = new StepFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.Keys.RECIPE_OBJECT, recipe);
        detailsFragment.setArguments(bundle);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.fragment_container, detailsFragment, "StepFragment");
        ft.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right,
                android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commitAllowingStateLoss();

    }

    @Override
    public void showFullStep(Recipe recipe, int position) {
        if (getResources().getBoolean(R.bool.is_single_pane)) {
            Intent intent = new Intent(this, StepDetailsActivity.class);
            intent.putExtra(Constants.Keys.RECIPE_OBJECT, recipe);
            intent.putExtra(StepDetailsActivity.POSITION, position);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

        } else {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.step_fragment_container, StepDetailsFragment.newInstance(recipe.getSteps(), position), "StepDetailsFragment");
            ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, R.anim.slide_in_left, R.anim.slide_out_right);
            ft.commitAllowingStateLoss();
        }
    }
}
