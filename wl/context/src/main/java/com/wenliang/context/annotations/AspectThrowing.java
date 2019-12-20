package com.wenliang.context.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author wenliang
 * @date 2019-07-31
 * 简介：方法可使用Exception对象作为参数接受异常并处理
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AspectThrowing {
    String value();
}
