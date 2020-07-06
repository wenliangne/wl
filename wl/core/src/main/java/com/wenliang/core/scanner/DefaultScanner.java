package com.wenliang.core.scanner;

import java.util.HashMap;
import java.util.Map;

/**
 * @author wenliang
 * @date 2020-07-06
 * 简介：
 */
public class DefaultScanner extends BaseScanner {

    private Map<String, Class> tempMap = new HashMap<>();
    private Class annotation;
    private ResourceType resourceType;

    public DefaultScanner(Class annotation) {
        this.annotation = annotation;
        this.resourceType = ResourceType.clazz;
    }

    public DefaultScanner(Class annotation, ResourceType resourceType) {
        this.annotation = annotation;
        this.resourceType = resourceType;
    }

    @Override
    protected void scanFile(String pathName) throws Exception {
        String path = trimExtension(pathName);
        try {
            Class<?> clazz = Class.forName(path);
            if (clazz.getAnnotation(annotation) != null) {
                tempMap.put(clazz.getName(), clazz);
            }
        } catch (Exception e) {
            log.warn("加载类失败：" + path);
        }

    }

    @Override
    protected ResourceType getFileType() {
        return this.resourceType;
    }

    public Map<String, Class> getResult() {
        return tempMap;
    }
}
