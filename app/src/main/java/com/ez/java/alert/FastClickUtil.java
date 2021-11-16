package com.ez.java.alert;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.view.View;

/**
 * @author : ezhuwx
 * Describe :
 * Designed on 2021/11/16
 * E-mail : ezhuwx@163.com
 * Update on 9:16 by ezhuwx
 */
class FastClickUtil {

    /**
     * 是否连点
     */
    public synchronized static boolean isNotFastClick(View view) {
        return isNotFastClick(view, 1000);
    }

    /**
     * 是否连点
     */
    public synchronized static boolean isNotFastClick(View view, long minInterval) {
        Long preTime = (Long) view.getTag(R.id.last_click_time);
        long currentTime = System.currentTimeMillis();
        if (preTime != null) {
            long timeInterval = currentTime - preTime;
            if (timeInterval < minInterval) {
                return false;
            }
            view.setTag(R.id.last_click_time, currentTime);
            return true;
        }
        view.setTag(R.id.last_click_time, currentTime);
        return true;
    }
}
