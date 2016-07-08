package com.example.mygesture;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

public class DoubleClickLayout extends RelativeLayout{

    private final String TAG = "Tpms: DoubleClickImageView"; 
    
    IDoubleClickListener mDoubleClickListener;  
    private long mLastClickTime;  
  
    public DoubleClickLayout(Context context, AttributeSet attrs, int defStyle) {  
        super(context, attrs, defStyle);  
        init();  
    }  
  
    public DoubleClickLayout(Context context, AttributeSet attrs) {  
        super(context, attrs);  
        init();  
    }  
  
    public DoubleClickLayout(Context context) {  
        super(context);  
        init();  
    }  
  
    private void init() {  
        mLastClickTime = System.currentTimeMillis();  
        Log.d(TAG, "mLastClickTime=" + mLastClickTime);  
    }  
    
    public void setOnDoubleClickListener(final IDoubleClickListener doubleClickListener) {  
        this.mDoubleClickListener = doubleClickListener;  
    }  
  
    @Override  
    public boolean onTouchEvent(MotionEvent event) {  
        if (event.getAction() == MotionEvent.ACTION_DOWN) {  
            long newClickTime = System.currentTimeMillis();  
            Log.d(TAG, "newClickTime=" + newClickTime);  
            if (newClickTime - mLastClickTime < 500) {
                //调用双击事件  
                mDoubleClickListener.onDoubleClick();  
  
            }  
            mLastClickTime = newClickTime;  
        }  
        return super.onTouchEvent(event);  
    }

}
