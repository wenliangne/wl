package com.wenliang.core.scanner;

/**
 * @author wenliang
 * @date 2020-07-06
 * 简介：
 */
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipException;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class BaseScanner implements Scanner {
    protected Log log = LogFactory.getLog(this.getClass());
    protected ResourceLoader resourceLoader = new ResourceLoader();
    protected ClassLoader classLoader = this.getClass().getClassLoader();
    protected String[] excludePaths;

    public BaseScanner() {
    }

    protected abstract void scanFile(String filePath) throws Exception;

    protected abstract ResourceType getFileType();

    public void doScan(String[] paths) throws Exception {
        String[] arr$ = paths;
        int len$ = paths.length;

        for(int i$ = 0; i$ < len$; ++i$) {
            String path = arr$[i$];
            this.log.info("扫描路径:" + path);

            try {
                this.doScan(path);
            } catch (Exception var7) {
                throw var7;
            }
        }

    }

    public boolean isExcludePath(String path) {
        if (this.excludePaths == null) {
            return false;
        } else if (StringUtils.isEmpty(path)) {
            return false;
        } else {
            if (path.indexOf("%") >= 0) {
                try {
                    path = URLDecoder.decode(path, "utf-8");
                } catch (Exception var8) {
                    this.log.warn("URLDecoder.decode error.", var8);
                }
            }

            boolean result = false;
            String[] arr$ = this.excludePaths;
            int len$ = arr$.length;

            for(int i$ = 0; i$ < len$; ++i$) {
                String excludePath = arr$[i$];
                String excludePathS = dotToSplash(excludePath);
                if (path.startsWith(excludePathS)) {
                    result = true;
                }
            }

            return result;
        }
    }

    public void doScan(String path) throws Exception {
        String splashPath = dotToSplash(path);
        Set<Resource> urls = new LinkedHashSet(64);
        this.resourceLoader.loadResource(splashPath, urls);
        Iterator i$ = urls.iterator();

        while(i$.hasNext()) {
            Resource res = (Resource)i$.next();
            if (res.getUrl() == null) {
                this.log.info("url is null" + res.toString());
            }

            boolean isJar = ResourceUtils.isJarURL(res.getUrl());
            if (isJar) {
                this.parseJarResources(res.getUrl());
            } else if (!this.isExcludePath(res.getPath())) {
                String urlPath = res.getUrl().getPath();
                if (urlPath.indexOf("%") >= 0) {
                    try {
                        urlPath = URLDecoder.decode(urlPath, "utf-8");
                    } catch (Exception var9) {
                        this.log.warn("URLDecoder.decode error.", var9);
                    }
                }

                if (this.getFileType() == ResourceType.clazz) {
                    String clazzName = res.getPath() + "/" + res.getFile().getName();
                    clazzName = clazzName.replace("/", ".");
                    this.scanFile(clazzName);
                } else if (this.getFileType() == ResourceType.xml) {
                    this.scanFile(urlPath);
                }
            }
        }

    }

    protected void scanJarFile(JarEntry entry) throws Exception {
        if (!entry.isDirectory()) {
            String name = entry.getName();
            if (!this.isExcludePath(name)) {
                String className = name.replace("/", ".");
                this.log.debug("加载:" + className);
                this.scanFile(className);
            }
        }
    }

    protected List<String> readFromDirectory(String path) {
        File file = new File(path);
        String[] names = file.list();
        return null == names ? null : Arrays.asList(names);
    }

    protected static String dotToSplash(String name) {
        return name.replaceAll("\\.", "/");
    }

    protected String getRootPath(URL url) {
        String fileUrl = url.getFile();

        try {
            fileUrl = URLDecoder.decode(fileUrl, "utf-8");
        } catch (UnsupportedEncodingException var4) {
            this.log.warn("URLDecoder.decode error.", var4);
        }

        int pos = fileUrl.indexOf(33);
        return -1 == pos ? fileUrl : fileUrl.substring(5, pos);
    }

    protected boolean isClassFile(String name) {
        return name.endsWith(".class");
    }

    protected String trimExtension(String name) {
        int pos = name.lastIndexOf(46);
        return -1 != pos ? name.substring(0, pos) : name;
    }

    protected void parseJarResources(URL url) throws Exception {
        URLConnection con = url.openConnection();
        JarFile jarFile;
        String jarFileUrl;
        String rootEntryPath;
        boolean closeJarFile;
        JarEntry entry;
        if (con instanceof JarURLConnection) {
            JarURLConnection jarCon = (JarURLConnection)con;
            ResourceUtils.useCachesIfNecessary(jarCon);
            jarFile = jarCon.getJarFile();
            jarFileUrl = jarCon.getJarFileURL().toExternalForm();
            entry = jarCon.getJarEntry();
            rootEntryPath = entry != null ? entry.getName() : "";
            closeJarFile = !jarCon.getUseCaches();
        } else {
            String urlFile = url.getFile();

            try {
                int separatorIndex = urlFile.indexOf("!/");
                if (separatorIndex != -1) {
                    jarFileUrl = urlFile.substring(0, separatorIndex);
                    rootEntryPath = urlFile.substring(separatorIndex + "!/".length());
                    jarFile = this.getJarFile(jarFileUrl);
                } else {
                    jarFile = new JarFile(urlFile);
                    rootEntryPath = "";
                }

                closeJarFile = true;
            } catch (ZipException var13) {
                this.log.info("Skipping invalid jar classpath entry [" + urlFile + "]");
                return;
            }
        }

        try {
            Enumeration entries = jarFile.entries();

            while(entries.hasMoreElements()) {
                entry = (JarEntry)entries.nextElement();
                String entryPath = entry.getName();
                if (entryPath.startsWith(rootEntryPath)) {
                    this.scanJarFile(entry);
                }
            }
        } finally {
            if (closeJarFile) {
                jarFile.close();
            }

        }

    }

    protected JarFile getJarFile(String jarFileUrl) throws IOException {
        if (jarFileUrl.startsWith("file:")) {
            try {
                return new JarFile(ResourceUtils.toURI(jarFileUrl).getSchemeSpecificPart());
            } catch (URISyntaxException var3) {
                return new JarFile(jarFileUrl.substring("file:".length()));
            }
        } else {
            return new JarFile(jarFileUrl);
        }
    }

    public ClassLoader getClassLoader() {
        return this.classLoader;
    }

    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public String[] getExcludePaths() {
        return this.excludePaths;
    }

    public void setExcludePaths(String[] excludePaths) {
        this.excludePaths = excludePaths;
    }
}
