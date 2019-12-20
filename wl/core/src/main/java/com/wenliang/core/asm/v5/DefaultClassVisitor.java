package com.wenliang.core.asm.v5;

import com.wenliang.core.asm.VisitMethodMatcher;
import org.kohsuke.asm5.ClassVisitor;
import org.kohsuke.asm5.MethodVisitor;
import org.kohsuke.asm5.Opcodes;

/**
 * @author wenliang
 * @date 2019-08-08
 * 简介：
 */
public class DefaultClassVisitor extends ClassVisitor {

    @Override
    public MethodVisitor visitMethod(int i, String s, String s1, String s2, String[] strings) {
        VisitMethodMatcher matcher = new VisitMethodMatcher(s, s1);
        if (matcher.isMatchMethod(MethodParameterNamesScanner.tempMethod)) {
            return new DefaultMethodVisitor(Opcodes.ASM5);
        }else {
            return super.visitMethod(i, s, s1, s2, strings);
        }
    }

    public DefaultClassVisitor(int i) {
        super(i);
    }
}
