package com.example.coreplatetemperature;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ScrollView;
import android.widget.TextView;

public class MainActivity extends Activity {

    private static final String PATH = "/sys/devices/virtual/thermal/thermal_zone0/temp";
    private static final String CPU1_CUR_FREQ_PATH = "/sys/devices/system/cpu/cpu1/cpufreq/scaling_cur_freq";
    private static final String CPU1_MIN_FREQ_PATH = "/sys/devices/system/cpu/cpu1/cpufreq/scaling_min_freq";
    private static final String CPU1_MAX_FREQ_PATH = "/sys/devices/system/cpu/cpu1/cpufreq/scaling_max_freq";
    private static final String CPU4_CUR_FREQ_PATH = "/sys/devices/system/cpu/cpu4/cpufreq/scaling_cur_freq";
    private static final String CPU4_MIN_FREQ_PATH = "/sys/devices/system/cpu/cpu4/cpufreq/scaling_min_freq";
    private static final String CPU4_MAX_FREQ_PATH = "/sys/devices/system/cpu/cpu4/cpufreq/scaling_max_freq";
    private static String LOG_PATH = "";
    
    private ScrollView scrollview;
    private TextView tvTemperature;
    private Handler tickHandler;
    private String strTemperature;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        tvTemperature = (TextView) findViewById(R.id.tv_temperature);
        scrollview = (ScrollView)findViewById(R.id.scrollView);
        LOG_PATH = getTemperatureLogPath();
        createFile(LOG_PATH);
    }

    public static String getTemperatureLogPath(){
        return Environment.getExternalStorageDirectory().getAbsolutePath() + "/log_core_temperature";
    }
    
    public static boolean createFile(String destFileName) {
        File file = new File(destFileName);
        if (file.exists()) {          
            return true;
        }

        //创建目标文件
        try {
            if (file.createNewFile()) {                
                return true;
            } else {               
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();           
            return false;
        }
    }
    
    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        strTemperature = "";
        tickHandler = new Handler();
        tickHandler.post(tickRunnable);
    }
    
    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        tickHandler.removeCallbacks(tickRunnable);
    }
    
    private Runnable tickRunnable = new Runnable() {
        @Override
        public void run() {

//            SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");       
//            String date = sDateFormat.format(new Date(System.currentTimeMillis()));
            SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.getDefault());
            String date = sDateFormat.format(new Date());
            
            BufferedReader br;
            String strTemperatureNext = "-1";
            try {
                br = new BufferedReader(new FileReader(PATH));
                strTemperatureNext = br.readLine().trim();
                
                strTemperatureNext += "\n";
                br = new BufferedReader(new FileReader(CPU1_CUR_FREQ_PATH));                
                strTemperatureNext +="cur_freq: " + br.readLine().trim();
                br = new BufferedReader(new FileReader(CPU1_MIN_FREQ_PATH));
                strTemperatureNext +="; min_freq: " + br.readLine().trim();
                br = new BufferedReader(new FileReader(CPU1_MAX_FREQ_PATH));
                strTemperatureNext +="; max_freq: " + br.readLine().trim();
                
                strTemperatureNext += "\n";
                br = new BufferedReader(new FileReader(CPU4_CUR_FREQ_PATH));
                strTemperatureNext +="cur_freq: " + br.readLine().trim();
                br = new BufferedReader(new FileReader(CPU4_MIN_FREQ_PATH));
                strTemperatureNext +="; min_freq: " + br.readLine().trim();
                br = new BufferedReader(new FileReader(CPU4_MAX_FREQ_PATH));
                strTemperatureNext +="; max_freq: " + br.readLine().trim();
            } catch (Exception e) {
                e.printStackTrace();
                
            }
            String log_temp = date + "  核心板温度 " + strTemperatureNext + "\n";
            writeFileToLog(LOG_PATH, log_temp);
            
            if(strTemperature.length() > 1024*1024){
                strTemperature = "";
            }
            strTemperature  = strTemperature + log_temp;
            tvTemperature.setText(strTemperature);
            //scrollview.scrollTo(0, tvTemperature.getHeight());
            scrollview.fullScroll(ScrollView.FOCUS_DOWN);
            tickHandler.postDelayed(tickRunnable, 1000);
        }
    };
    
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
    
    
    public void writeFileToLog(String path, String log){
      File file = new File(path);
      if (!file.exists()) {
           return;
      }

      FileOutputStream fOut;
      try {
        fOut = new FileOutputStream(file, true);
        byte [] log_bytes = log.getBytes();
        fOut.write(log_bytes);
        fOut.close();
      } catch (Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }

    }
     
}
