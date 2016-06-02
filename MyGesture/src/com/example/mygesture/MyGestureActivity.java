package com.example.mygesture;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MyGestureActivity extends Activity{

    private final static String TAG = "MyGestureActivity: ";
    
    private RelativeLayout left;
    private TextView tvLeftOperation;
    private RelativeLayout right;
    private TextView tvRightOperation;
    
    private GestureDetector mLeftGestureDetector;
    private GestureDetector mRightGestureDetector;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_gesture);
        
        mLeftGestureDetector = new GestureDetector(this, new LeftOnGestureListener());
        mRightGestureDetector = new GestureDetector(this, new RightOnGestureListener());
        
        left = (RelativeLayout)findViewById(R.id.left);
        tvLeftOperation = (TextView) findViewById(R.id.tv_left_operation);
        right = (RelativeLayout)findViewById(R.id.right);
        tvRightOperation = (TextView) findViewById(R.id.tv_right_operation);
        
        left.setOnTouchListener(new OnTouchListener() {
            
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d(getClass().getName(), "onTouch-----" + getActionName(event.getAction()));
                mLeftGestureDetector.onTouchEvent(event);
                // 一定要返回true，不然获取不到完整的事件
                return true;
            }
        });
        
        right.setOnTouchListener(new OnTouchListener() {
            
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d(getClass().getName(), "onTouch-----" + getActionName(event.getAction()));
                mRightGestureDetector.onTouchEvent(event);
                // 一定要返回true，不然获取不到完整的事件
                return true;
            }
        });
    }

    private String getActionName(int action) {
        String name = "";
        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                name = "ACTION_DOWN";
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                name = "ACTION_MOVE";
                break;
            }
            case MotionEvent.ACTION_UP: {
                name = "ACTION_UP";
                break;
            }
            default:
            break;
        }
        return name;
    }


    class LeftOnGestureListener extends SimpleOnGestureListener{
//        @Override
//        public boolean onDown(MotionEvent e) {
//            Log.d(TAG, "onDown-----" + getActionName(e.getAction()));
//            return false;
//        }
//        
//        @Override
//        public boolean onSingleTapUp(MotionEvent e) {
//            Log.d(TAG, "onSingleTapUp-----" + getActionName(e.getAction()));
//            return false;
//        }
        
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            Log.d(TAG, "onSingleTapConfirmed-----" + getActionName(e.getAction()));
            tvLeftOperation.setText("单击");
            return false;
        }
        
//        @Override
//        public boolean onDoubleTap(MotionEvent e) {
//            Log.d(TAG, "onDoubleTap-----" + getActionName(e.getAction()));
//            return false;
//        }
        
        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {
            Log.d(TAG, "onDoubleTapEvent-----" + getActionName(e.getAction()));
            if(e.getAction() == MotionEvent.ACTION_UP){
                tvLeftOperation.setText("双击");
            }
            return false;
        }
        
        @Override
        public void onLongPress(MotionEvent e) {
            Log.d(TAG, "onLongPress-----" + getActionName(e.getAction()));
            tvLeftOperation.setText("长按");
        }       
        
//        @Override
//        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
//                float velocityY) {
//            Log.d(TAG, "onFling-----" 
//                + getActionName(e2.getAction()) 
//                + ",(" + e1.getX() + "," + e1.getY() 
//                + ") ,(" + e2.getX() + "," + e2.getY() + ")"
//                + " velocityX = " + velocityX
//                + " velocityY = " + velocityY);
//            return false;
//        }
//        
//        @Override
//        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
//                float distanceY) {
//            Log.d(TAG, "onScroll-----" + getActionName(e2.getAction()) 
//                + ",(" + e1.getX() + "," + e1.getY() 
//                + ") ,(" + e2.getX() + "," + e2.getY() + ")"
//                + "distanceX = " + distanceX
//                + "distanceY = " + distanceY);
//            return false;
//        }
//        
//        @Override
//        public void onShowPress(MotionEvent e) {
//            Log.d(TAG, "onShowPress-----" + getActionName(e.getAction()));
//        }
    }
    
    class RightOnGestureListener extends SimpleOnGestureListener{
      
      @Override
      public boolean onSingleTapConfirmed(MotionEvent e) {
          Log.d(TAG, "onSingleTapConfirmed-----" + getActionName(e.getAction()));
          tvRightOperation.setText("单击");
          return false;
      }
      
      @Override
      public boolean onDoubleTapEvent(MotionEvent e) {
          Log.d(TAG, "onDoubleTapEvent-----" + getActionName(e.getAction()));
          if(e.getAction() == MotionEvent.ACTION_UP){
              tvRightOperation.setText("双击");
          }
          return false;
      }
      
      @Override
      public void onLongPress(MotionEvent e) {
          Log.d(TAG, "onLongPress-----" + getActionName(e.getAction()));
          tvRightOperation.setText("长按");
      }       
  }
    
}
