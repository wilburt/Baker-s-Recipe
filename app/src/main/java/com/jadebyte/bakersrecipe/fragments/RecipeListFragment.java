package com.jadebyte.bakersrecipe.fragments;


import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.jadebyte.bakersrecipe.R;
import com.jadebyte.bakersrecipe.adapters.RecipeAdapter;
import com.jadebyte.bakersrecipe.listeners.RecipeClickedListener;
import com.jadebyte.bakersrecipe.pojos.Ingredient;
import com.jadebyte.bakersrecipe.pojos.Recipe;
import com.jadebyte.bakersrecipe.utils.Constants;
import com.jadebyte.bakersrecipe.utils.MyVolleyError;
import com.jadebyte.bakersrecipe.utils.VolleyCache;
import com.jadebyte.bakersrecipe.utils.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
public class RecipeListFragment extends Fragment {

    //Constants
    private final String KEY_RECIPE_ITEMS = "recipeItems";
    private final String TAG = "RecipeListFragment";

    //Views
    @BindView(R.id.recipe_recycler) RecyclerView recyclerView;
    @BindView(R.id.recipe_pro_bar) ProgressBar progressBar;
    @BindView(R.id.recipe_info_error_root) LinearLayout errorLayout;
    @BindView(R.id.recipe_info_error_text) TextView errorText;
    @BindView(R.id.recipe_info_error_button) Button retryButton;


    //Fields
    private String recipeUrl = Constants.URLS.getRecipeUrl();
    private List<Recipe> recipeList;
    private RecipeAdapter adapter;
    private Unbinder unbinder;
    private boolean hasFailed = false;
    private String errorMessage;
    private RecipeClickedListener onRecipeClickedListener = sRecipeCallbacks;

    public RecipeListFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            hasFailed = savedInstanceState.getBoolean("hasFailed");
            errorMessage = savedInstanceState.getString("errorMessage");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipe_list, container, false);
        unbinder = ButterKnife.bind(this, view);

        if (savedInstanceState != null && savedInstanceState.containsKey(KEY_RECIPE_ITEMS)) {
            // A successful network call has been made and parsed previously.
            recipeList = savedInstanceState.getParcelableArrayList(KEY_RECIPE_ITEMS);
        } else if (hasFailed) {
            // An unsuccessful network call has been made previously. Just show the error layout
            errorText.setText(errorMessage);
            errorLayout.setVisibility(View.VISIBLE);
            recipeList = new ArrayList<>();
        } else {
            // No network call has been made or hasn't returned any response
            recipeList = new ArrayList<>();
            getRecipeArray();
        }

        setUpWidgets();
        widgetListeners();

        return view;
    }

    private void setUpWidgets() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new RecipeAdapter(recipeList);
        recyclerView.setAdapter(adapter);
        progressBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(getActivity
                (), R.color.colorAccent), PorterDuff.Mode.SRC_IN);
    }

    private void widgetListeners() {
        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errorLayout.setVisibility(View.GONE);
                getRecipeArray();
            }
        });

        adapter.setOnRecipeClickedListener(new RecipeClickedListener() {
            @Override
            public void onWholeRecipeClicked(Recipe recipe) {
                onRecipeClickedListener.onWholeRecipeClicked(recipe);
            }

            @Override
            public void onIngredientClicked(ArrayList<Ingredient> ingredients) {
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.add(DialogIngredients.newInstance(ingredients), "DialogIngredients");
                transaction.commitAllowingStateLoss();
            }
        });
    }

    // Get recipes from API
    private void getRecipeArray(){
        progressBar.setVisibility(View.VISIBLE);
        //Creating a json request
        JsonArrayRequest recipesRequest = new JsonArrayRequest(recipeUrl, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                // Successful request
                Log.d(TAG, "onResponse");
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
                parseResponse(response);
                hasFailed = false;

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse");
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
                if (adapter.getItemCount() == 1) {
                    errorMessage = MyVolleyError.errorMessage(error, getActivity());
                    errorText.setText(errorMessage);
                    errorLayout.setVisibility(View.VISIBLE);
                    hasFailed = true;
                }
            }
        }){
            @Override
            protected Response<JSONArray> parseNetworkResponse(NetworkResponse response) {
                Response<JSONArray>  resp = super.parseNetworkResponse(response);
                return Response.success(resp.result, VolleyCache.parseIgnoreCacheHeaders(response, 10800000L)); // cache for 3  hours
            }
        };


        RetryPolicy policy = new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        recipesRequest.setRetryPolicy(policy);
        recipesRequest.setShouldCache(true);
        recipesRequest.setTag(TAG);
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(recipesRequest);

    }

    private void parseResponse(JSONArray array) {
        Log.d(TAG, "parseResponse: ");
        try {

            for(int a = 0; a<array.length(); a++) {

                recipeList.add(new Recipe(array.getJSONObject(a)));
            }

            adapter.notifyItemRangeChanged(0, adapter.getItemCount());

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        if (adapter != null && adapter.getItemCount() > 1 ) {
            savedInstanceState.putParcelableArrayList(KEY_RECIPE_ITEMS, (ArrayList<? extends Parcelable>) recipeList);
        }
        savedInstanceState.putBoolean("hasFailed", hasFailed);
        savedInstanceState.putString("errorMessage", errorMessage);
        super.onSaveInstanceState(savedInstanceState);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            onRecipeClickedListener = (RecipeClickedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement RecipeClickedListener");
        }
    }


    private static RecipeClickedListener sRecipeCallbacks = new RecipeClickedListener() {
        @Override
        public void onWholeRecipeClicked(Recipe recipe) {

        }

        @Override
        public void onIngredientClicked(ArrayList<Ingredient> ingredients) {

        }
    };

    @Override
    public void onDestroyView(){
        VolleySingleton.getInstance(getActivity()).getRequestQueue().cancelAll(TAG);
        onRecipeClickedListener = null;
        super.onDestroyView();
        unbinder.unbind();
    }
}
