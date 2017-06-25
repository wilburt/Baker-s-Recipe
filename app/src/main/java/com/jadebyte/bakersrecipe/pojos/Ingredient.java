package com.jadebyte.bakersrecipe.pojos;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by wilbur on 6/17/17 at 10:35 PM.
 */

public class Ingredient implements Parcelable {
    private String name;
    private String measure;
    private int quantity;

    public Ingredient(JSONObject jsonObject) throws JSONException {
        this.name = jsonObject.getString("ingredient");
        this.measure = jsonObject.getString("measure");
        this.quantity = jsonObject.getInt("quantity");
    }

    public String getName() {
        return name;
    }

    public String getMeasure() {
        return measure;
    }

    public int getQuantity() {
        return quantity;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.measure);
        dest.writeInt(this.quantity);
    }

    protected Ingredient(Parcel in) {
        this.name = in.readString();
        this.measure = in.readString();
        this.quantity = in.readInt();
    }

    public static final Parcelable.Creator<Ingredient> CREATOR = new Parcelable.Creator<Ingredient>() {
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
