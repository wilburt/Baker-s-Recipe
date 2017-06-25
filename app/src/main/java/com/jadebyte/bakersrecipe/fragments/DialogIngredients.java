package com.jadebyte.bakersrecipe.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jadebyte.bakersrecipe.R;
import com.jadebyte.bakersrecipe.adapters.IngredientAdapter;
import com.jadebyte.bakersrecipe.pojos.Ingredient;

import java.util.ArrayList;

public class DialogIngredients extends DialogFragment {
    private static final String INGREDIENTS = "ingredients";
    private ArrayList<Ingredient> ingredients;



    public DialogIngredients() {
        // Required empty public constructor
    }


    public static DialogIngredients newInstance(ArrayList<Ingredient> ingredients) {
        DialogIngredients fragment = new DialogIngredients();
        Bundle args = new Bundle();
        args.putParcelableArrayList(INGREDIENTS, ingredients);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            ingredients = getArguments().getParcelableArrayList(INGREDIENTS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_ingredients, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDialog().setTitle(R.string.ingredients);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.rv_ingredients);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(new IngredientAdapter(ingredients));
    }
}
