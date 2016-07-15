package com.example.aidlservice;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class AidlActivity extends Activity {

    private TextView tvPlus;
    private TextView tvUppper;
    
    private Intent intent;
    private IAidlService mAidlService;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aidl);
        
        tvPlus = (TextView)findViewById(R.id.tv_plus);
        tvUppper = (TextView) findViewById(R.id.tv_upper);
        
        tvPlus.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if(mAidlService != null){
                    try {
                        int sum = mAidlService.plus(4, 5);
                        tvPlus.setText(String.valueOf(sum));
                    } catch (RemoteException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        });
        
        tvUppper.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if(mAidlService != null){
                    try {
                        String text = "hello world !!!";
                        String upperText = mAidlService.toUpperCase(text);
                        tvUppper.setText(upperText);
                    } catch (RemoteException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        });
        
        intent = new Intent("com.example.aidlservice.ServiceImpl");
        startService(intent);
        bindService(intent, conn, BIND_AUTO_CREATE);
        
    }
    
    private ServiceConnection conn = new ServiceConnection() {
        
        @Override
        public void onServiceDisconnected(ComponentName name) {
            // TODO Auto-generated method stub
            
        }
        
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // TODO Auto-generated method stub
            mAidlService = IAidlService.Stub.asInterface(service);
        }
    };
    
    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        unbindService(conn);
//        stopService(intent);
    }

}
