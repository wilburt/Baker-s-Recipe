package com.jadebyte.bakersrecipe.pojos;


import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Recipe implements Parcelable {
    private String title;
    private int id;
    private String name;
    private ArrayList<Ingredient> ingredients = new ArrayList<>();
    private ArrayList<Step> steps = new ArrayList<>();
    private int servings;
    private String image;

    public Recipe(JSONObject jsonObject) throws JSONException {

        JSONArray ingredientArray = jsonObject.getJSONArray("ingredients");
        JSONArray stepArray = jsonObject.getJSONArray("steps");


        for (int i = 0; i < ingredientArray.length(); i++) {
            ingredients.add(new Ingredient(ingredientArray.getJSONObject(i)));
        }

        for (int s = 0; s < stepArray.length(); s++) {
            steps.add(new Step(stepArray.getJSONObject(s)));
        }
        this.id = jsonObject.getInt("id");
        this.name = jsonObject.getString("name");
        this.servings = jsonObject.getInt("servings");
        this.image = jsonObject.getString("image");

        Log.d("Recipe", "Recipe: " + name);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public ArrayList<Ingredient> getIngredients() {
        return ingredients;
    }

    public ArrayList<Step> getSteps() {
        return steps;
    }

    public int getServings() {
        return servings;
    }

    public String getImage() {
        return image;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.name);
        dest.writeTypedList(this.ingredients);
        dest.writeTypedList(this.steps);
        dest.writeInt(this.servings);
        dest.writeString(this.image);
    }

    protected Recipe (Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();
        this.ingredients = in.createTypedArrayList(Ingredient.CREATOR);
        this.steps = in.createTypedArrayList(Step.CREATOR);
        this.servings = in.readInt();
        this.image = in.readString();
    }

    public static final Creator<Ingredient> CREATOR = new Creator<Ingredient>() {
        @Override
        public Ingredient createFromParcel(Parcel source) {
            return new Ingredient(source);
        }

        @Override
        public Ingredient[] newArray(int size) {
            return new Ingredient[size];
        }
    };
}
