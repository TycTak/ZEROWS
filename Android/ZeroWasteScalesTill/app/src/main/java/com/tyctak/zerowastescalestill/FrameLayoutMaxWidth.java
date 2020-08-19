package com.tyctak.zerowastescalestill;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

public class FrameLayoutMaxWidth extends FrameLayout {

    public FrameLayoutMaxWidth(Context context) {
        super(context);
    }
    public FrameLayoutMaxWidth(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private Integer mWidth = 30;

    public void setMaxWidth(Integer width) {
        mWidth = width;
    }

    public Integer getItemWidth() {
        return mWidth;
    }
}
