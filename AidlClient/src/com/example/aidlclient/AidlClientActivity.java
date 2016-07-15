package com.example.aidlclient;

import com.example.aidlservice.IAidlService;

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

public class AidlClientActivity extends Activity {

    private IAidlService mAidlService;
    private Intent intent;
    
    private TextView tvPlus;
    private TextView tvUpper;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aidl_client);
        
        tvPlus = (TextView) findViewById(R.id.tv_plus);
        tvUpper = (TextView) findViewById(R.id.tv_upper);
        
        tvPlus.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if(mAidlService != null){
                    try {
                        int result = mAidlService.plus(3, 3);
                        tvPlus.setText(String.valueOf(result));
                    } catch (RemoteException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        });
        
        tvUpper.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                try {
                    String text = "hello world!!!";
                    String upperText;
                    upperText = mAidlService.toUpperCase(text);
                    tvUpper.setText(upperText);
                } catch (RemoteException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
                             
        intent = new Intent("com.example.aidlservice.ServiceImpl");
        bindService(intent, connection, BIND_AUTO_CREATE);
        
    }

    private ServiceConnection connection = new ServiceConnection() {
        
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
        unbindService(connection);
    }
}
