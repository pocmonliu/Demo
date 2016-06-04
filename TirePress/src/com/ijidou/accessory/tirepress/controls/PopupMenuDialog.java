package com.ijidou.accessory.tirepress.controls;


import com.ijidou.accessory.tirepress.R;
import com.ijidou.accessory.tirepress.common.ThemeListener;
import com.ijidou.accessory.tirepressure.TpmsApplication;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;

public class PopupMenuDialog extends Dialog implements ThemeListener {
    
    private String TAG = "Tpms : PopupMenuDialog";
    
    private Context mContext;
    
//    private LinearLayout MenuList;
//    public TextView tvSingleDetect;

    private Handler uiHandler = new Handler();

    public PopupMenuDialog(Context context) {
        super(context, R.style.CustomDialog);
        setContentView(R.layout.dlg_popup_menu);
        
        mContext = context;
        
//        MenuList = (LinearLayout) findViewById(R.id.menu_list);
//        tvSingleDetect = (TextView) findViewById(R.id.tv_single_detection);

        doChangeTheme();
        TpmsApplication.addThemeListener(this);
    }
    
    public void setItemTitle(String title){
//        tvSingleDetect.setText(title);
    }
    
//    public int getMenuWidth(){
//        return MenuList.getMeasuredWidth();
//    }
//    
//    public int getMenuHeight(){
//        return MenuList.getMeasuredHeight();
//    }
    
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