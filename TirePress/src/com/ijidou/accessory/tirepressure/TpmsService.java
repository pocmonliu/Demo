package com.ijidou.accessory.tirepressure;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.ijidou.accessory.tirepress.common.CommonUtils;
import com.ijidou.accessory.tirepress.common.ITireMatchListener;
import com.ijidou.accessory.tirepress.common.ITireStateListener;
import com.ijidou.accessory.tirepress.common.ITireWarningValueListener;
import com.ijidou.accessory.tirepress.common.ITpmsService;

import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.CanbusManager;
import android.os.CanbusManager.CarInfoDetailsListener;
import android.os.CarInfoState;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.canbus.HexStringUtils;
import android.util.Log;
import android.webkit.WebView.PrivateAccess;

public class TpmsService extends Service implements ITpmsService{
    public static final String TAG = "Tpms:TpmsService";

    public static final byte ComID_TireMatching = 0x00;
    public static final byte ComID_HighPressureWarning = 0x01;
    public static final byte ComID_LowPressureWarning = 0x02;
    public static final byte ComID_HighTemperatureWarning = 0x03;
    public static final byte ComID_TireState = 0x04;
    public static final byte ComID_SleepSetting = 0x05;
    
    public static final byte Signal_TireMatching = 0x00;
    public static final byte Signal_HighPressureWarning = 0x01;
    public static final byte Signal_LowPressureWarning = 0x02;
    public static final byte Signal_HighTemperatureWarning = 0x03;
    public static final byte Signal_ExtensibleSetting = 0x05;
    
    public static final String tireFrontLeft = "tireFrontLeft";
    public static final String tireFrontRight = "tireFrontRight";
    public static final String tireRearLeft = "tireRearLeft";
    public static final String tireRearRight = "tireRearRight";
    
    public static final byte FRONT_LEFT = 0x00;
    public static final byte FRONT_RIGHT = 0x01;
    public static final byte REAR_LEFT = 0x02;
    public static final byte REAR_RIGHT = 0x03;
    
    public static final int WHAT_ARRIVED = 0;
    public static final int WHAT_SLEEP = 1;
    
    public static final byte CANCEL_TIRE_MATCH = (byte) 0xFF;
    
    static {
        System.loadLibrary("tpms_serial_jni");
    }
    
    private native void native_open();
    private native void native_close();
    private native int native_send_message(byte[] sendMsg, int slen);

    private int carSpeed;
    
    private CanbusManager mCanbusManager = null;
    private Handler msgHandler = null;
    private static Handler staticMsgHandler = null;
    
    private TpmsWarning tpmsWarning = null;
    
    private HashMap<Byte, Byte> mActuallyMatchedTires = new HashMap<Byte, Byte>();
    
    private MatchOperation mMatchOperation = new MatchOperation();
    
    private TireState mTireFrontLeft = new TireState();
    private TireState mTireFrontRight = new TireState();
    private TireState mTireRearLeft = new TireState();
    private TireState mTireRearRight = new TireState();
    
    private ITireStateListener mTireStateListener;
    private ITireMatchListener mTireMatchListener;
    private ITireWarningValueListener mTireWarningValueListener;
    
    private static class MessageData {
        
        byte[] data;
        
        int length;
        
        /**
         * Length including time stamps
         */
        int debugLength;
        
        /**
         * Time stamp when message arrived in service JNI
         */
        long msgArrivedTimeInJni;
        
        /**
         * Time stamp when message arrived in service JAVA
         */
        long msgArrivedTimeInJava;
        
        public MessageData(byte[] message, int length) {
            
            data = new byte[message.length];
            System.arraycopy(message, 0, data, 0, data.length);
            
            this.length = length;
            //[msg][JNI time length][JNI time][JAVA length][JAVA time]
            //     0                1         9            10         18 (index)
            //[msg][1]              [8]       [1]          [8]           (length)  
            int off = length;
            if (message.length <= off) {
                return;
            }
            
            off++;                   //pass timestamp for JNI  (length)
            if (message.length < off + 8) {
                return;
            }
            msgArrivedTimeInJni = HexStringUtils.bytes2Long(message, off);
            off += 8;
            
            off++;                   //pass timestamp for JNI  (length)
            if (message.length < off + 8) {
                return;
            }
            msgArrivedTimeInJava = HexStringUtils.bytes2Long(message, off);
            off += 8;
            
            debugLength = off;
        }
        
        public byte[] getData() {
            return data;
        }
        
        public int getLength() {
            return length;
        }

        /**
         * length with debug information
         * @return
         */
        public int getDebugLenth() {
            return debugLength;
        }
        
        public long getMsgArrivedTimeInJni() {
            return msgArrivedTimeInJni;
        }

        public long getMsgArrivedTimeInJava() {
            return msgArrivedTimeInJava;
        }
    }
    
