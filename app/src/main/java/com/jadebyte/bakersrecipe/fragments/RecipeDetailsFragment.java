package com.jadebyte.bakersrecipe.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jadebyte.bakersrecipe.R;
import com.jadebyte.bakersrecipe.adapters.StepAdapter;
import com.jadebyte.bakersrecipe.listeners.StepVideoClickListener;
import com.jadebyte.bakersrecipe.pojos.Recipe;
import com.jadebyte.bakersrecipe.pojos.Step;
import com.jadebyte.bakersrecipe.utils.Constants;
import com.jadebyte.bakersrecipe.utils.VolleySingleton;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class RecipeDetailsFragment extends Fragment{

    // Constants
    private final String TAG = "RecipeDetailsFragment";

    //Views
    @BindView(R.id.recipe_title) TextView recipeTitle;
    @BindView(R.id.recipe_ingredients) TextView recipeIngredients;
    @BindView(R.id.recipe_steps) TextView recipeSteps;
    @BindView(R.id.rv_steps) RecyclerView rvSteps;

    //Fields
    private Unbinder unbinder;
    private Recipe recipe;
    private StepVideoClickListener videoClickListener = playCallbacks;


    public RecipeDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recipe_fragment_details, container, false);
        unbinder = ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getRecipeBundle();
        setupRecyclerViews();
    }

    private void getRecipeBundle() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            recipe = bundle.getParcelable(Constants.Keys.RECIPE_OBJECT);
            assert recipe != null;

            recipeTitle.setText(recipe.getName());
            recipeIngredients.setText(String.valueOf(recipe.getIngredients().size()));
            recipeSteps.setText(String.valueOf(recipe.getSteps().size()));
        }
    }


    private void setupRecyclerViews() {
        StepAdapter adapter = new StepAdapter(recipe.getSteps());
        rvSteps.setAdapter(adapter);
        rvSteps.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvSteps.setNestedScrollingEnabled(false);
        adapter.setVideoClickListener(new StepVideoClickListener() {
            @Override
            public void play(ArrayList<Step> steps, int position) {
                videoClickListener.play(recipe.getSteps(), position);
            }
        });
    }

    @OnClick({R.id.recipe_ingredients, R.id.recipe_steps})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.recipe_ingredients:
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.add(DialogIngredients.newInstance(recipe.getIngredients()), "DialogIngredients");
                transaction.commitAllowingStateLoss();
                break;

            case R.id.recipe_steps:
                showLabelsPopup(view);
                break;

        }
    }

    private void showLabelsPopup(View view) {
        PopupMenu popup = new PopupMenu(getActivity(), view);
        final ArrayList<Step> steps = recipe.getSteps();
        for (int i = 0; i < steps.size(); i++) {
            final Step step = steps.get(i);
            popup.getMenu().add(Menu.NONE, Menu.NONE, i, i + 1 + ". " + step.getShortDescription());
        }
        popup.show();
    }

    public static StepVideoClickListener playCallbacks = new StepVideoClickListener() {
        @Override
        public void play(ArrayList<Step> steps, int position) {

        }
    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            videoClickListener = (StepVideoClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " should implement StepVideoClickListener");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        VolleySingleton.getInstance(getActivity()).getRequestQueue().cancelAll(TAG);
        unbinder.unbind();
    }
}
