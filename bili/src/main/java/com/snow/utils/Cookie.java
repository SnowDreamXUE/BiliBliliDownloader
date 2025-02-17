package com.snow.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// 读取 cookies 文件内容
public class Cookie {

    private static final String cookiesPath = Constants.PATH_COOKIES;

    public String getCookiesFromFile() {
        // 读取 cookies 文件内容
        List<String> cookiesList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(cookiesPath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // 提取 Cookie 的 key=value 部分
                String cookie = line.split(";", 2)[0].trim(); // 只保留第一部分
                cookiesList.add(cookie);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "Error: Failed to read cookies file.";
        }

        // 将所有 Cookie 拼接为一个字符串
        return String.join("; ", cookiesList);
    }
}
