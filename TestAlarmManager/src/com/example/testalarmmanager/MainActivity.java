package com.example.testalarmmanager;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        //AlarmManager对象,注意这里并不是new一个对象，Alarmmanager为系统级服务  
        AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);    
          
        //创建Intent对象，action为ELITOR_CLOCK，附加信息为字符串“...I am still alive...”  
        Intent intent = new Intent("ELITOR_CLOCK");  
        intent.putExtra("msg","...I am still alive...");    
          
        //定义一个PendingIntent对象，PendingIntent.getBroadcast包含了sendBroadcast的动作。  
        //也就是发送了action 为"ELITOR_CLOCK"的intent   
        PendingIntent pi = PendingIntent.getBroadcast(this,0,intent,0);    
        
        //设置闹钟从当前时间开始，每隔5s执行一次PendingIntent对象pi，注意第一个参数与第二个参数的关系  
        // 5秒后通过PendingIntent pi对象发送广播  
        am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), 20*1000, pi); 
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
