package com.snow.utils;

public class Constants {

    public static String PATH_BASE;

    public static String PATH_DOWNLOAD;

    public static String PATH_COOKIES;

    public static String PATH_FFMPEG;

    static {
        PATH_BASE = PropertiesUtils.getString("path.base");
        PATH_DOWNLOAD = PATH_BASE + PropertiesUtils.getString("path.download");
        PATH_COOKIES = PATH_BASE + PropertiesUtils.getString("path.cookies");
        PATH_FFMPEG = PropertiesUtils.getString("path.ffmpeg");
    }
}
