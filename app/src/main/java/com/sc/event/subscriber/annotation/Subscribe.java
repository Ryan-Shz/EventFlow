package com.sc.event.subscriber.annotation;

import com.sc.event.subscriber.ThreadMode;

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
     * 执行线程 {@link com.sc.event.subscriber.ThreadMode}
     */
    int threadMode() default ThreadMode.POSTER;

}
