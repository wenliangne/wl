package com.wenliang.security.encrypt;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author wenliang
 * @date 2019-12-24
 * 简介：
 */
public class Md5Encrypt implements Encrypt {
    private MessageDigest md5;
    public Md5Encrypt() {
        try {
            md5=MessageDigest.getInstance("md5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
    public String encrypt(String password) {
        md5.update(password.getBytes());
        byte[] digest = md5.digest();
        return fromByteToHex(digest);
    }
    private static String fromByteToHex(byte[] result){
        int num;
        StringBuffer sb=new StringBuffer();
        for (int i = 0; i < result.length; i++) {
            num = result[i];
            if (num < 0) {
                num = result[i] & 0xff;
            }
            if (num < 16){
                sb.append("0");
            }
            sb.append(Integer.toHexString(num));
        }
        return sb.toString();
    }
}
