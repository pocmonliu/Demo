package com.ijidou.accessory.tirepressure;

import com.ijidou.accessory.tirepress.controls.WarningWindowManager;
import com.ijidou.accessory.tirepress.controls.WarningWindowManager.WaningType;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

public class TpmsWarning {
    public static final String TAG = "Tpms:TpmsWarning";
    
    private final int DISSMISS_ALARM_WINDOW_TIME = 5 * 1000;
    
    private final long INTERVAL_WARNING_QUICK_LEAK = 5 * 60 * 1000;
    private final int COUNT_WARNING_QUICK_LEAK = 6;
    
    private final long INTERVAL_WARNING_SLOW_LEAK = 10 * 60 * 1000;
    private final int COUNT_WARNING_SLOW_LEAK = 3;
    
    private final long INTERVAL_WARNING_OVER_PRESSURE = 10 * 60 * 1000;
    private final int COUNT_WARNING_OVER_PRESSURE = 2;
    
    private final long INTERVAL_WARNING_LESS_PRESSURE = 10 * 60 * 1000;
    private final int COUNT_WARNING_LESS_PRESSURE = 2;
    
    private final long INTERVAL_WARNING_OVER_HEATING = 10 * 60 * 1000;
    private final int COUNT_WARNING_OVER_HEATING = 2;
    
    private Context mContext;
    private WarningWindowManager alarmWindowManager;
    
    private WarningItem mWarningQuickLeakFL = null;
    private WarningItem mWarningQuickLeakFR = null;
    private WarningItem mWarningQuickLeakRL = null;
    private WarningItem mWarningQuickLeakRR = null;

    private WarningItem mWarningSlowLeakFL = null;
    private WarningItem mWarningSlowLeakFR = null;
    private WarningItem mWarningSlowLeakRL = null;
    private WarningItem mWarningSlowLeakRR = null;

    private WarningItem mWarningOverPressureFL = null;
    private WarningItem mWarningOverPressureFR = null;
    private WarningItem mWarningOverPressureRL = null;
    private WarningItem mWarningOverPressureRR = null;

    private WarningItem mWarningLessPressureFL = null;
    private WarningItem mWarningLessPressureFR = null;
    private WarningItem mWarningLessPressureRL = null;
    private WarningItem mWarningLessPressureRR = null;

    private WarningItem mWarningOverHeatingFL = null;
    private WarningItem mWarningOverHeatingFR = null;
    private WarningItem mWarningOverHeatingRL = null;
    private WarningItem mWarningOverHeatingRR = null;
    
    public TpmsWarning(Context context) {
        this.mContext = context;
        alarmWindowManager = new WarningWindowManager(context);
        
        mWarningQuickLeakFL = new WarningItem("左前轮", TpmsService.FRONT_LEFT, 
                false, COUNT_WARNING_QUICK_LEAK, -1);
        mWarningQuickLeakFR = new WarningItem("右前轮", TpmsService.FRONT_RIGHT, 
                false, COUNT_WARNING_QUICK_LEAK, -1);
        mWarningQuickLeakRL = new WarningItem("左后轮", TpmsService.REAR_LEFT, 
                false, COUNT_WARNING_QUICK_LEAK, -1);
        mWarningQuickLeakRR = new WarningItem("右后轮", TpmsService.REAR_RIGHT, 
                false, COUNT_WARNING_QUICK_LEAK, -1);
        
        mWarningSlowLeakFL = new WarningItem("左前轮", TpmsService.FRONT_LEFT, 
                false, COUNT_WARNING_SLOW_LEAK, -1);
        mWarningSlowLeakFR = new WarningItem("右前轮", TpmsService.FRONT_RIGHT, 
                false, COUNT_WARNING_SLOW_LEAK, -1);
        mWarningSlowLeakRL = new WarningItem("左后轮", TpmsService.REAR_LEFT, 
                false, COUNT_WARNING_SLOW_LEAK, -1);
        mWarningSlowLeakRR = new WarningItem("右后轮", TpmsService.REAR_RIGHT, 
                false, COUNT_WARNING_SLOW_LEAK, -1);
        
        mWarningOverPressureFL = new WarningItem("左前轮", TpmsService.FRONT_LEFT, 
                false, COUNT_WARNING_OVER_PRESSURE, -1);
        mWarningOverPressureFR = new WarningItem("右前轮", TpmsService.FRONT_RIGHT, 
                false, COUNT_WARNING_OVER_PRESSURE, -1);
        mWarningOverPressureRL = new WarningItem("左后轮", TpmsService.REAR_LEFT, 
                false, COUNT_WARNING_OVER_PRESSURE, -1);
        mWarningOverPressureRR = new WarningItem("右后轮", TpmsService.REAR_RIGHT, 
                false, COUNT_WARNING_OVER_PRESSURE, -1);
        
        mWarningLessPressureFL = new WarningItem("左前轮", TpmsService.FRONT_LEFT, 
                false, COUNT_WARNING_LESS_PRESSURE, -1);
        mWarningLessPressureFR = new WarningItem("右前轮", TpmsService.FRONT_RIGHT, 
                false, COUNT_WARNING_LESS_PRESSURE, -1);
        mWarningLessPressureRL = new WarningItem("左后轮", TpmsService.REAR_LEFT, 
                false, COUNT_WARNING_LESS_PRESSURE, -1);
        mWarningLessPressureRR = new WarningItem("右后轮", TpmsService.REAR_RIGHT, 
                false, COUNT_WARNING_LESS_PRESSURE, -1);
        
        mWarningOverHeatingFL = new WarningItem("左前轮", TpmsService.FRONT_LEFT, 
                false, COUNT_WARNING_OVER_HEATING, -1);
        mWarningOverHeatingFR = new WarningItem("右前轮", TpmsService.FRONT_RIGHT, 
                false, COUNT_WARNING_OVER_HEATING, -1);
        mWarningOverHeatingRL = new WarningItem("左后轮", TpmsService.REAR_LEFT, 
                false, COUNT_WARNING_OVER_HEATING, -1);
        mWarningOverHeatingRR = new WarningItem("右后轮", TpmsService.REAR_RIGHT, 
                false, COUNT_WARNING_OVER_HEATING, -1);
    }
    
