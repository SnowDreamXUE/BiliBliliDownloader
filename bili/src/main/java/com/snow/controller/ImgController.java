package com.snow.controller;


import cn.hutool.core.io.IoUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

@RestController
public class ImgController {

    /*
     * 代理图片
     */
    @GetMapping("/proxy-image")
    public void proxyImage(@RequestParam String url, HttpServletResponse response) throws IOException {
        try {
            HttpResponse httpResponse = HttpRequest.get(url)
                    .header("Referer", "https://www.bilibili.com/")
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                    .timeout(10000) // 设置超时时间
                    .execute();

            // 检查响应状态码
            if (httpResponse.isOk()) {
                // 获取响应数据
                byte[] imageData = httpResponse.bodyBytes();

                // 设置响应头
                response.setContentType(httpResponse.header("Content-Type")); // 使用原始图片的 Content-Type
                response.setContentLength(imageData.length);

                // 输出数据到响应流
                try (OutputStream outputStream = response.getOutputStream()) {
                    IoUtil.write(outputStream, true, imageData);
                }
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("Failed to fetch image. Status: " + httpResponse.getStatus());
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Error fetching image: " + e.getMessage());
        }
    }
}
