package com.ruian.utils;

import org.springframework.util.DigestUtils;

public class Md5Utils{


    private static final String salt = "ruian";

    public static String key(String adminId) {
        String key = adminId+'/'+salt;
        return DigestUtils.md5DigestAsHex(key.getBytes());
    }
}
