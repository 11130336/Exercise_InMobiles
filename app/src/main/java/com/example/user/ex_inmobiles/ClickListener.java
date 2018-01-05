package com.example.user.ex_inmobiles;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by user on 1/4/2018.
 */

public class ClickListener extends RecyclerView.ViewHolder implements View.OnClickListener {

    private RecyclerViewClickListener mListener;

    ClickListener(View v, RecyclerViewClickListener listener) {
        super(v);
        mListener = listener;
        v.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        mListener.onClick(view, getAdapterPosition());
    }
}