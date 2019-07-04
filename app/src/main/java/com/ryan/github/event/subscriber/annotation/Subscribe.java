package com.ryan.github.event.subscriber.annotation;

import com.ryan.github.event.subscriber.ThreadMode;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * created by 2018/10/16 下午5:12
 *
 * @author Ryan
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface Subscribe {

    /**
     * 执行线程 {@link com.ryan.github.event.subscriber.ThreadMode}
     */
    int threadMode() default ThreadMode.POSTER;

    int priority() default 0;

    boolean sticky() default false;
}
