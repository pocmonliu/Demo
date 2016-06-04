package com.ijidou.accessory.tirepress.common;

public interface ITireMatchListener {
    /**
     * 配对状态
     * @param position
     * @param result
     */
    void onResult(byte position, byte result);
    
    void onChanged();
}
