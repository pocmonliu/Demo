package com.ijidou.accessory.tirepress.controls;

import com.ijidou.accessory.tirepress.R;
import com.ijidou.accessory.tirepress.common.ThemeListener;
import com.ijidou.accessory.tirepressure.TpmsApplication;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ConfirmDialog extends Dialog implements ThemeListener {
    
    private String TAG = "Tpms : ConfirmDialog";
    
    private LinearLayout confirmBg;
    private TextView tvContent;
    public TextView tvYes;
    public TextView tvNo;

    private Context mContext;
    
    private Handler uiHandler = new Handler();

    public ConfirmDialog(Context context) {
        super(context, R.style.CustomDialog);
        setContentView(R.layout.dlg_confirm);
        getWindow().getAttributes().gravity = Gravity.CENTER;
        
        confirmBg = (LinearLayout) findViewById(R.id.confirm_bg);
        tvContent = (TextView) findViewById(R.id.tv_title);
        tvYes = (TextView) findViewById(R.id.tv_yes);
        tvNo = (TextView) findViewById(R.id.tv_no);
        
        doChangeTheme();
        TpmsApplication.addThemeListener(this);
    }
    
    @Override
    public void dismiss() {
        super.dismiss();
        TpmsApplication.removeThemeListener(this);
    }

    @Override
    public void doChangeTheme() {

        final boolean isDayTheme = TpmsApplication.isDayTheme;
        
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                if (isDayTheme) {
                    
                } else {
                    
                }
            }
        });

    }
}
