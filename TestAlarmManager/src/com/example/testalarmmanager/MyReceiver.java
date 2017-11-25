package com.example.testalarmmanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class MyReceiver extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        Log.d("MyTag", "onclock......................");  
        String msg = intent.getStringExtra("msg");  
        Toast.makeText(context,msg,Toast.LENGTH_SHORT).show(); 
    }

}
