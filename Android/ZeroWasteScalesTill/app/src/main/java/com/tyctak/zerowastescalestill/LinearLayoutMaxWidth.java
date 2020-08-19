package com.tyctak.zerowastescalestill;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;

public class LinearLayoutMaxWidth extends LinearLayout {

    public LinearLayoutMaxWidth(Context context) {
        super(context);
    }

    public LinearLayoutMaxWidth(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        FrameLayoutMaxWidth parent = (FrameLayoutMaxWidth) this.getParent();
        Integer width = parent.getItemWidth();

        Integer px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, width, getResources().getDisplayMetrics());

        int widthMaxSpec = View.MeasureSpec.makeMeasureSpec(px, MeasureSpec.EXACTLY);
        super.onMeasure(widthMaxSpec, heightMeasureSpec);
    }
}