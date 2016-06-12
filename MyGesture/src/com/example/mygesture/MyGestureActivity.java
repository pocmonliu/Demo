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
    
    private RelativeLayout leftfornt;
    private TextView tvLeftFrontOperation;
    private RelativeLayout rightfront;
    private TextView tvRightFrontOperation;
    private RelativeLayout leftrear;
    private TextView tvLeftRearOperation;
    private RelativeLayout rightrear;
    private TextView tvRightRearOperation;
    
    private GestureDetector mLeftFrontGestureDetector;
    private GestureDetector mRightFrontGestureDetector;
    private GestureDetector mLeftRearGestureDetector;
    private GestureDetector mRightRearGestureDetector;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_gesture);
        
        mLeftFrontGestureDetector = new GestureDetector(this, new LeftFrontOnGestureListener());
        mRightFrontGestureDetector = new GestureDetector(this, new RightFrontOnGestureListener());
        mLeftRearGestureDetector = new GestureDetector(this, new LeftRearOnGestureListener());
        mRightRearGestureDetector = new GestureDetector(this, new RightRearOnGestureListener());
        
        leftfornt = (RelativeLayout)findViewById(R.id.front_left);
        tvLeftFrontOperation = (TextView) findViewById(R.id.tv_front_left_operation);
        rightfront = (RelativeLayout)findViewById(R.id.front_right);
        tvRightFrontOperation = (TextView) findViewById(R.id.tv_front_right_operation);
        leftrear = (RelativeLayout)findViewById(R.id.rear_left);
        tvLeftRearOperation = (TextView) findViewById(R.id.tv_rear_left_operation);
        rightrear = (RelativeLayout)findViewById(R.id.rear_right);
        tvRightRearOperation = (TextView) findViewById(R.id.tv_rear_right_operation);
        
        
        leftfornt.setOnTouchListener(new OnTouchListener() {
            
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d(getClass().getName(), "onTouch-----" + getActionName(event.getAction()));
                mLeftFrontGestureDetector.onTouchEvent(event);
                // 一定要返回true，不然获取不到完整的事件
                return true;
            }
        });
        
        rightfront.setOnTouchListener(new OnTouchListener() {
            
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d(getClass().getName(), "onTouch-----" + getActionName(event.getAction()));
                mRightFrontGestureDetector.onTouchEvent(event);
                // 一定要返回true，不然获取不到完整的事件
                return true;
            }
        });
        
        leftrear.setOnTouchListener(new OnTouchListener() {
            
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d(getClass().getName(), "onTouch-----" + getActionName(event.getAction()));
                mLeftRearGestureDetector.onTouchEvent(event);
                // 一定要返回true，不然获取不到完整的事件
                return true;
            }
        });
        
        rightrear.setOnTouchListener(new OnTouchListener() {
            
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d(getClass().getName(), "onTouch-----" + getActionName(event.getAction()));
                mRightRearGestureDetector.onTouchEvent(event);
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


    class LeftFrontOnGestureListener extends SimpleOnGestureListener{
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
            tvLeftFrontOperation.setText("单击");
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
                tvLeftFrontOperation.setText("双击");
            }
            return false;
        }
        
        @Override
        public void onLongPress(MotionEvent e) {
            Log.d(TAG, "onLongPress-----" + getActionName(e.getAction()));
            tvLeftFrontOperation.setText("长按");
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
    
    class RightFrontOnGestureListener extends SimpleOnGestureListener{
      
      @Override
      public boolean onSingleTapConfirmed(MotionEvent e) {
          Log.d(TAG, "onSingleTapConfirmed-----" + getActionName(e.getAction()));
          tvRightFrontOperation.setText("单击");
          return false;
      }
      
      @Override
      public boolean onDoubleTapEvent(MotionEvent e) {
          Log.d(TAG, "onDoubleTapEvent-----" + getActionName(e.getAction()));
          if(e.getAction() == MotionEvent.ACTION_UP){
              tvRightFrontOperation.setText("双击");
          }
          return false;
      }
      
      @Override
      public void onLongPress(MotionEvent e) {
          Log.d(TAG, "onLongPress-----" + getActionName(e.getAction()));
          tvRightFrontOperation.setText("长按");
      }       
  }
    
    class LeftRearOnGestureListener extends SimpleOnGestureListener{
        
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            Log.d(TAG, "onSingleTapConfirmed-----" + getActionName(e.getAction()));
            tvLeftRearOperation.setText("单击");
            return false;
        }
        
        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {
            Log.d(TAG, "onDoubleTapEvent-----" + getActionName(e.getAction()));
            if(e.getAction() == MotionEvent.ACTION_UP){
                tvLeftRearOperation.setText("双击");
            }
            return false;
        }
        
        @Override
        public void onLongPress(MotionEvent e) {
            Log.d(TAG, "onLongPress-----" + getActionName(e.getAction()));
            tvLeftRearOperation.setText("长按");
        }       
    }
    
    class RightRearOnGestureListener extends SimpleOnGestureListener{
        
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            Log.d(TAG, "onSingleTapConfirmed-----" + getActionName(e.getAction()));
            tvRightRearOperation.setText("单击");
            return false;
        }
        
        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {
            Log.d(TAG, "onDoubleTapEvent-----" + getActionName(e.getAction()));
            if(e.getAction() == MotionEvent.ACTION_UP){
                tvRightRearOperation.setText("双击");
            }
            return false;
        }
        
        @Override
        public void onLongPress(MotionEvent e) {
            Log.d(TAG, "onLongPress-----" + getActionName(e.getAction()));
            tvRightRearOperation.setText("长按");
        }       
    }
}
