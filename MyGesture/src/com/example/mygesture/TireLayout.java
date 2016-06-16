package com.example.mygesture;

import org.w3c.dom.Text;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TireLayout extends RelativeLayout{

    private TextView tvOperation;
    private TextView tvPosition;
    
    public TireLayout(Context context) {
        super(context);
        // 导入布局  
        LayoutInflater.from(context).inflate(R.layout.tire_layout, this, true);  
        tvOperation = (TextView) findViewById(R.id.tv_operation);  
        tvPosition = (TextView) findViewById(R.id.tv_position); 
    }

    public TireLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    public TireLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // TODO Auto-generated constructor stub
    }
    
    /** 
     * 设置图片资源 
     */  
    public void setOperation(String operation) {  
        tvOperation.setText(operation);  
    }  
  
    /** 
     * 设置显示的文字 
     */  
    public void setPosition(String position) {  
        tvPosition.setText(position);  
    } 

}
