package com.ijidou.accessory.tirepressure;

import com.ijidou.accessory.tirepress.R;
import com.ijidou.accessory.tirepress.common.IDoubleClickListener;
import com.ijidou.accessory.tirepress.common.ITireMatchListener;
import com.ijidou.accessory.tirepress.common.ITireStateListener;
import com.ijidou.accessory.tirepress.common.ThemeListener;
import com.ijidou.accessory.tirepress.controls.ConfirmDialog;
import com.ijidou.accessory.tirepress.controls.DoubleClickLayout;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class TpmsTireStateActivity extends Activity implements ThemeListener{
    private final static String TAG = "Tpms: TpmsActivity";
    
    private TpmsService tpmsService = null;  
    
    private Button btnSet;
    private DoubleClickLayout tireFrontLeft;
    private TextView tvFrontLeftPressure;
    private TextView tvFrontLeftTemperature;
    private DoubleClickLayout tireFrontRight;
    private TextView tvFrontRightPressure;
    private TextView tvFrontRightTemperature;
    private DoubleClickLayout tireRearLeft;
    private TextView tvRearLeftPressure;
    private TextView tvRearLeftTemperature;
    private DoubleClickLayout tireRearRight;
    private TextView tvRearRightPressure;
    private TextView tvRearRightTemperature;
    
    private ConfirmDialog mConfirmDialog;
    
    public class TireViewHolder {
        TextView pressure;
        TextView temperature;
        
        public TireViewHolder(TextView pressure, TextView temperature){
            this.pressure = pressure;
            this.temperature = temperature;
        }
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_tire_press);
    
        btnSet = (Button)findViewById(R.id.btn_set);
        tireFrontLeft = (DoubleClickLayout)findViewById(R.id.tv_front_left_tire);
        tvFrontLeftPressure = (TextView)findViewById(R.id.tv_front_left_pressure);
        tvFrontLeftTemperature = (TextView)findViewById(R.id.tv_front_left_temperature);
        tireFrontRight = (DoubleClickLayout)findViewById(R.id.tv_front_right_tire);
        tvFrontRightPressure = (TextView)findViewById(R.id.tv_front_right_pressure);
        tvFrontRightTemperature = (TextView)findViewById(R.id.tv_front_right_temperature);
        tireRearLeft = (DoubleClickLayout)findViewById(R.id.tv_rear_left_tire);
        tvRearLeftPressure = (TextView)findViewById(R.id.tv_rear_left_pressure);
        tvRearLeftTemperature = (TextView)findViewById(R.id.tv_rear_left_temperature);
        tireRearRight = (DoubleClickLayout)findViewById(R.id.tv_rear_right_tire);
        tvRearRightPressure = (TextView)findViewById(R.id.tv_rear_right_pressure);
        tvRearRightTemperature = (TextView)findViewById(R.id.tv_rear_right_temperature);
        
        tireFrontLeft.setTag(new TireViewHolder(tvFrontLeftPressure, tvFrontLeftTemperature));
        tireFrontRight.setTag(new TireViewHolder(tvFrontRightPressure, tvFrontRightTemperature));
        tireRearLeft.setTag(new TireViewHolder(tvRearLeftPressure, tvRearLeftTemperature));
        tireRearRight.setTag(new TireViewHolder(tvRearRightPressure, tvRearRightTemperature));
        
        //绑定Service  
        Intent intent = new Intent("com.ijidou.accessory.tirepress.TPMS_SERVICE");  
        bindService(intent, conn, Context.BIND_AUTO_CREATE);  
        
        btnSet.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TpmsTireStateActivity.this, TpmsSettingsActivity.class);
                startActivity(intent);
                
            }
        });
        
        tireFrontLeft.setOnDoubleClickListener(new IDoubleClickListener() {
            
            @Override
            public void onDoubleClick() {
                if(tpmsService.getCarSpeed() > 0 ){
                    Toast.makeText(TpmsTireStateActivity.this, "车速不为0，不能配对", Toast.LENGTH_SHORT).show();
                    return;
                }
                
                if(tpmsService.isMatching()){
                    tpmsService.cancelMatch();
                    showConfirmDialog(TpmsService.FRONT_LEFT);
                }else{
                    tpmsService.requestMatch(TpmsService.FRONT_LEFT);
                }

            }
        });
        
        tireFrontRight.setOnDoubleClickListener(new IDoubleClickListener() {
            
            @Override
            public void onDoubleClick() {
                if(tpmsService.getCarSpeed() > 0 ){
                    Toast.makeText(TpmsTireStateActivity.this, "车速不为0，不能配对", Toast.LENGTH_SHORT).show();
                    return;
                }
                
                if(tpmsService.isMatching()){
                    tpmsService.cancelMatch();
                    showConfirmDialog(TpmsService.FRONT_RIGHT);
                }else{
                    tpmsService.requestMatch(TpmsService.FRONT_RIGHT);
                }

            }
        });
        
        tireRearLeft.setOnDoubleClickListener(new IDoubleClickListener() {
            
            @Override
            public void onDoubleClick() {
                if(tpmsService.getCarSpeed() > 0 ){
                    Toast.makeText(TpmsTireStateActivity.this, "车速不为0，不能配对", Toast.LENGTH_SHORT).show();
                    return;
                }
                
                if(tpmsService.isMatching()){
                    tpmsService.cancelMatch();
                    showConfirmDialog(TpmsService.REAR_LEFT);
                }else{
                    tpmsService.requestMatch(TpmsService.REAR_LEFT);
                }
                
            }
        });
        
        tireRearRight.setOnDoubleClickListener(new IDoubleClickListener() {
            
            @Override
            public void onDoubleClick() {
                if(tpmsService.getCarSpeed() > 0 ){
                    Toast.makeText(TpmsTireStateActivity.this, "车速不为0，不能配对", Toast.LENGTH_SHORT).show();
                    return;
                }
                
                if(tpmsService.isMatching()){
                    tpmsService.cancelMatch();
                    showConfirmDialog(TpmsService.REAR_RIGHT);
                }else{
                    tpmsService.requestMatch(TpmsService.REAR_RIGHT);
                }
                
            }
        });
        
    }

    /**
     * 倒车结果确认框
     */
    public void showConfirmDialog(final byte position) {
        
        if(mConfirmDialog != null){
            mConfirmDialog.dismiss();
        }
        mConfirmDialog = new ConfirmDialog (this);
        mConfirmDialog .setCanceledOnTouchOutside( false);
        mConfirmDialog .show();

        mConfirmDialog.tvYes.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mConfirmDialog.dismiss();
                disableAllTireView();
                tpmsService.tireChange(position);
            }
        });

        mConfirmDialog.tvNo.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mConfirmDialog.dismiss();
                
                Toast.makeText(TpmsTireStateActivity.this, "取消交换", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        
        doChangeTheme();
        TpmsApplication.addThemeListener(this);
        
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        
        TpmsApplication.removeThemeListener(this);
    }
    
    @Override
    protected void onDestroy() {
        //注销车胎状态监听
        tpmsService.unRegisterTireStateListener();
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
            
            //注册车胎状态监听
            tpmsService.registerTireStateListener(tireStateListener);
            //注册车胎配对监听
            tpmsService.registerTireMatchListener(tireMatchListener);
        }
    }; 
    
    private ITireStateListener tireStateListener = new ITireStateListener() {

        @Override
        public void onFrontLeft(TireState tireState) {
            updateTireStateUI((TireViewHolder) tireFrontLeft.getTag(), tireState);            
        }

        @Override
        public void onFrontRight(TireState tireState) {
            updateTireStateUI((TireViewHolder) tireFrontRight.getTag(), tireState);
        }

        @Override
        public void onRearLeft(TireState tireState) {
            updateTireStateUI((TireViewHolder) tireRearLeft.getTag(), tireState);
        }

        @Override
        public void onRearRight(TireState tireState) {
            updateTireStateUI((TireViewHolder) tireRearRight.getTag(), tireState);            
        }

    };
    
    private ITireMatchListener tireMatchListener = new ITireMatchListener() {
        
        @Override
        public void onResult(byte position, byte result) {
            switch (result) {
            case 0x00:
                Toast.makeText(TpmsTireStateActivity.this, "进入匹配", Toast.LENGTH_SHORT).show();
                break;
            case 0x01:
                Toast.makeText(TpmsTireStateActivity.this, "匹配成功", Toast.LENGTH_SHORT).show();
                break;
            case 0x02:
                Toast.makeText(TpmsTireStateActivity.this, "匹配失败或超时退出", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
            }
        }

        @Override
        public void onChanged() {
            enableAllTireView();
        }
    };
    
    private void enableAllTireView(){
        tireFrontLeft.setClickable(true);
        tireFrontRight.setClickable(true);
        tireRearLeft.setClickable(true);
        tireRearRight.setClickable(true);
    }
    
    private void disableAllTireView(){
        tireFrontLeft.setClickable(false);
        tireFrontRight.setClickable(false);
        tireRearLeft.setClickable(false);
        tireRearRight.setClickable(false);
    }
    
    private void updateTireStateUI(final TireViewHolder holderTireView, final TireState tireState){
        runOnUiThread(new Runnable() {
            
            @Override
            public void run() {
                if(tireState.pressure == 0){
                    holderTireView.pressure.setText("--");
                    holderTireView.temperature.setText("--");
                }else{
                    holderTireView.pressure.setText(tireState.pressure + "Bar");
                    holderTireView.temperature.setText(tireState.temperature + "℃");
                }
            }
        });

    }
    
    @Override
    public void doChangeTheme() {
        final boolean dayTheme = TpmsApplication.isDayTheme;

        runOnUiThread(new Runnable() {
            
            @Override
            public void run() {
                if (dayTheme) {
                    
                } else {
                    
                }
            }
        });
        
    }
    
}