    private class WarningItem{
        String strTirePosition = "";
        byte tirePosition = (byte) 0xFF;
        boolean isWarning = false;
        int needWarningCount = 0;
        long lastWaningTime = -1;
        
        public WarningItem(String strTirePosition, byte tirePosition,boolean isWarning,int needWarningCount,long lastWaningTime ){
            this.strTirePosition = strTirePosition;
            this.tirePosition = tirePosition;
            this.isWarning = isWarning;
            this.needWarningCount = needWarningCount;
            this.lastWaningTime = lastWaningTime;
        }
    }
    
    /**
     * 处理悬浮窗5秒后自动隐藏
     */
    private Handler mHandler = new Handler(){
        public void handleMessage(Message msg) {
            byte tirePostion = (byte) msg.obj;
            alarmWindowManager.removeAlarmWindow(tirePostion);
        };
    };
    
    private void broadVoice(String content) {
        String action = "com.ijidou.voice.broadcastvoice";
        Intent i = new Intent(action);
        i.putExtra("method", "braodcastvoice");
        i.putExtra("content", content);
        mContext.startService(i);
    }
    
    public void updateTireWarningFL(TireState tireState){
        mWarningQuickLeakFL.isWarning = tireState.quickLeak;
        updateWarningQuickLeak(mWarningQuickLeakFL);
        
        mWarningSlowLeakFL.isWarning = tireState.slowLeak;
        updateWarningSlowLeak(mWarningSlowLeakFL);
        
        mWarningOverPressureFL.isWarning = tireState.overPressure;
        updateWarningOverPressure(mWarningOverPressureFL);
        
        mWarningLessPressureFL.isWarning = tireState.lessPressure;
        updateWarningLessPressure(mWarningLessPressureFL);
        
        mWarningOverHeatingFL.isWarning = tireState.overHeating;
        updateWarningOverHeating(mWarningOverHeatingFL);
    }
    
    public void updateTireWarningFR(TireState tireState){
        mWarningQuickLeakFR.isWarning = tireState.quickLeak;
        updateWarningQuickLeak(mWarningQuickLeakFR);
        
        mWarningSlowLeakFR.isWarning = tireState.slowLeak;
        updateWarningSlowLeak(mWarningSlowLeakFR);
        
        mWarningOverPressureFR.isWarning = tireState.overPressure;
        updateWarningOverPressure(mWarningOverPressureFR);
        
        mWarningLessPressureFR.isWarning = tireState.lessPressure;
        updateWarningLessPressure(mWarningLessPressureFR);
        
        mWarningOverHeatingFR.isWarning = tireState.overHeating;
        updateWarningOverHeating(mWarningOverHeatingFR);
    }
    
