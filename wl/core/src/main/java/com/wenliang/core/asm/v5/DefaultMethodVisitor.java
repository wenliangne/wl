package com.wenliang.core.asm.v5;


import org.kohsuke.asm5.Label;
import org.kohsuke.asm5.MethodVisitor;

/**
 * @author wenliang
 * @date 2019-08-08
 * 简介：
 */
public class DefaultMethodVisitor extends MethodVisitor {
    public DefaultMethodVisitor(int i) {
        super(i);
    }

    @Override
    public void visitLocalVariable(String s, String s1, String s2, Label label, Label label1, int i) {
        if (!"this".equals(s)&&i<MethodParameterNamesScanner.index) {
            MethodParameterNamesScanner.tempParameterNames.add(s);
        }
    }
}
