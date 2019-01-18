package com.sc.event.subscriber.utils;

import android.support.annotation.Nullable;

/**
 * created by 2018/10/16 下午10:57
 *
 * @author Ryan
 */
public class ObjectUtil {

    public static boolean equal(@Nullable Object a, @Nullable Object b) {
        return a == b || (a != null && a.equals(b));
    }
}
