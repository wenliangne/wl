package com.wenliang.controller.group;

import java.util.Map;

public class ModelAndView extends Model {
    String path;

    public ModelAndView(Map<String, Object> map) {
        super(map);
    }
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
