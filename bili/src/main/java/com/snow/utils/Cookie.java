package com.snow.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

// 读取 cookies 文件内容
public class Cookie {
    public String getCookiesFromFile() {
        // 获取项目根目录路径
        String projectRootPath = Paths.get("").toAbsolutePath().toString();
        Path cookiesFilePath = Paths.get(projectRootPath, "cookies", "cookies.txt");

        // 读取 cookies 文件内容
        List<String> cookiesList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(cookiesFilePath.toString()))) {
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
