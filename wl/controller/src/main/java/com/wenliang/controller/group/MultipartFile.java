package com.wenliang.controller.group;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class MultipartFile {
    private final String name;
    private final String contentType;
    private byte[] bytes;

    public MultipartFile(String name, String contentType, byte[] bytes) {
        this.name = name;
        this.contentType = contentType;
        this.bytes = bytes;
    }

    public MultipartFile(String name, String contentType) {
        this.name = name;
        this.contentType = contentType;
        this.bytes = new byte[0];
    }

    public void addBytes(byte[] tempByte2, int len) {
        byte[] tempByte1 = this.bytes;
        this.bytes = new byte[tempByte1.length + len];
        System.arraycopy(tempByte1, 0, this.bytes, 0, tempByte1.length);
        System.arraycopy(tempByte2, 0, this.bytes, tempByte1.length, len);
    }

    public String getName() {
        return this.name;
    }

    public String getContentType() {
        return this.contentType;
    }

    public byte[] getBytes() {
        return this.getBytes();
    }

    public long getSize() {
        return this.bytes.length;
    }

    public InputStream getInputStream() {
        return new ByteArrayInputStream(this.bytes);
    }

    public void transfer(File file) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            fos.write(this.bytes);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("文件存储异常！");
        }finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (Exception e) {
                }
            }
        }
    }

    public void transfer(String path) {
        this.transfer(new File(path));
    }
}
