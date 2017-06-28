package com.jadebyte.bakersrecipe.utils;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;

/**
 * Created by wilbur on 6/27/17 at 7:23 PM.
 */

public class Utils {
    /**
     * This method converts dp unit to equivalent pixels, depending on device density.
     *
     * @param dp A value in dp (density independent pixels) unit. Which we need to convert into pixels
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent px equivalent to dp depending on device density
     */
    public static float dpToPx(float dp, Context context) {
        WeakReference<Context> contextRef = new WeakReference<>(context);
        Resources resources = contextRef.get().getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    public static String readFromAssets(Context context, String filename){
        WeakReference<Context> weakCxt = new WeakReference<>(context);
        BufferedReader reader;
        try {
            reader = new BufferedReader(new InputStreamReader(weakCxt.get().getAssets().open(filename)));

            StringBuilder sb = new StringBuilder();
            String mLine = reader.readLine();
            while (mLine != null) {
                sb.append(mLine); // process line
                mLine = reader.readLine();
            }
            reader.close();
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
