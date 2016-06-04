package com.ijidou.accessory.tirepress.common;

public interface ITireWarningValueListener {
    /**
     * 高压报警值
     */
    void onHighPressure(double value);
    /**
     * 低压报警值
     */
    void onLowPressure(double value);
    /**
     * 高温报警值
     */
    void onHighTemperature(int value);
}
