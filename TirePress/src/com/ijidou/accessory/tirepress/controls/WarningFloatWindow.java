package com.ijidou.accessory.tirepress.controls;

import com.ijidou.accessory.tirepress.R;
import com.ijidou.accessory.tirepress.common.ThemeListener;
import com.ijidou.accessory.tirepressure.TpmsApplication;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class WarningFloatWindow extends LinearLayout implements ThemeListener {

    private static final String TAG = "Tpms:WarningFloatWindow: ";

    Context context;
    
    private Handler uiHandler = new Handler();

    private RelativeLayout warningLayout;    
    private ImageView ivWarning;
    private TextView tvWarning;
    
    public WarningFloatWindow(Context context) {
        super(context);
        this.context = context;

        LayoutInflater.from(context).inflate(R.layout.warning_float_window, this);
        warningLayout = (RelativeLayout)findViewById(R.id.warnig_bg);
        ivWarning = (ImageView)findViewById(R.id.iv_warning);
        tvWarning = (TextView)findViewById(R.id.tv_warning);
        
        doChangeTheme();
        TpmsApplication.addThemeListener(this);
    }
   
    public void updateWarningText(String text) {
        Log.d(TAG, text);
        tvWarning.setText(text);
    }
    
    public void onDestory(){
        TpmsApplication.removeThemeListener(this);
    }
    
    @Override
    public void doChangeTheme() {

        final boolean isDayTheme = TpmsApplication.isDayTheme;
        
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                
                if (isDayTheme) {
//                    MenuList.setBackgroundResource(R.drawable.menu_list_bg_d);
//                    tvSingleDetect.setTextColor(mContext.getResources().getColor(R.color.menu_list_text_d));
                } else {
//                    MenuList.setBackgroundResource(R.drawable.menu_list_bg_n);
//                    tvSingleDetect.setTextColor(mContext.getResources().getColor(R.color.menu_list_text_n));
                }
            }
        });
    }
    
}