    public void updateTireWarningRL(TireState tireState){
        mWarningQuickLeakRL.isWarning = tireState.quickLeak;
        updateWarningQuickLeak(mWarningQuickLeakRL);
        
        mWarningSlowLeakRL.isWarning = tireState.slowLeak;
        updateWarningSlowLeak(mWarningSlowLeakRL);
        
        mWarningOverPressureRL.isWarning = tireState.overPressure;
        updateWarningOverPressure(mWarningOverPressureRL);
        
        mWarningLessPressureRL.isWarning = tireState.lessPressure;
        updateWarningLessPressure(mWarningLessPressureRL);
        
        mWarningOverHeatingRL.isWarning = tireState.overHeating;
        updateWarningOverHeating(mWarningOverHeatingRL);
    }
    
    public void updateTireWarningRR(TireState tireState){
        mWarningQuickLeakRR.isWarning = tireState.quickLeak;
        updateWarningQuickLeak(mWarningQuickLeakRR);
        
        mWarningSlowLeakRR.isWarning = tireState.slowLeak;
        updateWarningSlowLeak(mWarningSlowLeakRR);
        
        mWarningOverPressureRR.isWarning = tireState.overPressure;
        updateWarningOverPressure(mWarningOverPressureRR);
        
        mWarningLessPressureRR.isWarning = tireState.lessPressure;
        updateWarningLessPressure(mWarningLessPressureRR);
        
        mWarningOverHeatingRR.isWarning = tireState.overHeating;
        updateWarningOverHeating(mWarningOverHeatingRR);
    }
    
    /**
     * 快漏气报警
     * 
     * @param 
     */
    private void updateWarningQuickLeak(WarningItem warningItem) {

        if (!warningItem.isWarning) {
            alarmWindowManager.removeAlarmWindow(warningItem.tirePosition);
            warningItem.needWarningCount = COUNT_WARNING_QUICK_LEAK;
            warningItem.lastWaningTime = -1;
            return;
        }

        boolean warning = false;
        
        if (warningItem.needWarningCount > 0 ) {
            if (System.currentTimeMillis() - warningItem.lastWaningTime > INTERVAL_WARNING_QUICK_LEAK) {
                warning = true;
            }
        }
        
        if (warning) {
            String content = warningItem.strTirePosition + "快漏气";
            broadVoice(content);
            
            if(warningItem.needWarningCount == COUNT_WARNING_QUICK_LEAK){
                if (!alarmWindowManager.containFloatWindow(warningItem.tirePosition)) {
                    boolean result = alarmWindowManager.createAlarmWindow(warningItem.tirePosition, WaningType.QUICK_LEAK);
                    if (result) {
                        Message msg = mHandler.obtainMessage();
                        msg.obj = warningItem.tirePosition;
                        mHandler.sendMessageDelayed(msg, DISSMISS_ALARM_WINDOW_TIME);
                    }
                }
            }
            
            if(warningItem.needWarningCount > 0){
                warningItem.needWarningCount--;
            }
            warningItem.lastWaningTime = System.currentTimeMillis();
        }
    }
    
    /**
     * 慢漏气报警
     * 
     * @param 
     */
    private void updateWarningSlowLeak(WarningItem warningItem) {

        if (!warningItem.isWarning) {
            alarmWindowManager.removeAlarmWindow(warningItem.tirePosition);
            warningItem.needWarningCount = COUNT_WARNING_SLOW_LEAK;
            warningItem.lastWaningTime = -1;
            return;
        }

        boolean warning = false;
        
        if (warningItem.needWarningCount > 0 ) {
            if (System.currentTimeMillis() - warningItem.lastWaningTime > INTERVAL_WARNING_SLOW_LEAK) {
                warning = true;
            }
        }
        
        if (warning) {
            String content = warningItem.strTirePosition + "慢漏气";
            broadVoice(content);
            
            if (!alarmWindowManager.containFloatWindow(warningItem.tirePosition)) {
                boolean result = alarmWindowManager.createAlarmWindow(warningItem.tirePosition, WaningType.SLOW_LEAK);
                if (result) {
                    Message msg = mHandler.obtainMessage();
                    msg.obj = warningItem.tirePosition;
                    mHandler.sendMessageDelayed(msg, DISSMISS_ALARM_WINDOW_TIME);
                }
            }
            
            if(warningItem.needWarningCount > 0){
                warningItem.needWarningCount--;
            }
            warningItem.lastWaningTime = System.currentTimeMillis();
        }
    }
    
