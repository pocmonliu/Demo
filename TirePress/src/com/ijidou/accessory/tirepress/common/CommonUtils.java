package com.ijidou.accessory.tirepress.common;

public class CommonUtils {
    public static double convertDouble(byte byte0){
        int partDecimal =  byte0 & 0x0000000F; 
        int partInteger = (byte0 >> 4) & 0x0000000F;
        Double value = Double.parseDouble(partInteger + "." + partDecimal);
        return value.doubleValue();
    }
}
