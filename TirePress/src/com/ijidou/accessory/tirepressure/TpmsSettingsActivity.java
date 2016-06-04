package com.ijidou.accessory.tirepressure;

import com.ijidou.accessory.tirepress.R;
import com.ijidou.accessory.tirepress.common.ThemeListener;
import com.ijidou.accessory.tirepress.common.ITireWarningValueListener;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Window;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class TpmsSettingsActivity extends Activity implements ThemeListener, OnSeekBarChangeListener {
    private final String TAG = "Tpms: TpmsSettingsActivity";
    
    private TpmsService tpmsService = null;
   
    public static final double QUERY_HIGH_PRESSURE = 0xFF;
    public static final double QUERY_LOW_PRESSURE = 0xFF;
    public static final int QUERY_HIGH_TEMPERATURE = 0xFF;
    
    private SeekBar sbHighPressure;
    private TextView tvHighPressureValue;
    private SeekBar sbLowPressure;
    private TextView tvLowPressureValue;
    private SeekBar sbHighTemperature;
    private TextView tvHighTemperatureValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_settings);
    
        sbHighPressure = (SeekBar)findViewById(R.id.sb_high_pressure);
        tvHighPressureValue = (TextView)findViewById(R.id.tv_high_pressure_value);
        sbLowPressure = (SeekBar)findViewById(R.id.sb_low_pressure);
        tvLowPressureValue = (TextView)findViewById(R.id.tv_low_pressure_value);
        sbHighTemperature = (SeekBar)findViewById(R.id.sb_high_temperature);
        tvHighTemperatureValue = (TextView)findViewById(R.id.tv_high_temperature_value);
        
        //绑定Service  
        Intent intent = new Intent("com.ijidou.accessory.tirepress.TPMS_SERVICE");  
        bindService(intent, conn, Context.BIND_AUTO_CREATE); 
        
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        
        doChangeTheme();
        TpmsApplication.addThemeListener(this);
    }
    
    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        
        TpmsApplication.removeThemeListener(this);
    }
    
    @Override
    protected void onDestroy() {
        //注销车胎状态监听
        tpmsService.unRegisterTireWarningValueListener();
        unbindService(conn);
        super.onDestroy();
    }

    ServiceConnection conn = new ServiceConnection() {  
        @Override  
        public void onServiceDisconnected(ComponentName name) {  
  
        }  
          
        @Override  
        public void onServiceConnected(ComponentName name, IBinder service) {  
            //返回一个Service对象  
            tpmsService = ((TpmsService.TpmsBinder)service).getService();
            
            //注册车胎报警值监听
            tpmsService.registerTireWarningValueListener(mTireWarningValueListener);
            
            tpmsService.setHighPressureWarning(QUERY_HIGH_PRESSURE);
            tpmsService.setLowPressureWarning(QUERY_LOW_PRESSURE);
            tpmsService.setHighTemperatureWarning(QUERY_HIGH_TEMPERATURE);
        }
    };
    
    private ITireWarningValueListener mTireWarningValueListener = new ITireWarningValueListener() {
        
        @Override
        public void onHighPressure(double value) {
            tvHighPressureValue.setText(String.valueOf(value));
            int progress = (int)(value * 10) - 30;
            sbHighPressure.setProgress(progress);
        }
        
        @Override
        public void onLowPressure(double value) {
            tvLowPressureValue.setText(String.valueOf(value));
            int progress = (int)(value * 10) - 16;
            sbLowPressure.setProgress(progress);
        }
        
        @Override
        public void onHighTemperature(int value) {
            tvHighTemperatureValue.setText(String.valueOf(value));
            int progress = value - 60;
            sbHighTemperature.setProgress(progress);
        }
        
    };
    
    
    @Override
    public void doChangeTheme() {
        final boolean dayTheme = TpmsApplication.isDayTheme;

        runOnUiThread(new Runnable() {
            
            @Override
            public void run() {
                // TODO Auto-generated method stub
                if (dayTheme) {
                    
                } else {
                    
                }
            }
        });
    }

    @Override
    public void onProgressChanged(SeekBar arg0, int arg1, boolean fromUser) {
        if(fromUser){
            switch (arg0.getId()) {
            case R.id.sb_high_pressure:
                double valueHighPressure = ((double)(arg0.getProgress() + 30)) / 10;
                tvHighPressureValue.setText(String.valueOf(valueHighPressure));
                break;
            case R.id.sb_low_pressure:
                double valueLowPressure = ((double)(arg0.getProgress() + 16)) / 10;
                tvLowPressureValue.setText(String.valueOf(valueLowPressure));
                break;
            case R.id.sb_high_temperature:
                int valueHighTemperature = arg0.getProgress() + 60;
                tvHighTemperatureValue.setText(String.valueOf(valueHighTemperature));
                break;
            default:
                break;
            }
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar arg0) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onStopTrackingTouch(SeekBar arg0) {
        switch (arg0.getId()) {
        case R.id.sb_high_pressure:
            double valueHighPressure = ((double)(arg0.getProgress() + 30)) / 10;
            tvHighPressureValue.setText(String.valueOf(valueHighPressure));
            tpmsService.setHighPressureWarning(valueHighPressure);
            break;
        case R.id.sb_low_pressure:
            double valueLowPressure = ((double)(arg0.getProgress() + 16)) / 10;
            tvLowPressureValue.setText(String.valueOf(valueLowPressure));
            tpmsService.setLowPressureWarning(valueLowPressure);
            break;
        case R.id.sb_high_temperature:
            int valueHighTemperature = arg0.getProgress() + 60;
            tvHighTemperatureValue.setText(String.valueOf(valueHighTemperature));
            tpmsService.setHighTemperatureWarning(valueHighTemperature);
            break;
        default:
            break;
        }
    }
}
