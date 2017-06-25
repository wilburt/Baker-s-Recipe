package com.jadebyte.bakersrecipe.activities;


import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.jadebyte.bakersrecipe.R;
import com.jadebyte.bakersrecipe.fragments.BlankFragment;
import com.jadebyte.bakersrecipe.fragments.RecipeDetailsFragment;
import com.jadebyte.bakersrecipe.fragments.RecipeListFragment;
import com.jadebyte.bakersrecipe.fragments.VideoFragment;
import com.jadebyte.bakersrecipe.listeners.RecipeClickedListener;
import com.jadebyte.bakersrecipe.listeners.StepVideoClickListener;
import com.jadebyte.bakersrecipe.pojos.Ingredient;
import com.jadebyte.bakersrecipe.pojos.Recipe;
import com.jadebyte.bakersrecipe.pojos.Step;
import com.jadebyte.bakersrecipe.utils.Constants;
import com.jadebyte.bakersrecipe.utils.MyFile;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements RecipeClickedListener, StepVideoClickListener {

    //Views
    @BindView(R.id.recipe_toolbar) Toolbar toolbar;

    //Fields
    private boolean hasTwoPanes;
    private Fragment mContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_bar_recipe);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        hasTwoPanes = getResources().getBoolean(R.bool.has_two_panes);

        if (savedInstanceState == null) {
            launchRecipeListFragment();
            launchBlankFragment();
        } else {
            mContent = getSupportFragmentManager().getFragment(savedInstanceState, "mContent");
        }

    }


    // Start a fragment that does nothing but display
    // {@link com.jadebyte.bakersrecipe.R.string#details_stub } on the right pane if the device is a
    // tablet
    private void launchBlankFragment() {
        if (hasTwoPanes) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out);
            ft.add(R.id.recipe_content_details, new BlankFragment());
            ft.commit();
        }
    }

    private void launchRecipeListFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out);
        ft.replace(R.id.recipe_content_frame, new RecipeListFragment(), "RecipeListFragment");
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commitAllowingStateLoss();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_license:
                showLicenseDialog();
                break;
            case R.id.action_ingredients:
                // Do nothing yet
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    // Start {@link RecipeDetailsFragment
    public void launchDetailsFrag(@NonNull Recipe recipe) {
        RecipeDetailsFragment detailsFragment = new RecipeDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.Keys.RECIPE_OBJECT, recipe);
        detailsFragment.setArguments(bundle);

        int container = hasTwoPanes ? R.id.recipe_content_details : R.id.recipe_content_frame;

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.addToBackStack(null);
        ft.add(container, detailsFragment, "RecipeDetailsFragment");
        ft.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right,
                android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commitAllowingStateLoss();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mContent != null) {
            getSupportFragmentManager().putFragment(outState, "mContent", mContent);
        }
        super.onSaveInstanceState(outState);
    }

    private void showLicenseDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_license, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton(R.string.dismiss, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setView(view);
        TextView textView = (TextView) view.findViewById(R.id.license_text);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            textView.setText(Html.fromHtml(MyFile.readFromAssets(this, "license.html"), Html
                    .FROM_HTML_MODE_COMPACT));
        } else {
            textView.setText(Html.fromHtml(MyFile.readFromAssets(this, "license.html")));
        }
        builder.show();
    }


    @Override
    public void onWholeRecipeClicked(Recipe recipe) {
        launchDetailsFrag(recipe);
    }

    @Override
    public void onIngredientClicked(ArrayList<Ingredient> ingredients) {
        // Chill out. Not for your consumption
    }

    @Override
    public void play(ArrayList<Step> steps, int position) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (!hasTwoPanes) {
            Log.d("MainA", "play: phone");
            ft.addToBackStack(null);
            ft.replace(R.id.recipe_content_frame, VideoFragment.newInstance(steps, position), "VideoFragment");

        } else {
            Log.d("MainA", "play: tablet");
            ft.replace(R.id.video_container, VideoFragment.newInstance(steps, position), "VideoFragment");
        }
        ft.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right,
                android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commitAllowingStateLoss();
    }
}
