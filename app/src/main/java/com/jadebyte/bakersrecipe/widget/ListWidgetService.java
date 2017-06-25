package com.jadebyte.bakersrecipe.widget;

import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.jadebyte.bakersrecipe.R;
import com.jadebyte.bakersrecipe.pojos.Ingredient;
import com.jadebyte.bakersrecipe.pojos.Recipe;
import com.jadebyte.bakersrecipe.utils.Constants;
import com.jadebyte.bakersrecipe.utils.VolleyCache;
import com.jadebyte.bakersrecipe.utils.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;
import java.util.Random;

public class ListWidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ListRemoteViewFactory(this.getApplicationContext());
    }

    class ListRemoteViewFactory implements RemoteViewsService.RemoteViewsFactory {
        Context context;
        private List<Ingredient> ingredients;
        public ListRemoteViewFactory(Context context) {
            this.context = context;
        }

        @Override
        public void onCreate() {
        }

        @Override
        public void onDataSetChanged() {
            getRecipeArray();
        }

        @Override
        public void onDestroy() {

        }

        @Override
        public int getCount() {
            return ingredients == null ? (0) : ingredients.size();
        }

        @Override
        public RemoteViews getViewAt(int position) {
            if (ingredients.size() == 0) return  null;
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.ingredient_item);
            final Ingredient ingredient = ingredients.get(position);
            views.setTextViewText(R.id.ingredient_name, ingredient.getName());
            views.setTextViewText(R.id.ingredient_quantity, ingredient.getQuantity() + " " + ingredient.getMeasure());
            return views;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        private void getRecipeArray(){
            String recipeUrl = Constants.URLS.getRecipeUrl();
            JsonArrayRequest recipesRequest = new JsonArrayRequest(recipeUrl, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    // Successful request
                    parseResponse(response);

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
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
            VolleySingleton.getInstance(context).addToRequestQueue(recipesRequest);

        }

        private void parseResponse(JSONArray array) {
            Random random = new Random();
            try {
                int randIndex = random.nextInt(array.length());
                Recipe recipe = new Recipe(array.getJSONObject(randIndex));
                ingredients = recipe.getIngredients();


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
