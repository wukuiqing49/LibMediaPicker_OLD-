package com.wkq.media.utils;

/**
 * 作者: 吴奎庆
 * <p>
 * 时间: 2019/6/4
 * <p>
 * 简介:
 */
public class DoublePressed {
    private static long time_pressed;
    private static long upload_time;

    public DoublePressed() {
    }

    public static boolean onDoublePressed() {
        return onDoublePressed(false, 2000L);
    }

    public static boolean onLongDoublePressed() {
        return onDoublePressed(true, 2000L);
    }

    public static boolean onDoublePressed(boolean var0, long var1) {
        boolean var3 = time_pressed + var1 > System.currentTimeMillis();
        if (var0) {
            time_pressed = System.currentTimeMillis();
        } else if (upload_time < System.currentTimeMillis()) {
            upload_time = System.currentTimeMillis() + var1;
            time_pressed = System.currentTimeMillis();
        }

        return var3;
    }
}
