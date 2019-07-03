package com.ryan.github.event.subscriber.utils;

/**
 * created by 2018/10/16 下午10:48
 *
 * @author Ryan
 */
public class HashCodeUtil {

    private static final int X = 31;

    public static int hashCode(Object o1, Object o2) {
        int acc = X + o1.hashCode();
        acc = X * acc + o2.hashCode();
        return acc;
    }

    public static int hashCode(Object o1, Object o2, Object o3, Object o4) {
        int acc = X + o1.hashCode();
        acc = X * acc + o2.hashCode();
        acc = X * acc + o3.hashCode();
        acc = X * acc + o4.hashCode();
        return acc;
    }

}
