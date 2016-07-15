package com.example.startotherapp;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class StartOtherActivity extends Activity {

    private Button btnStartApp;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_other);
        btnStartApp = (Button) findViewById(R.id.tv_start);
        btnStartApp.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                //方法一  
                Intent intent=new Intent();  
                //包名 包名+类名（全路径）  
                intent.setClassName("com.ijidou.accessory.tirepressure", "com.ijidou.accessory.tirepressure.TpmsTireStateActivity");  
                startActivity(intent);  
                
                //方法二  
//                Intent intent = new Intent();  
//                ComponentName comp = new ComponentName("com.ijidou.accessory.tirepressure","com.ijidou.accessory.tirepressure.TpmsTireStateActivity");  
//                intent.setComponent(comp);  
//                intent.setAction("android.intent.action.MAIN");  
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
//                startActivity(intent);    
            }
        });
    }


}
