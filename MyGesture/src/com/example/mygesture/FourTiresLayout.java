package com.example.mygesture;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

public class FourTiresLayout extends LinearLayout{
    private String TAG = "MyGestureActivity: ";

    public FourTiresLayout(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    public FourTiresLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    public FourTiresLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // TODO Auto-generated constructor stub
    }
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Log.d(TAG, "TireLayout onMeasure()");
    }
    
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        
        final int childCount = getChildCount();
        Log.d(TAG, "onLayout(), childCount = " + childCount);
            
        View childView0 = getChildAt(0);
        int l0 = 0;
        int t0 = 0;
        int r0 = right / 2;
        int b0 = bottom / 2;
        Log.d(TAG, "l0=" + l0 + ", t0=" + t0 + ", r0=" + r0 + ", b0=" + b0);
        childView0.layout(l0, t0, r0, b0);
        
        View childView1 = getChildAt(1);
        int l1 = right / 2;
        int t1 = 0;
        int r1 = right;
        int b1 = bottom / 2;
        Log.d(TAG, "l1=" + l1 + ", t1=" + t1 + ", r1=" + r1 + ", b1=" + b1);
        childView1.layout(l1, t1, r1, b1);
        
        View childView2 = getChildAt(2);
        int l2 = 0;
        int t2 = bottom / 2;
        int r2 = right / 2;
        int b2 = bottom;
        Log.d(TAG, "l2=" + l2 + ", t2=" + t2 + ", r2=" + r2 + ", b2=" + b2);
        childView2.layout(l2, t2, r2, b2);
        
        View childView3 = getChildAt(3);
        int l3 = right / 2;
        int t3 = bottom / 2;
        int r3 = right;
        int b3 = bottom;
        Log.d(TAG, "l3=" + l3 + ", t3=" + t3 + ", r3=" + r3 + ", b3=" + b3);
        childView3.layout(l3, t3, r3, b3);
    }
}
