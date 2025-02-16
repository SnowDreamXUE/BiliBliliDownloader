package com.snow;

import cn.hutool.http.HttpUtil;

public class download {

    public void getDownloadUrl(){

        // 构建API URL并直接附加上查询参数
        String apiUrl = "https://api.bilibili.com/x/player/wbi/playurl?avid=113939459346503&cid=28199357904&fnval=80";

        // 直接构建Cookie字符串（替换下方值即可）
        String cookie = "";
        String response =  HttpUtil.createGet(apiUrl)
                .header("Referer", "https://www.bilibili.com")
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                .header("Cookie", cookie)
                .execute().body();
        System.out.println(response);
    }

    public static void main(String[] args) {
        download d = new download();
        d.getDownloadUrl();
    }
}
