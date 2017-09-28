package com.example.za;

import com.example.za.And7z.And7zCallback;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends Activity {
    private final static String TAG = "And7z";
    //private String cmd = "/data/user/7za x /data/user/update.zip -o/data/user/ -p123456 -aoa -bsp1";
    
    private TextView tvInfo;
    private TextView tvProgress;
    
    private And7z mAnd7z;
    
    private And7zCallback mAnd7zCallback = new And7zCallback(){
        
        @Override
        public void onProgress(int percent) {
            tvProgress.setText(String.valueOf(percent));
            Log.d(TAG, percent + "%");
        }

        @Override
        public void onResult(int result, String message) {
            if(result == And7z.AND_7Z_FAILED) {
                
            } else if(result == And7z.AND_7Z_DETAIL) {
                
            } else if(result == And7z.AND_7Z_SUCCEED) {
                
            }
            
            Log.d(TAG, result + ": " + message);
            tvInfo.setText(result + ": " + message);
        }
        
    };
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        tvInfo = (TextView)findViewById(R.id.tv_info);
        tvProgress = (TextView)findViewById(R.id.tv_progress);
        
        mAnd7z = new And7z();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        
        new Thread(new Runnable() {
            
            @Override
            public void run() {
                String pwd = "123456";
                
                String srcPath1 = "/sdcard/update_pwd.zip";
                String dstPath1 = "/sdcard/";
                mAnd7z.And7zDecompression(srcPath1, dstPath1, pwd, mAnd7zCallback);
                
//                String srcPath2 = "/sdcard/update.zip";
//                String dstPath2 = "/sdcard/update_pwd.zip";
//                mAnd7z.And7zCompression(srcPath2, dstPath2, pwd, mAnd7zCallback);
            }
        }).start();
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
