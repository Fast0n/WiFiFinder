package com.fast0n.wififinder.java;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;


public class RecyclerItemListener implements RecyclerView.OnItemTouchListener {

    private GestureDetector gd;

    public RecyclerItemListener(Context ctx, final RecyclerView recycler_view, final RecyclerTouchListener listener) {
        gd = new GestureDetector(ctx, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public void onLongPress(MotionEvent e) {
                View v = recycler_view.findChildViewUnder(e.getX(), e.getY());
                listener.onLongClickItem(v, recycler_view.getChildAdapterPosition(v));

            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                View v = recycler_view.findChildViewUnder(e.getX(), e.getY());
                listener.onClickItem(v, recycler_view.getChildAdapterPosition(v));
                return true;
            }
        });

    }

    @Override
    public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
        View child = rv.findChildViewUnder(e.getX(), e.getY());
        return (child != null && gd.onTouchEvent(e));
    }

    @Override
    public void onTouchEvent(@NonNull RecyclerView recycler_view, @NonNull MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }

    public interface RecyclerTouchListener {
        void onClickItem(View v, int position);

        void onLongClickItem(View v, int position);
    }
}
