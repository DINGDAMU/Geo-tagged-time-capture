package com.example.dingdamu.ding;

/**
 * Created by dingdamu on 25/05/16.
 */
public class Utils {
    private static long lastClickTime;
    final static int clicktime=10000;
    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if ( 0 < timeD && timeD < clicktime) {
            return true;
        }
        lastClickTime = time;
        return false;
    }
}
