package com.wenliang.test.proxy;

import com.wenliang.context.annotations.Aspect;
import com.wenliang.context.annotations.AspectAfter;
import com.wenliang.context.annotations.AspectBefore;

import javax.servlet.http.HttpServletRequest;

/**
 * @author wenliang
 * @date 2019-07-31
 * 简介：
 */


@Aspect
public class TestAspect {

    @AspectBefore("public void com.wenliang.test.service..*(*)")
    public void testAspectAaa() {
        System.out.println("执行了：TestAspect:AspectBefore:testAspectAaa");
    }

    @AspectAfter("public void com.wenliang.test.service..*(*)")
    public void testAspectBbb() {
        System.out.println("执行了：TestAspect:AspectAfter:testAspectBbb");
    }
    @AspectBefore("public void com.wenliang.test.service..*(*)")
    public void testAspectCcc() {
        System.out.println("执行了：TestAspect:AspectBefore:testAspectCcc");
    }
    @AspectBefore("public void com.wenliang.test.service..*(*)")
    public void testAspectDdd(HttpServletRequest request) {
        System.out.println("执行了：TestAspect:AspectBefore:testAspectDdd，并获取了request："+request);
    }
}
