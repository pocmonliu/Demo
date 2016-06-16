package com.example.mygesture;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class MyGestureActivity extends Activity{

    private final static String TAG = "MyGestureActivity: ";
    
    private RelativeLayout layoutFourTires;
    private ImageView ivCar;
    
    int mScreenWidth;
    int mScreenHeight;
    
    private TireLayout leftfront;
    private TireLayout rightfront;
    private TireLayout leftrear;
    private TireLayout rightrear;
    
    private GestureDetector mLeftFrontGestureDetector;
    private GestureDetector mRightFrontGestureDetector;
    private GestureDetector mLeftRearGestureDetector;
    private GestureDetector mRightRearGestureDetector;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_gesture);
        
        layoutFourTires = (RelativeLayout)findViewById(R.id.four_tires_layout);
        ivCar = (ImageView)findViewById(R.id.iv_car);
        
        mLeftFrontGestureDetector = new GestureDetector(this, new LeftFrontOnGestureListener());
        mRightFrontGestureDetector = new GestureDetector(this, new RightFrontOnGestureListener());
        mLeftRearGestureDetector = new GestureDetector(this, new LeftRearOnGestureListener());
        mRightRearGestureDetector = new GestureDetector(this, new RightRearOnGestureListener());
        
        ivCar.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                SwapTires(leftrear, rightfront);
            }
        });
        
        layoutFourTires.post(addTireLayout);
    }

    /**
     * 动态添加轮胎布局
     */
    private Runnable addTireLayout = new Runnable() {
        
        @Override
        public void run() {
            mScreenWidth = layoutFourTires.getWidth();
            mScreenHeight = layoutFourTires.getHeight();
            
            leftfront = new TireLayout(MyGestureActivity.this);
            leftfront.setBackgroundColor(Color.RED);
            leftfront.setPosition("0左前胎");
            leftfront.setOnTouchListener(new OnTouchListener() {
                
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    Log.d(TAG, "onTouch-----" + getActionName(event.getAction()));
                    mLeftFrontGestureDetector.onTouchEvent(event);
                    // 一定要返回true，不然获取不到完整的事件
                    return true;
                }
            });
            
   
            rightfront = new TireLayout(MyGestureActivity.this);
            rightfront.setBackgroundColor(Color.YELLOW);
            rightfront.setPosition("1右前胎");
            rightfront.setOnTouchListener(new OnTouchListener() {
                
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    Log.d(TAG, "onTouch-----" + getActionName(event.getAction()));
                    mRightFrontGestureDetector.onTouchEvent(event);
                    // 一定要返回true，不然获取不到完整的事件
                    return true;
                }
            });
            
            leftrear = new TireLayout(MyGestureActivity.this);
            leftrear.setBackgroundColor(Color.BLUE);
            leftrear.setPosition("2左后胎");
            leftrear.setOnTouchListener(new OnTouchListener() {
                
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    Log.d(TAG, "onTouch-----" + getActionName(event.getAction()));
                    mLeftRearGestureDetector.onTouchEvent(event);
                    // 一定要返回true，不然获取不到完整的事件
                    return true;
                }
            });
            
            rightrear = new TireLayout(MyGestureActivity.this);
            rightrear.setBackgroundColor(Color.GREEN);
            rightrear.setPosition("3右后胎");
            rightrear.setOnTouchListener(new OnTouchListener() {
                
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    Log.d(TAG, "onTouch-----" + getActionName(event.getAction()));
                    mRightRearGestureDetector.onTouchEvent(event);
                    // 一定要返回true，不然获取不到完整的事件
                    return true;
                }
            });
            
            layoutFourTires.addView(leftfront, 0, new ViewGroup.LayoutParams(mScreenWidth / 2, mScreenHeight / 2));
            layoutFourTires.addView(rightfront, 1, new ViewGroup.LayoutParams(mScreenWidth / 2, mScreenHeight / 2));
            layoutFourTires.addView(leftrear, 2, new ViewGroup.LayoutParams(mScreenWidth / 2, mScreenHeight / 2));
            layoutFourTires.addView(rightrear, 3, new ViewGroup.LayoutParams(mScreenWidth / 2, mScreenHeight / 2));
            
            setLayout(leftfront, 0, 0);
            setLayout(rightfront, mScreenWidth / 2, 0);
            setLayout(leftrear, 0, mScreenHeight / 2);
            setLayout(rightrear, mScreenWidth / 2, mScreenHeight / 2);
        }
    };
    
    /* 
     * 设置控件所在的位置YY，并且不改变宽高， 
     * XY为绝对位置 
     */ 
    private void setLayout(View view, int x,int y) { 
        MarginLayoutParams margin = new MarginLayoutParams(view.getLayoutParams()); 
        margin.setMargins(x, y, x + margin.width, y + margin.height); 
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(margin); 
        view.setLayoutParams(layoutParams); 
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
            leftfront.setOperation("单击");
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
                leftfront.setOperation("双击");
            }
            return false;
        }
        
        @Override
        public void onLongPress(MotionEvent e) {
            Log.d(TAG, "onLongPress-----" + getActionName(e.getAction()));
            leftfront.setOperation("长按");
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
          rightfront.setOperation("单击");
          return false;
      }
      
      @Override
      public boolean onDoubleTapEvent(MotionEvent e) {
          Log.d(TAG, "onDoubleTapEvent-----" + getActionName(e.getAction()));
          if(e.getAction() == MotionEvent.ACTION_UP){
              rightfront.setOperation("双击");
          }
          return false;
      }
      
      @Override
      public void onLongPress(MotionEvent e) {
          Log.d(TAG, "onLongPress-----" + getActionName(e.getAction()));
          rightfront.setOperation("长按");
      }       
  }
    
    class LeftRearOnGestureListener extends SimpleOnGestureListener{
        
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            Log.d(TAG, "onSingleTapConfirmed-----" + getActionName(e.getAction()));
            leftrear.setOperation("单击");
            return false;
        }
        
        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {
            Log.d(TAG, "onDoubleTapEvent-----" + getActionName(e.getAction()));
            if(e.getAction() == MotionEvent.ACTION_UP){
                leftrear.setOperation("双击");
            }
            return false;
        }
        
        @Override
        public void onLongPress(MotionEvent e) {
            Log.d(TAG, "onLongPress-----" + getActionName(e.getAction()));
            leftrear.setOperation("长按");
        }       
    }
    
    class RightRearOnGestureListener extends SimpleOnGestureListener{
        
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            Log.d(TAG, "onSingleTapConfirmed-----" + getActionName(e.getAction()));
            rightrear.setOperation("单击");
            return false;
        }
        
        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {
            Log.d(TAG, "onDoubleTapEvent-----" + getActionName(e.getAction()));
            if(e.getAction() == MotionEvent.ACTION_UP){
                rightrear.setOperation("双击");
            }
            return false;
        }
        
        @Override
        public void onLongPress(MotionEvent e) {
            Log.d(TAG, "onLongPress-----" + getActionName(e.getAction()));
            rightrear.setOperation("长按");
        }       
    }

    AnimationListener animationListener = new AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            Log.d(TAG, "动画结束");
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    };

    private synchronized void SwapTires(final RelativeLayout view1, final RelativeLayout view2) {

        final int left1 = view1.getLeft();
        final int top1 = view1.getTop();
        final int right1 = view1.getRight();
        final int bottom1 = view1.getBottom();
        Log.d(TAG, "left1 = " + left1 + ", top1 = " + top1 + ", right1 = " + right1 + ", bottom1 = " + bottom1);

        final int left2 = view2.getLeft();
        final int top2 = view2.getTop();
        final int right2 = view2.getRight();
        final int bottom2 = view2.getBottom();
        Log.d(TAG, "left2 = " + left2 + ", top2 = " + top2 + ", right2 = " + right2 + ", bottom2 = " + bottom2);

        TranslateAnimation taView1= new TranslateAnimation(0, left2 - left1, 0, top2 - top1);
        taView1.setDuration(500);
        taView1.setInterpolator(new LinearInterpolator());
        taView1.setAnimationListener(new AnimationListener() {
            
            @Override
            public void onAnimationStart(Animation animation) {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public void onAnimationRepeat(Animation animation) {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public void onAnimationEnd(Animation animation) {
                view1.clearAnimation();
                setLayout(view1, left2, top2);
            }
        });
        
        TranslateAnimation taView2= new TranslateAnimation(0, left1 - left2, 0, top1 - top2);
        taView2.setDuration(500);
        taView2.setInterpolator(new LinearInterpolator());
        taView2.setAnimationListener(new AnimationListener() {
            
            @Override
            public void onAnimationStart(Animation animation) {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public void onAnimationRepeat(Animation animation) {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public void onAnimationEnd(Animation animation) {
                view2.clearAnimation();
                setLayout(view2, left1, top1);
            }
        });
        
        view1.startAnimation(taView1);
        view2.startAnimation(taView2);
        
    }
}
