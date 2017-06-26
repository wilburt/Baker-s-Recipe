package com.jadebyte.bakersrecipe.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jadebyte.bakersrecipe.R;

import java.util.List;

/**
 * Created by wilbur on 6/26/17 at 10:21 AM.
 */

public class TestAdapter extends RecyclerView.Adapter<TestAdapter.TestHolder> {

    private final List<String> stringList;

    private final Context mContext;

    public static class TestHolder extends RecyclerView.ViewHolder {
        private final TextView textView;

        private boolean isTheMiddleItem = false;

        TestHolder(View v) {
            super(v);
            textView = (TextView) v.findViewById(R.id.test_textView);
        }

        TextView getTextView() {
            return textView;
        }

        public boolean getIsInTheMiddle() {
            return isTheMiddleItem;
        }

        void setIsInTheMiddle(boolean isInTheMiddle) {
            isTheMiddleItem = isInTheMiddle;
        }
    }
    
    public TestAdapter(List<String> dataSet, Context context) {
        stringList = dataSet;
        mContext = context;
    }

    @Override
    public TestHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view.
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.test_item, viewGroup, false);

        return new TestHolder(v);
    }

    @Override
    public void onBindViewHolder(TestHolder testHolder, final int position) {
        if (position == stringList.size() / 2) {
            testHolder.setIsInTheMiddle(true);
            testHolder.getTextView().setText(mContext.getResources().getString(R.string.middle_item));
        } else {
            testHolder.setIsInTheMiddle(false);
            testHolder.getTextView().setText(stringList.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return stringList.size();
    }
}