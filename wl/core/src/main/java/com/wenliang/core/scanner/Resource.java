package com.wenliang.core.scanner;

/**
 * @author wenliang
 * @date 2020-07-06
 * 简介：
 */
import java.io.File;
import java.io.Serializable;
import java.net.URL;

public class Resource implements Serializable {
    private static final long serialVersionUID = 1L;
    private ResourceType type;
    private String path;
    private File file;
    private URL url;

    public Resource() {
    }

    public ResourceType getType() {
        return this.type;
    }

    public void setType(ResourceType type) {
        this.type = type;
    }

    public String getPath() {
        return this.path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public File getFile() {
        return this.file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public URL getUrl() {
        return this.url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    public String toString() {
        return "Resource [type=" + this.type + ", path=" + this.path + ", file=" + this.file + ", url=" + this.url + "]";
    }
}
