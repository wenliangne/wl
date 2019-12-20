package com.wenliang.controller.group;

import java.util.Map;
import java.util.Set;

public class Model {
    private Map<String, Object> map;

    public Model(Map<String, Object> map) {
        this.map = map;
    }

    public Object get(String key) {
        return this.map.get(key);
    }

    public Object put(String key, String value) {
        return this.map.put(key, value);
    }

    public Object putIfAbsent(String key, String value) {
        return this.map.putIfAbsent(key,value);
    }

    public void putAll(Map map) {
        this.map.putAll(map);
    }
    public Set<String> keySet() {
        return this.map.keySet();
    }

    public boolean containsKey(String key) {
        return this.map.containsKey(key);
    }

    public boolean containsValue(String value) {
        return this.map.containsValue(value);
    }

    public Object remove(String key) {
        return this.map.remove(key);
    }

    public Object replace(String key,Object value) {
        return this.map.replace(key,value);
    }

    public boolean size(String key,Object oldValue,Object newValue) {
        return this.map.replace(key, oldValue, newValue);
    }

    public int size() {
        return this.map.size();
    }



}