    private Handler setStaticHandler(Handler msgHandler){
        staticMsgHandler = msgHandler;
        return staticMsgHandler;
    }
    
    private class MatchOperation {
        
        private boolean matching = false;
        private byte position = (byte) 0xFF;
        
        private boolean isMacthing(){
            return matching;
        }
        
        public synchronized void requestMacth(byte position){
            this.matching = true;
            this.position = (byte)((position << 4) & 0xF0);
            hudSendMessage(Signal_TireMatching, this.position);
        }
       
        public synchronized void cancelMatch(){
            this.matching = false;
            hudSendMessage(Signal_TireMatching, CANCEL_TIRE_MATCH);
        }
        
        public synchronized void onMatchReply(byte position, byte result){
            this.matching = false;
            
            if(mTireMatchListener != null){
                mTireMatchListener.onResult(position, result);
            }    
        }
        
        public synchronized void tiresChange(byte position){
            byte tmpPosition = mActuallyMatchedTires.get(position);
            mActuallyMatchedTires.put(position, mActuallyMatchedTires.get(this.position));
            mActuallyMatchedTires.put(this.position, tmpPosition);
            
            saveSpActuallyMatchedTires(mActuallyMatchedTires);
            
            if(mTireMatchListener != null){
                mTireMatchListener.onChanged();
            }    
        }
    }

