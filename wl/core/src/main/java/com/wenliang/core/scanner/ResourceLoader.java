package com.wenliang.core.scanner;

/**
 * @author wenliang
 * @date 2020-07-06
 * 简介：
 */
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.Set;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ResourceLoader {
    private Log log = LogFactory.getLog(this.getClass());

    public ResourceLoader() {
    }

    public void loadResource(String path, Set<Resource> result) {
        if (path.startsWith("/")) {
            path = path.substring(1);
        }

        try {
            ClassLoader cl = ResourceLoader.class.getClassLoader();
            Enumeration<URL> resourceUrls = cl.getResources(path);
            if (resourceUrls == null || !resourceUrls.hasMoreElements()) {
                resourceUrls = ClassLoader.getSystemResources(path);
            }

            while(resourceUrls.hasMoreElements()) {
                URL url = (URL)resourceUrls.nextElement();
                this.log.debug(url.toString());
                String urlPath = null;

                try {
                    urlPath = url.toURI().getRawSchemeSpecificPart();
                } catch (Exception var11) {
                    this.log.warn("to uri error", var11);
                    urlPath = url.getPath();
                    this.log.warn("get path urlPath:" + urlPath);
                }

                if (urlPath.indexOf("%") >= 0) {
                    try {
                        urlPath = URLDecoder.decode(urlPath, "utf-8");
                    } catch (Exception var10) {
                        this.log.warn("URLDecoder.decode error.", var10);
                    }
                }

                this.log.warn(urlPath);
                if (this.isDirectory(urlPath)) {
                    this.parseDirectory(path, urlPath, result);
                } else {
                    String subFloder = this.getSubFolder(url);
                    Resource res = new Resource();
                    res.setType(this.getResourceType(url.toString()));
                    String subPath = path;
                    if (!path.endsWith("/")) {
                        subPath = path + "/";
                    }

                    res.setPath(subPath + subFloder);
                    res.setUrl(url);
                    res.setFile(new File(url.toString()));
                    result.add(res);
                }
            }

        } catch (IOException var12) {
            throw new RuntimeException("加载资源出错", var12);
        } catch (Exception var13) {
            throw new RuntimeException("加载资源出错", var13);
        }
    }

    private boolean isDirectory(URL url) {
        File file = new File(url.getFile());
        return file.isDirectory();
    }

    private boolean isDirectory(String url) {
        File file = new File(url);
        return file.isDirectory();
    }

    private void parseDirectory(String path, String url, Set<Resource> result) throws IOException {
        File file = new File(url);
        File[] files = file.listFiles();
        if (!ArrayUtils.isEmpty(files)) {
            File[] arr$ = files;
            int len$ = files.length;

            for(int i$ = 0; i$ < len$; ++i$) {
                File f = arr$[i$];
                if (f.isDirectory()) {
                    if (!path.endsWith("/")) {
                        this.loadResource(path + "/" + f.getName(), result);
                    } else {
                        this.loadResource(path + f.getName(), result);
                    }
                } else {
                    URL fileUrl = f.toURI().toURL();
                    Resource res = new Resource();
                    res.setFile(f);
                    res.setType(this.getResourceType(f.getName()));
                    res.setPath(path);
                    res.setUrl(fileUrl);
                    result.add(res);
                }
            }

        }
    }

    private ResourceType getResourceType(String fileName) {
        if (fileName.toLowerCase().endsWith(".class")) {
            return ResourceType.clazz;
        } else {
            return fileName.toLowerCase().endsWith(".xml") ? ResourceType.xml : null;
        }
    }

    private String getSubFolder(URL url) {
        int start = url.toString().lastIndexOf("/");
        String subFloder = url.toString().substring(start + 1);
        return subFloder;
    }
}
