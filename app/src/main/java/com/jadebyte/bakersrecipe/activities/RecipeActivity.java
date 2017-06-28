package com.jadebyte.bakersrecipe.activities;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.test.espresso.IdlingResource;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import com.jadebyte.bakersrecipe.fragments.DialogIngredients;
import com.jadebyte.bakersrecipe.listeners.RecipeClickedListener;
import com.jadebyte.bakersrecipe.pojos.Ingredient;
import com.jadebyte.bakersrecipe.pojos.Recipe;
import com.jadebyte.bakersrecipe.utils.AutoFitGridLayoutManager;
import com.jadebyte.bakersrecipe.utils.Constants;
import com.jadebyte.bakersrecipe.utils.MyVolleyError;
import com.jadebyte.bakersrecipe.utils.SimpleIdlingResource;
import com.jadebyte.bakersrecipe.utils.Utils;
import com.jadebyte.bakersrecipe.utils.VolleyCache;
import com.jadebyte.bakersrecipe.utils.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipeActivity extends AppCompatActivity implements RecipeClickedListener {

    // Constants
    private final String KEY_RECIPE_ITEMS = "recipeItems";
    private final String TAG = "RecipeActivity";

    //Views
    @BindView(R.id.recipe_toolbar) Toolbar toolbar;
    @BindView(R.id.recipe_recycler) RecyclerView recyclerView;
    @BindView(R.id.recipe_pro_bar) ProgressBar progressBar;
    @BindView(R.id.recipe_info_error_root) LinearLayout errorLayout;
    @BindView(R.id.recipe_info_error_text) TextView errorText;
    @BindView(R.id.recipe_info_error_button) Button retryButton;

    //Fields
    private String recipeUrl = Constants.URLS.getRecipeUrl();
    private List<Recipe> recipeList;
    private RecipeAdapter adapter;
    private boolean hasFailed = false;
    private String errorMessage;
    @Nullable private SimpleIdlingResource idlingResource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        if (savedInstanceState != null) {
            hasFailed = savedInstanceState.getBoolean("hasFailed");
            errorMessage = savedInstanceState.getString("errorMessage");
        }

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

    }



    private void setUpWidgets() {
        LinearLayoutManager layoutManager;
        int recipeItemLayoutId;
        if (getResources().getBoolean(R.bool.is_single_pane)) {
            layoutManager = new LinearLayoutManager(this);
            recipeItemLayoutId = R.layout.recipe_item_phone;

        } else {
            layoutManager = new AutoFitGridLayoutManager(this, (int) Utils.dpToPx(350, this));
            recipeItemLayoutId = R.layout.recipe_item_tablet;
        }
        recyclerView.setLayoutManager(layoutManager);
        adapter = new RecipeAdapter(recipeList, recipeItemLayoutId);
        recyclerView.setAdapter(adapter);
        progressBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(this,
                R.color.colorAccent), PorterDuff.Mode.SRC_IN);
    }

    private void widgetListeners() {
        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errorLayout.setVisibility(View.GONE);
                getRecipeArray();
            }
        });

        adapter.setOnRecipeClickedListener(this);
    }

    // Get recipes from API
    private void getRecipeArray(){
        progressBar.setVisibility(View.VISIBLE);
        if (idlingResource != null) {
            idlingResource.setIdleState(false);
        }
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
                if (idlingResource != null) {
                    idlingResource.setIdleState(true);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse");
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
                if (adapter.getItemCount() == 1) {
                    errorMessage = MyVolleyError.errorMessage(error, RecipeActivity.this);
                    errorText.setText(errorMessage);
                    errorLayout.setVisibility(View.VISIBLE);
                    hasFailed = true;
                }

                if (idlingResource != null) {
                    idlingResource.setIdleState(true);
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
        VolleySingleton.getInstance(this).addToRequestQueue(recipesRequest);

    }

    private void parseResponse(JSONArray array) {
        Log.d(TAG, "parseResponse: ");
        try {

            for(int a = 0; a<array.length(); a++) {
                recipeList.add(new Recipe(array.getJSONObject(a)));
            }

            adapter.notifyItemRangeChanged(0, adapter.getItemCount());
            saveIngredients();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void saveIngredients() {
        Random random = new Random();
        Recipe recipe = recipeList.get(random.nextInt(recipeList.size()));
        ArrayList<Ingredient> ingredients = recipe.getIngredients();
        Set<String> set = new HashSet<>();
        for (Ingredient ingredient: ingredients) {
            set.add(ingredient.getName());
        }

        SharedPreferences preferences = getSharedPreferences(Constants.Keys.SAVED_INGREDIENTS, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putStringSet(Constants.Keys.SAVED_INGREDIENTS_SET, set);
        editor.putString(Constants.Keys.SAVED_INGREDIENTS_TITLE, recipe.getName());
        editor.apply();
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
    protected void onDestroy() {
        super.onDestroy();
        VolleySingleton.getInstance(this).getRequestQueue().cancelAll(TAG);
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
        }
        return super.onOptionsItemSelected(item);
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
            textView.setText(Html.fromHtml(Utils.readFromAssets(this, "license.html"), Html
                    .FROM_HTML_MODE_COMPACT));
        } else {
            textView.setText(Html.fromHtml(Utils.readFromAssets(this, "license.html")));
        }
        builder.show();
    }


    @Override
    public void onWholeRecipeClicked(Recipe recipe) {
        Intent intent = new Intent(this, StepActivity.class);
        intent.putExtra(Constants.Keys.RECIPE_OBJECT, recipe);
        startActivity(intent);
    }

    @Override
    public void onIngredientClicked(ArrayList<Ingredient> ingredients) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(DialogIngredients.newInstance(ingredients), "DialogIngredients");
        transaction.commitAllowingStateLoss();
    }

    @VisibleForTesting
    @NonNull
    public IdlingResource getIdlingResource() {

        if (idlingResource == null) {
            idlingResource = new SimpleIdlingResource();
        }
        return idlingResource;
    }


    @VisibleForTesting
    @NonNull
    public List<Recipe> getRecipeList() {
        return recipeList;
    }
}
