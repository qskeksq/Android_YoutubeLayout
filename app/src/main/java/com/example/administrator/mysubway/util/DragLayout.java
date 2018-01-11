package com.example.administrator.mysubway.util;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Created by Administrator on 2017-10-25.
 */

public class DragLayout extends LinearLayout {

    ViewDragHelper dragHelper;
    View mDragView;

    public DragLayout(Context context) {
        this(context, null);
    }

    public DragLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DragLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

}
