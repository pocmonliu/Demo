package com.ijidou.accessory.tirepressure;

import com.ijidou.accessory.tirepress.common.CommonUtils;

public class TireState implements Cloneable{
        
        public static final int BIT_0 = 0x01;
        public static final int BIT_1 = 0x02;
        public static final int BIT_2 = 0x04;
        public static final int BIT_3 = 0x08;
        public static final int BIT_4 = 0x10;
        public static final int BIT_5 = 0x20;
        public static final int BIT_6 = 0x40;
        public static final int BIT_7 = 0x80;
       
        /**
         * 轮胎气压
         */
        public double pressure;
        
        /**
         * 轮胎温度
         */
        public int temperature;
        
        /**
         * 慢漏
         */
        public boolean slowLeak = false;
        
        /**
         * 快漏
         */
        public boolean quickLeak = false;
        
        /**
         * 传感器低电
         */
        public boolean sensorLowPower = false;
        
        /**
         * 轮胎过热
         */
        public boolean overHeating = false;
        
        /**
         * 轮胎过压
         */
        public boolean overPressure = false;
        
        /**
         * 轮胎欠压
         */
        public boolean lessPressure = false;
        
        /**
         * 传感器失效
         */
        public boolean sensorInvalid = false;
        
        public TireState() {
        }
        
        public TireState(byte[] data) {
            double pressure = CommonUtils.convertDouble(data[6]);  //轮胎气压
            this.pressure = pressure;
            int temperature = data[7] & 0x000000FF - 40;           //轮胎温度
            this.temperature = temperature;
            boolean slowLeak = ((BIT_0 & data[8]) == BIT_0);   //慢漏
            this.slowLeak = slowLeak;
            boolean quickLeak = ((BIT_1 & data[8]) == BIT_1);  //快漏
            this.quickLeak = quickLeak;
            boolean sensorLowPower = ((BIT_2 & data[8]) == BIT_2); //传感器低电
            this.sensorLowPower = sensorLowPower;
            boolean overHeating = ((BIT_3 & data[8]) == BIT_3);    //轮胎过热
            this.overHeating = overHeating;
            boolean overPressure = ((BIT_4 & data[8]) == BIT_4);   //轮胎过压
            this.overPressure = overPressure;
            boolean lessPressure = ((BIT_5 & data[8]) == BIT_5);   //轮胎欠压
            this.lessPressure = lessPressure;
            boolean sensorInvalid = ((BIT_7 & data[8]) == BIT_7);  //传感器失效
            this.sensorInvalid = sensorInvalid;
        }
        
        @Override
        public boolean equals(Object o) {
            if(this == o){
                return true;
            }
            
            if(o == null){
                return false;
            }
            
            if(!(o instanceof TireState)) {
                return false;
            }
            
            TireState tireState = (TireState) o;
            if(this.pressure == tireState.pressure
                    && this.temperature == tireState.temperature
                    && this.slowLeak == tireState.slowLeak
                    && this.quickLeak == tireState.quickLeak
                    && this.sensorLowPower == tireState.sensorLowPower
                    && this.overHeating == tireState.overHeating
                    && this.overPressure == tireState.overPressure
                    && this.lessPressure == tireState.lessPressure
                    && this.sensorInvalid == tireState.sensorInvalid){
                return true;
            }
            
            return false;
        }
        
        @Override
        protected Object clone() throws CloneNotSupportedException {
            // TODO Auto-generated method stub
            return super.clone();
        }
}
