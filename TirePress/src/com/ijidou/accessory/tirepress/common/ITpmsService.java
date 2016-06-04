package com.ijidou.accessory.tirepress.common;

/**
 * ITpmsService
 * @author Administrator
 *
 */
public interface ITpmsService {

        /**
         * 高压报警值设置
         * @param cmd
         * @param value
         */
        public void setHighPressureWarning(double value);
        
        /**
         * 低压报警值设置
         * @param cmd
         * @param value
         */
        public void setLowPressureWarning(double value);
        
        /**
         * 高温报警值设置
         * @param cmd
         * @param value
         */
        public void setHighTemperatureWarning(int value);

        /**
         * 休眠设置
         * @param cmd
         * @param value
         */
        public void setSleepSetting(byte data);

}
