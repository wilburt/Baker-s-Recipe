package com.jadebyte.bakersrecipe.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.jadebyte.bakersrecipe.R;
import com.jadebyte.bakersrecipe.pojos.Ingredient;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by wilbur on 6/18/17 at 1:00 PM.
 */

public class IngredientViewHolder extends RecyclerView.ViewHolder{

    @BindView(R.id.ingredient_name) TextView tvName;
    @BindView(R.id.ingredient_quantity) TextView tvQuantity;

    public IngredientViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void bindModel(Ingredient ingredient) {
        tvName.setText(ingredient.getName());
        tvQuantity.setText(ingredient.getQuantity() + " " + ingredient.getMeasure());
    }
}
