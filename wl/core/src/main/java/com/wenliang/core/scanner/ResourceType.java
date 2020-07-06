package com.wenliang.core.scanner;

/**
 * @author wenliang
 * @date 2020-07-06
 * 简介：
 */
import java.io.Serializable;

public enum ResourceType implements Serializable {
    xml,
    clazz,
    jar;

    private ResourceType() {
    }
}
