package com.wenliang.test.proxy;

import com.wenliang.context.annotations.*;

/**
 * @author wenliang
 * @date 2019-08-01
 * 简介：
 */
@Aspect
public class TestAspect2 {

    @AspectBefore("* com.wenliang.test.controller..*(*)")
    public void aaa() {
        System.out.println("执行了：TestAspect2:AspectBefore:aaa");
    }

    @AspectAfter("* com.wenliang.test.controller..*(*)")
    public void bbb() {
        System.out.println("执行了：TestAspect2:AspectAfter:bbb");
    }

    @AspectThrowing("* com.wenliang.test.controller..*(*)")
    public void ccc() {
        System.out.println("执行了：TestAspect2:AspectThrowing:ccc");
    }

    @AspectFinally("* com.wenliang.test.controller..*(*)")
    public void ddd() {
        System.out.println("执行了：TestAspect2:AspectFinally:ddd");
    }

    @AspectThrowing("* com.wenliang.test.controller..*(*)")
    public void eee(Exception e) {
        System.out.println("执行了：TestAspect2:AspectThrowing:eee");
        System.out.println(e.getMessage());
    }
}
