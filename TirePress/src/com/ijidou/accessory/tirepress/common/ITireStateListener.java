package com.ijidou.accessory.tirepress.common;

import com.ijidou.accessory.tirepressure.TireState;

public interface ITireStateListener {
    /**
     * 前左车胎状态
     * @param tireState
     */
    void onFrontLeft(TireState tireState);
    /**
     * 前右车胎状态
     * @param tireState
     */
    void onFrontRight(TireState tireState);
    /**
     * 后左车胎状态
     * @param tireState
     */
    void onRearLeft(TireState tireState);
    /**
     * 后右车胎状态
     * @param tireState
     */
    void onRearRight(TireState tireState);
}
