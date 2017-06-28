package com.jadebyte.bakersrecipe.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jadebyte.bakersrecipe.R;
import com.jadebyte.bakersrecipe.adapters.StepAdapter;
import com.jadebyte.bakersrecipe.listeners.StepClickListener;
import com.jadebyte.bakersrecipe.pojos.Recipe;
import com.jadebyte.bakersrecipe.utils.Constants;
import com.jadebyte.bakersrecipe.utils.VolleySingleton;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class StepFragment extends Fragment{

    // Constants
    private final String TAG = "StepFragment";

    //Views
    @BindView(R.id.rv_steps) RecyclerView rvSteps;

    //Fields
    private Unbinder unbinder;
    private Recipe recipe;
    private StepClickListener stepClickListener = stepCallbacks;


    public StepFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_step, container, false);
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
        }
    }


    private void setupRecyclerViews() {
        StepAdapter adapter = new StepAdapter(recipe.getSteps());
        rvSteps.setAdapter(adapter);
        rvSteps.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvSteps.setHasFixedSize(true);
        rvSteps.setNestedScrollingEnabled(false);
        adapter.setVideoClickListener(new StepClickListener() {
            @Override
            public void showFullStep(Recipe recipe, int position) {
                stepClickListener.showFullStep(StepFragment.this.recipe, position);
            }
        });
    }

    @OnClick({R.id.recipe_ingredients})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.recipe_ingredients:
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.add(DialogIngredients.newInstance(recipe.getIngredients()), "DialogIngredients");
                transaction.commitAllowingStateLoss();
                break;

        }
    }

    public static StepClickListener stepCallbacks = new StepClickListener() {
        @Override
        public void showFullStep(Recipe recipe, int position) {

        }
    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            stepClickListener = (StepClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " should implement StepClickListener");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        VolleySingleton.getInstance(getActivity()).getRequestQueue().cancelAll(TAG);
        unbinder.unbind();
    }
}
