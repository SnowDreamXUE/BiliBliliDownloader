package com.snow.controller;

import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.snow.utils.Cookie;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


@RestController
public class LoginController {

    //生成登录二维码
    @GetMapping("/login_url")
    public String loginTest() {
        String apiUrl = "https://passport.bilibili.com/x/passport-login/web/qrcode/generate";
        return HttpUtil.createGet(apiUrl)
                .header("Referer", "https://www.bilibili.com")
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                .execute().body();
    }

    @GetMapping("/login_check")
    public String loginCheck(@RequestParam String qrcode_key) throws IOException {
        String apiUrl = "https://passport.bilibili.com/x/passport-login/web/qrcode/poll";
        HttpResponse response = HttpUtil.createGet(apiUrl)
                .header("Referer", "https://www.bilibili.com")
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                .form("qrcode_key", qrcode_key)
                .execute();

        String responseBody = response.body();

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(responseBody);
        JsonNode dataNode = rootNode.path("data");
        int code = dataNode.path("code").asInt();

        if (code == 0 && !dataNode.isMissingNode()) {

            // 获取项目根目录，并保存 cookies.txt 到与 src 同级的 cookies 目录下
            String projectRootPath = Paths.get("").toAbsolutePath().toString();
            Path cookiesDirPath = Paths.get(projectRootPath, "cookies");
            Path cookiesFilePath = cookiesDirPath.resolve("cookies.txt");

            // 如果 cookies 目录不存在，则创建
            if (!Files.exists(cookiesDirPath)) {
                Files.createDirectories(cookiesDirPath);
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(cookiesFilePath.toString()))) {
                for (String cookie : response.headers().get("Set-Cookie")) {
                    writer.write(cookie + "\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        return responseBody;
    }

    //通过获取用户信息，查看是否登录
    @GetMapping("/user_info")
    public String userInfo() {
        // 调用 Cookie 类的 getCookiesFromFile 方法，获取 cookies 字符串
        String cookiesString = new Cookie().getCookiesFromFile();

        String apiUrl = "https://api.bilibili.com/x/web-interface/nav";

        return HttpUtil.createGet(apiUrl)
                .header("Referer", "https://www.bilibili.com")
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                .header("Cookie", cookiesString)
                .execute().body();
    }

}
