package com.snow.controller;


import cn.hutool.http.HttpUtil;

import com.snow.utils.Cookie;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author SnowDreamXUE
 * 代理控制器
 */
@RestController
public class BiliController {
    /**
     * 移动端链接
     */
    @GetMapping("/move")
    public String getMobileContent(@RequestParam("url") String url) {
        return HttpUtil.createGet(url).execute().body();
    }

    /**
     * 根据bv获取详细信息，其中avid和cid很重要
     */
    @GetMapping("/av/{bv}")
    public String getAv(@PathVariable("bv") String bv) {
        return HttpUtil.createGet("https://api.bilibili.com/x/web-interface/view?bvid=" + bv).execute().body();
    }

    // 修改下载接口
    @GetMapping("/download/{avid}/{cid}")
    public String getDownloadUrl(
            @PathVariable("avid") String avid,
            @PathVariable("cid") String cid,
            @RequestParam("qn") String qn) {

        // 构建API URL并直接附加上查询参数
        String apiUrl = String.format("https://api.bilibili.com/x/player/playurl?avid=%s&cid=%s&qn=%s&fnval=80",
                avid, cid, qn);

        // 调用 Cookie 类的 getCookiesFromFile 方法，获取 cookies 字符串
        String cookiesString = new Cookie().getCookiesFromFile();

        return HttpUtil.createGet(apiUrl)
                .header("Referer", "https://www.bilibili.com")
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                .header("Cookie", cookiesString)
                .execute().body();
    }



}
