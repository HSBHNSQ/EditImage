package com.liubowang.photoretouch.Template;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

/**
 * Created by heshaobo on 2017/12/6.
 */

public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

    private static final String TAG = GridSpacingItemDecoration.class.getSimpleName();
    private int spacing; //间隔

    public GridSpacingItemDecoration(int spacing) {
        this.spacing = spacing;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.set(spacing/2,spacing/2,spacing/2,spacing/2);
    }


}