    /**
     * 胎压高报警
     * 
     * @param 
     */
    private void updateWarningOverPressure(WarningItem warningItem) {

        if (!warningItem.isWarning) {
            alarmWindowManager.removeAlarmWindow(warningItem.tirePosition);
            warningItem.needWarningCount = COUNT_WARNING_OVER_PRESSURE;
            warningItem.lastWaningTime = -1;
            return;
        }

        boolean warning = false;
        
        if (warningItem.needWarningCount > 0 ) {
            if (System.currentTimeMillis() - warningItem.lastWaningTime > INTERVAL_WARNING_OVER_PRESSURE) {
                warning = true;
            }
        }
        
        if (warning) {
            String content = warningItem.strTirePosition + "胎压高";
            broadVoice(content);
            
            if (!alarmWindowManager.containFloatWindow(warningItem.tirePosition)) {
                boolean result = alarmWindowManager.createAlarmWindow(warningItem.tirePosition, WaningType.OVER_PRESSURE);
                if (result) {
                    Message msg = mHandler.obtainMessage();
                    msg.obj = warningItem.tirePosition;
                    mHandler.sendMessageDelayed(msg, DISSMISS_ALARM_WINDOW_TIME);
                }
            }
            
            if(warningItem.needWarningCount > 0){
                warningItem.needWarningCount--;
            }
            warningItem.lastWaningTime = System.currentTimeMillis();
        }
    }
    
    /**
     * 胎压低报警
     * 
     * @param 
     */
    private void updateWarningLessPressure(WarningItem warningItem) {

        if (!warningItem.isWarning) {
            alarmWindowManager.removeAlarmWindow(warningItem.tirePosition);
            warningItem.needWarningCount = COUNT_WARNING_LESS_PRESSURE;
            warningItem.lastWaningTime = -1;
            return;
        }

        boolean warning = false;
        
        if (warningItem.needWarningCount > 0 ) {
            if (System.currentTimeMillis() - warningItem.lastWaningTime > INTERVAL_WARNING_LESS_PRESSURE) {
                warning = true;
            }
        }
        
        if (warning) {
            String content = warningItem.strTirePosition + "胎压低";
            broadVoice(content);
            
            if (!alarmWindowManager.containFloatWindow(warningItem.tirePosition)) {
                boolean result = alarmWindowManager.createAlarmWindow(warningItem.tirePosition, WaningType.LESS_PRESSURE);
                if (result) {
                    Message msg = mHandler.obtainMessage();
                    msg.obj = warningItem.tirePosition;
                    mHandler.sendMessageDelayed(msg, DISSMISS_ALARM_WINDOW_TIME);
                }
            }
            
            if(warningItem.needWarningCount > 0){
                warningItem.needWarningCount--;
            }
            warningItem.lastWaningTime = System.currentTimeMillis();
        }
    }
    
    /**
     * 胎温高报警
     * 
     * @param 
     */
    private void updateWarningOverHeating(WarningItem warningItem) {

        if (!warningItem.isWarning) {
            alarmWindowManager.removeAlarmWindow(warningItem.tirePosition);
            warningItem.needWarningCount = COUNT_WARNING_OVER_HEATING;
            warningItem.lastWaningTime = -1;
            return;
        }

        boolean warning = false;
        
        if (warningItem.needWarningCount > 0 ) {
            if (System.currentTimeMillis() - warningItem.lastWaningTime > INTERVAL_WARNING_OVER_HEATING) {
                warning = true;
            }
        }
        
        if (warning) {
            String content = warningItem.strTirePosition + "胎温高";
            broadVoice(content);
            
            if (!alarmWindowManager.containFloatWindow(warningItem.tirePosition)) {
                boolean result = alarmWindowManager.createAlarmWindow(warningItem.tirePosition, WaningType.OVER_HEATING);
                if (result) {
                    Message msg = mHandler.obtainMessage();
                    msg.obj = warningItem.tirePosition;
                    mHandler.sendMessageDelayed(msg, DISSMISS_ALARM_WINDOW_TIME);
                }
            }
            
            if(warningItem.needWarningCount > 0){
                warningItem.needWarningCount--;
            }
            warningItem.lastWaningTime = System.currentTimeMillis();
        }
    }
    
}
