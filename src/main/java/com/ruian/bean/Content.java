package com.ruian.bean;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Properties;

public class Content{
    public static final String REAL_TITLE_IMG_PATH;
    public static final String REAL_FILE_PATH;
    public static final String TITLE_IMG_PATH;
    public static final String FILE_PATH;


    public static final String URL;
    public static final String USER;
    public static final String PASSWORD;
    public static final String DRIVER;


    static {
        Resource resource = new ClassPathResource("content.properties");
        Properties properties;
        try {
            properties = PropertiesLoaderUtils.loadProperties(resource);
        } catch(IOException e) {
            throw new RuntimeException("content.properties import exception");
        }

        String projectPath = null;
        try {
            projectPath = URLDecoder.decode(Content.class.getClassLoader().getResource("../../").getPath(),"UTF-8");
        } catch(UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        projectPath = projectPath.substring(0, projectPath.length() - 1);
        TITLE_IMG_PATH = properties.getProperty("title_img_path");
        FILE_PATH = properties.getProperty("file_path");
        REAL_TITLE_IMG_PATH = projectPath + TITLE_IMG_PATH;
        REAL_FILE_PATH = projectPath + FILE_PATH;

//        System.out.println(TITLE_IMG_PATH);
//        System.out.println(FILE_PATH);

        URL = properties.getProperty("jdbc_url");
        USER = properties.getProperty("jdbc_user");
        PASSWORD = properties.getProperty("jdbc_password");
        DRIVER = properties.getProperty("jdbc_driver");
    }
}
