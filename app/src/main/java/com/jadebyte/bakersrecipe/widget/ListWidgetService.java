package com.jadebyte.bakersrecipe.widget;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.jadebyte.bakersrecipe.R;
import com.jadebyte.bakersrecipe.utils.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ListWidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ListRemoteViewFactory(this.getApplicationContext());
    }

    private class ListRemoteViewFactory implements RemoteViewsService.RemoteViewsFactory {
        private Context context;
        private List<String> ingredients = new ArrayList<>();

        ListRemoteViewFactory(Context context) {
            this.context = context;
        }

        @Override
        public void onCreate() {
        }

        @Override
        public void onDataSetChanged() {
            SharedPreferences preferences = context.getSharedPreferences(Constants.Keys.SAVED_INGREDIENTS, MODE_PRIVATE);
            Set<String> set = preferences.getStringSet(Constants.Keys.SAVED_INGREDIENTS_SET, null);
            if (set != null) {
                for (String string: set) {
                    ingredients.add(string);
                }
            }
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
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_ingredient_item);
            views.setTextViewText(R.id.ingredient_name, ingredients.get(position));
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
    }
}
