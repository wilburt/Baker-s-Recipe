package com.jadebyte.bakersrecipe.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.jadebyte.bakersrecipe.R;
import com.jadebyte.bakersrecipe.activities.MainActivity;
import com.jadebyte.bakersrecipe.pojos.Recipe;
import com.jadebyte.bakersrecipe.utils.Constants;
import com.jadebyte.bakersrecipe.utils.VolleyCache;
import com.jadebyte.bakersrecipe.utils.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Random;

/**
 * Implementation of App Widget functionality.
 */
public class RecipeWidget extends AppWidgetProvider {
    private final String TAG = "RecipeWidget";

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId, Recipe recipe) {

        CharSequence widgetText = recipe.getName();
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.recipe_widget);
        Intent intent = new Intent(context, ListWidgetService.class);
        views.setRemoteAdapter(R.id.appwidget_list, intent);

        Intent configIntent = new Intent(context, MainActivity.class);
        PendingIntent configPendingIntent = PendingIntent.getActivity(context, 0, configIntent, 0);
        views.setOnClickPendingIntent(R.id.appwidget_text, configPendingIntent);

         views.setTextViewText(R.id.appwidget_text, widgetText);
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.appwidget_list);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        getRecipeArray(context, appWidgetManager, appWidgetIds);
    }

    private void getRecipeArray(final Context context, final AppWidgetManager appWidgetManager, final int[] appWidgetIds) {
        String recipeUrl = Constants.URLS.getRecipeUrl();
        JsonArrayRequest recipesRequest = new JsonArrayRequest(recipeUrl, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                // Successful request
                Log.d(TAG, "onResponse");
                parseResponse(response, context, appWidgetManager, appWidgetIds);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse");
            }
        }) {
            @Override
            protected Response<JSONArray> parseNetworkResponse(NetworkResponse response) {
                Response<JSONArray> resp = super.parseNetworkResponse(response);
                return Response.success(resp.result, VolleyCache.parseIgnoreCacheHeaders(response, 10800000L)); // cache for 3  hours
            }
        };


        RetryPolicy policy = new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        recipesRequest.setRetryPolicy(policy);
        recipesRequest.setShouldCache(true);
        recipesRequest.setTag(TAG);
        VolleySingleton.getInstance(context).addToRequestQueue(recipesRequest);

    }

    private void parseResponse(JSONArray array, Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.d(TAG, "parseResponse: ");
        Random random = new Random();
        try {
            int randIndex = random.nextInt(array.length());
            Recipe recipe = new Recipe(array.getJSONObject(randIndex));
            for (int appWidgetId : appWidgetIds) {
                updateAppWidget(context, appWidgetManager, appWidgetId, recipe);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

