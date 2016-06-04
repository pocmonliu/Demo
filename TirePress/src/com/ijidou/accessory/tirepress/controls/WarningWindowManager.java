package com.ijidou.accessory.tirepress.controls;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.ijidou.accessory.tirepress.R;
import com.ijidou.accessory.tirepressure.TpmsService;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.CarAcState;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class WarningWindowManager {
    private static final String TAG = "Tpms:WarningWindowManager: ";
    
    private Context mContext;
    
    public enum WaningType {
        QUICK_LEAK,
        SLOW_LEAK,
        OVER_PRESSURE,
        LESS_PRESSURE,
        OVER_HEATING
    }
    /**
     * 用于控制在屏幕上添加或移除悬浮窗
     */
    private WindowManager mWManager;
    
    private Map<Byte, WarningFloatWindow> warningmWindowsMap;
    
    /**
     * 如果WindowManager还未创建，则创建一个新的WindowManager返回。否则返回当前已创建的WindowManager。
     * 
     * @param context 必须为应用程序的Context.
     * @return WindowManager的实例，用于控制在屏幕上添加或移除悬浮窗。
     */
    public WarningWindowManager(Context context){
        mContext = context;
        warningmWindowsMap = new HashMap<Byte, WarningFloatWindow>();
        mWManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    }
    
    private LayoutParams getTypeLayoutParams() {
        DisplayMetrics dm = mContext.getResources().getDisplayMetrics();  
        int screenWidth = dm.widthPixels;  
        int screenHeight = dm.heightPixels;
        LayoutParams floatWindowParams = new WindowManager.LayoutParams();
        floatWindowParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        floatWindowParams.format = PixelFormat.RGBA_8888;
        floatWindowParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL 
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        floatWindowParams.gravity = Gravity.LEFT | Gravity.TOP;
        floatWindowParams.width = screenWidth;
        floatWindowParams.height = screenHeight;
        floatWindowParams.windowAnimations = R.style.FloatWindowAnimation;

        floatWindowParams.x = 0;
        floatWindowParams.y = 0;

        return floatWindowParams;
    }
    
    private String getTirePosition(byte position){
        if(TpmsService.FRONT_LEFT == position){
            return "左前轮";
        } else if(TpmsService.FRONT_RIGHT == position){
            return "右前轮";
        } else if(TpmsService.REAR_LEFT == position){
            return "左后轮";
        } else if(TpmsService.REAR_RIGHT == position){
            return "右后轮";
        }else {
            return "";
        }
    }
    
    public boolean createAlarmWindow(byte tirePosition, WaningType type) {
        try {
            
            WarningFloatWindow warningWindow;
            if (warningmWindowsMap.containsKey(tirePosition)) {
                warningWindow = warningmWindowsMap.get(tirePosition);
            } else {
                warningWindow = new WarningFloatWindow(mContext);
            }

            RelativeLayout waningView = (RelativeLayout)warningWindow.findViewById(R.id.warnig_bg);
            TextView tvWarning = (TextView) warningWindow.findViewById(R.id.tv_warning);
            waningView.setOnTouchListener(new OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    //点击任意区域，悬浮窗消失
                    Byte tirePosition = (Byte)v.getTag();
                    removeAlarmWindow(tirePosition);
                    return false;
                }
            });
            
            waningView.setTag(tirePosition);

            switch (type) {
                case QUICK_LEAK:
                     tvWarning.setText(getTirePosition(tirePosition) + "快漏气");
                     break;
                case SLOW_LEAK:
                     tvWarning.setText(getTirePosition(tirePosition) + "慢漏气");
                     break;
                case OVER_PRESSURE:
                     tvWarning.setText(getTirePosition(tirePosition) + "胎压高");
                     break;
                case LESS_PRESSURE:
                     tvWarning.setText(getTirePosition(tirePosition) + "胎压低");
                     break;
                case OVER_HEATING:
                     tvWarning.setText(getTirePosition(tirePosition) + "胎温高");
                     break;
            }
            mWManager.addView(warningWindow, getTypeLayoutParams());
            warningmWindowsMap.put(tirePosition, warningWindow);
            return true;
        } catch (Exception e) {
            Log.e(TAG, "createAlarmWindow error:" + e.toString());
            e.printStackTrace();
        }
        return false;
    }

    public boolean containFloatWindow(Byte tirePosition) {
        return warningmWindowsMap.containsKey(tirePosition);
    }
    
    public void removeAlarmWindow(Byte tirePosition) {
        
        if (warningmWindowsMap.containsKey(tirePosition)) {
            WarningFloatWindow alarmFloatWindow = warningmWindowsMap.remove(tirePosition);
            alarmFloatWindow.onDestory();
            mWManager.removeView(alarmFloatWindow);
        }
    }
    
    public void removeAllWindow() {
        if (warningmWindowsMap != null) {
            Set<Byte> keyList = warningmWindowsMap.keySet();
            Iterator<Byte> keyIterator = keyList.iterator();
            while (keyIterator.hasNext()) {
                mWManager.removeView(warningmWindowsMap.get(keyIterator.next()));
            }
            warningmWindowsMap.clear();
        }
    }
    
}