    private class MsgHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
                switch (msg.what) {
                    case WHAT_ARRIVED:
                
                        if (!(msg.obj instanceof MessageData)) {
                            Log.e(TAG, "msg.obj is not MessageData");
                            return;
                        }
                
                        MessageData md = (MessageData) msg.obj;
                
//                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss", Locale.getDefault());
//                        Log.d(TAG, sdf.format(new Date()) + " : " 
//                                + " jni="   + md.getMsgArrivedTimeInJni() 
//                                + " java=" + md.getMsgArrivedTimeInJava()
//                                + " data=" + HexStringUtils.bytes2HexString(md.getData(), 0, md.getLength(), true));

                        handleData(md);
                        break;
                    case WHAT_SLEEP:
                        //收到休眠广播后
                        
                        break;
                    default:
                        break;
            }
        }
    };

    private byte getActuallyPositionFromJniPosition(Byte jniPosition){
        
        Iterator<Entry<Byte, Byte>> it = mActuallyMatchedTires.entrySet().iterator();
        while(it.hasNext()) {
            Map.Entry<Byte, Byte> entry = (Map.Entry<Byte, Byte>)it.next();
            if(entry.getValue().equals(jniPosition)) {
                byte actuallyPoition = entry.getKey().byteValue();
                return actuallyPoition;
            }
        }
        
        return (byte) 0xFF;
    }
    
    private void handleData(MessageData md) {
        byte[] data = md.getData();
        int comId = (data[4] & 0x000000FF);
        
        switch (comId) {
            case ComID_TireState:
//                 //配对时，界面不显示车胎数据，等待待配对传感器放气以获取数据
//                 if(mMatchOperation.isMacthing()){
//                     return;
//                 }
                
                 byte jniPosition = data[5];
                 byte actuallyPosition = getActuallyPositionFromJniPosition(jniPosition);
                 
                 TireState tireState = new TireState(data);

                 if(FRONT_LEFT == actuallyPosition){
                     tpmsWarning.updateTireWarningFL(tireState);
                     if(mTireFrontLeft.equals(tireState) && mTireStateListener != null){
                         mTireStateListener.onFrontLeft(tireState);
                     };
                 }else if(FRONT_RIGHT == actuallyPosition) {
                     tpmsWarning.updateTireWarningFR(tireState);
                     if(mTireFrontRight.equals(tireState) && mTireStateListener != null){
                         mTireStateListener.onFrontRight(tireState);
                     };
                 }else if(REAR_LEFT == actuallyPosition) {
                     tpmsWarning.updateTireWarningRL(tireState);
                     if(mTireRearLeft.equals(tireState) && mTireStateListener != null){
                         mTireStateListener.onRearLeft(tireState);
                     };
                 }else if(REAR_RIGHT == actuallyPosition) {
                     tpmsWarning.updateTireWarningRR(tireState);
                     if(mTireFrontRight.equals(tireState) && mTireStateListener != null){
                         mTireStateListener.onRearRight(tireState);
                     };
                 }

                 break;
            case ComID_TireMatching:
                byte matchReply = (byte)(data[5] & 0x0F);
                byte tirePosition = (byte)((data[5] >> 4) & 0x0F);
                mMatchOperation.onMatchReply(tirePosition, matchReply);
                
                break;
            case ComID_HighPressureWarning:
                //轮胎高压报警值
                double valueHighPressure = CommonUtils.convertDouble(data[5]);  
                if(mTireWarningValueListener != null){
                    mTireWarningValueListener.onHighPressure(valueHighPressure);
                }
                break; 
            case ComID_LowPressureWarning:
                double valueLowPressure = CommonUtils.convertDouble(data[5]);
                if(mTireWarningValueListener != null){
                    mTireWarningValueListener.onLowPressure(valueLowPressure);
                }
                break; 
            case ComID_HighTemperatureWarning:
                int valueHighTemperature = data[5] & 0x000000FF - 40;
                if(mTireWarningValueListener != null){
                    mTireWarningValueListener.onHighTemperature(valueHighTemperature);
                }
                break;
            case ComID_SleepSetting:
                
                break;
            default:
                Log.d(TAG, "unsupport comId:" + comId);
                break;
        }
    }
    
    /** 
     * 返回一个Binder对象 
     */  
    @Override  
    public IBinder onBind(Intent intent) {  
        return new TpmsBinder();  
    }  
      
    public class TpmsBinder extends Binder{  
        /** 
         * 获取当前Service的实例 
         * @return 
         */  
        public TpmsService getService(){  
            return TpmsService.this;  
        }  
    }  
    
    @Override
    public void onCreate() {
        super.onCreate();
        
        IntentFilter filter = new IntentFilter();  
        filter.addAction(Intent.ACTION_SCREEN_OFF); //系统休眠检测信息  
        registerReceiver(broadcast, filter);
        
        getSpActuallyMatchedTires();
        
        mCanbusManager = (CanbusManager) getSystemService(Context.CANBUS_SERVICE);
        if(mCanbusManager != null){
            mCanbusManager.registerCarInfoDetailsListener(mCarInfoDetailsListener);
        }
        
        msgHandler = new MsgHandler();
        setStaticHandler(msgHandler);
        
        tpmsWarning = new TpmsWarning(this);
        
        new Thread(new Runnable() {
            
            @Override
            public void run() {
                native_open();
            }
        }).start();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub
        return super.onStartCommand(intent, flags, startId);
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcast);
    }
    
    /**
     * 
     */
    private BroadcastReceiver broadcast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                msgHandler.sendEmptyMessage(WHAT_SLEEP);
            }
        }
    };
    
    /**
     * 注册车胎状态监听
     * @param tireStateListener
     */
    public void registerTireStateListener(ITireStateListener tireStateListener) {
        if (tireStateListener == null) {
            return;
        }
        mTireStateListener = tireStateListener;
    }
    
    /**
     * 注册车胎配对监听
     * @param tireMatchListener
     */
    public void registerTireMatchListener(ITireMatchListener tireMatchListener) {
        if (tireMatchListener == null) {
            return;
        }
        mTireMatchListener = tireMatchListener;
    }
    
    /**
     * 注册车胎报警值监听
     * @param tireMatchListener
     */
    public void registerTireWarningValueListener(ITireWarningValueListener tireWarningValueListener) {
        if (tireWarningValueListener == null) {
            return;
        }
        mTireWarningValueListener = tireWarningValueListener;
    }
    
    /**
     * 移除车胎状态监听
     * @param tireStateListener
     */
    public void unRegisterTireStateListener() {
        mTireStateListener = null;
    }
    
    /**
     * 移除车胎状态监听
     * @param tireStateListener
     */
    public void unRegisterTireMatchListener() {
        mTireMatchListener = null;
    }

    /**
     * 移除车胎报警值监听
     * @param tireStateListener
     */
    public void unRegisterTireWarningValueListener() {
        mTireWarningValueListener = null;
    }
    
    public boolean isMatching(){
        return mMatchOperation.isMacthing();
    }
    
    public void requestMatch(byte position){ 
        mMatchOperation.requestMacth(position);
    }
    
    public void cancelMatch(){ 
        mMatchOperation.cancelMatch();
    }
    
    public void tireChange(byte position){ 
        mMatchOperation.tiresChange(position);
    }
    
    @Override
    public void setHighPressureWarning(double value) {
       if (((int)value) == 0xFF){
           hudSendMessage(Signal_HighPressureWarning, (byte)0xFF);
       } else {
          String strValue = String.valueOf((int)(value * 10));
          byte byteValue = Integer.valueOf(strValue, 16).byteValue();
          hudSendMessage(Signal_HighPressureWarning, byteValue);
       }
    }
    
    @Override
    public void setLowPressureWarning(double value) {
        if (((int)value) == 0xFF){
            hudSendMessage(Signal_LowPressureWarning, (byte)0xFF);
        } else {
           String strValue = String.valueOf((int)(value * 10));
           byte byteValue = Integer.valueOf(strValue, 16).byteValue();
           hudSendMessage(Signal_LowPressureWarning, byteValue);
        }
    }
    
    @Override
    public void setHighTemperatureWarning(int value) {
        if (value == 0xFF){
            hudSendMessage(Signal_HighTemperatureWarning, (byte)0xFF);
        } else {
            byte byteValue = (byte)((value + 40) & 0xFF);
            hudSendMessage(Signal_HighTemperatureWarning, byteValue);
        }
    }
    
    @Override
    public void setSleepSetting(byte data) {
        // TODO Auto-generated method stub
        
    }
    
    private static void notifyListener(int comId, byte[] msgArrived, int length) {

        byte[] msg = new byte[msgArrived.length];
        System.arraycopy(msgArrived, 0, msg, 0, msgArrived.length);

        int off = length;
        off++;
        off += 8;
        
        long serviceJavaTime = System.currentTimeMillis();
        msg[off] = 8;
        off++;
        System.arraycopy(HexStringUtils.long2Bytes(serviceJavaTime), 0, msg, off, 8);
        off += 8;
        
        if (!checkMessage (msg, length)) {
            Log.d(TAG, "check sum is wrong!!!: " + HexStringUtils.bytes2HexString(msg, 0, length , true));
            return;
        }

        MessageData md = new MessageData(msg, length);
        Message message = staticMsgHandler.obtainMessage(WHAT_ARRIVED, md);
        staticMsgHandler.sendMessage(message);
    }
    
    /**
   * <pre>
   * FF F5 03 00 00 00 03
   *   sum=03+00+00+00=03
   * </pre>
   * @param md
   * @return
   */
    private static boolean checkMessage(byte[] message, int length) {
        
        if (message == null) {
            return false;
        }
        
        int sum = 0;
        for (int i = 2; i < length - 1; i++) {
            sum += (message[i] & 0x000000FF);
        }
        sum &= 0x000000FF;
           
        if ((sum & 0x000000FF) == (message[length-1] & 0x000000FF)) {
            return true;
        } else {
            return false;
        }
    }
    
    private int hudSendMessage(byte comId, byte data){
        byte[] sendMsg = new byte[7];
        sendMsg[0] = (byte) 0xff;
        sendMsg[1] = (byte) 0xf5;
        sendMsg[2] = (byte) 0x03;
        sendMsg[3] = (byte) 0x00;
        sendMsg[4] = comId;
        sendMsg[5] = data;
        sendMsg[6] = (byte) 0xff;
        return native_send_message(sendMsg, sendMsg.length);
    }

    private CarInfoDetailsListener mCarInfoDetailsListener = new CarInfoDetailsListener() {
        @Override
        public void carInfoState(CarInfoState state) {
            carSpeed = state.getBasicspeed();
        }
    };
    
    public int getCarSpeed(){
        return carSpeed;
    }
    
    public void getSpActuallyMatchedTires() {
        
        SharedPreferences sp = TpmsApplication.gContext.getSharedPreferences(
                "ActuallyMatchedTires", Activity.MODE_PRIVATE);
        
        int tFrontLeft = sp.getInt(tireFrontLeft, FRONT_LEFT);
        mActuallyMatchedTires.put(FRONT_LEFT, (byte)(tFrontLeft & 0xFF));
        
        int tFrontRight = sp.getInt(tireFrontRight, FRONT_RIGHT);
        mActuallyMatchedTires.put(FRONT_RIGHT, (byte)(tFrontRight & 0xFF));
        
        int tRearLeft = sp.getInt(tireRearLeft, REAR_LEFT);
        mActuallyMatchedTires.put(REAR_LEFT, (byte)(tRearLeft & 0xFF));
        
        int tRearRight = sp.getInt(tireRearRight, REAR_RIGHT);
        mActuallyMatchedTires.put(REAR_RIGHT, (byte)(tRearRight & 0xFF));
    }

    public void saveSpActuallyMatchedTires(final HashMap<Byte, Byte> actuallyMatchedTires) {
        new Thread(new Runnable() {
           
            @Override
            public void run() {
                // TODO Auto-generated method stub
                SharedPreferences sp = TpmsApplication.gContext.getSharedPreferences(
                        "ActuallyMatchedTires", Activity. MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                
                int tFrontLeft = mActuallyMatchedTires.get(FRONT_LEFT) & 0x000000FF;
                editor.putInt(tireFrontLeft, tFrontLeft);
                
                int tFrontRight = mActuallyMatchedTires.get(FRONT_RIGHT) & 0x000000FF;
                editor.putInt(tireFrontRight, tFrontRight);
                
                int tRearLeft = mActuallyMatchedTires.get(REAR_LEFT) & 0x000000FF;
                editor.putInt(tireRearLeft, tRearLeft);
                
                int tRearRight = mActuallyMatchedTires.get(REAR_RIGHT) & 0x000000FF;
                editor.putInt(tireRearRight, tRearRight);
               
                editor.commit();
            }
        }).start();
    }
    
}
