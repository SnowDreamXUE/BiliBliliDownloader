package com.snow;

import cn.hutool.http.HttpUtil;

import java.io.File;

public class videoDownload {

    public static void main(String[] args) {
        // 视频和音频的URL
        String videoUrl = "https://cn-gdgz-gd-live-03.bilivideo.com/upgcxcode/62/66/28223406662/28223406662-1-30080.m4s?e=ig8euxZM2rNcNbdlhoNvNC8BqJIzNbfqXBvEqxTEto8BTrNvN0GvT90W5JZMkX_YN0MvXg8gNEV4NC8xNEV4N03eN0B5tZlqNxTEto8BTrNvNeZVuJ10Kj_g2UB02J0mN0B5tZlqNCNEto8BTrNvNC7MTX502C8f2jmMQJ6mqF2fka1mqx6gqj0eN0B599M=&uipk=5&nbs=1&deadline=1738918406&gen=playurlv2&os=bcache&oi=1996680607&trid=00006d5b14c8d0de46598b83dd3352f8db31u&mid=497373611&platform=pc&og=hw&upsig=8f3eca629d2081b9d4011072b37213b1&uparams=e,uipk,nbs,deadline,gen,os,oi,trid,mid,platform,og&cdnid=6698&bvc=vod&nettype=0&orderid=0,3&buvid=&build=0&f=u_0_0&agrr=1&bw=168548&logo=80000000";
        String audioUrl = "https://cn-gdgz-gd-live-03.bilivideo.com/upgcxcode/62/66/28223406662/28223406662-1-30280.m4s?e=ig8euxZM2rNcNbdlhoNvNC8BqJIzNbfqXBvEqxTEto8BTrNvN0GvT90W5JZMkX_YN0MvXg8gNEV4NC8xNEV4N03eN0B5tZlqNxTEto8BTrNvNeZVuJ10Kj_g2UB02J0mN0B5tZlqNCNEto8BTrNvNC7MTX502C8f2jmMQJ6mqF2fka1mqx6gqj0eN0B599M=&uipk=5&nbs=1&deadline=1738919413&gen=playurlv2&os=bcache&oi=1996680607&trid=00008fcadf0a98b14f54a0657e814ce884d7u&mid=497373611&platform=pc&og=hw&upsig=25c389fab70eb18a01ccd012dbc9de65&uparams=e,uipk,nbs,deadline,gen,os,oi,trid,mid,platform,og&cdnid=6698&bvc=vod&nettype=0&orderid=0,3&buvid=&build=0&f=u_0_0&agrr=1&bw=21629&logo=80000000";

        // 下载视频文件
        String videoFilePath = "video.m4s";
        downloadFile(videoUrl, videoFilePath);
        System.out.println("视频文件下载完成: " + videoFilePath);

        // 下载音频文件
        String audioFilePath = "audio.m4s";
        downloadFile(audioUrl,  audioFilePath);
        System.out.println("音频文件下载完成: " + audioFilePath);

        // 合并音视频（可选）
        String outputFilePath = "output.mp4";
        mergeVideoAndAudio(videoFilePath, audioFilePath, outputFilePath);
        System.out.println("音视频合并完成: " + outputFilePath);
    }

    /**
     * 下载文件
     *
     * @param url      文件URL
     * @param filePath 保存路径
     */
    private static void downloadFile(String url, String filePath) {
        HttpUtil.createGet(url)
                .header("Referer", "https://www.bilibili.com")
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                .execute()
                .writeBody(new File(filePath));
    }

    /**
     * 合并音视频文件
     *
     * @param videoFilePath 视频文件路径
     * @param audioFilePath 音频文件路径
     * @param outputFilePath 输出文件路径
     */
    private static void mergeVideoAndAudio(String videoFilePath, String audioFilePath, String outputFilePath) {
        try {
            // 使用 FFmpeg 合并音视频
            String ffmpegCommand = String.format("D:/Program Files/ffmpeg-7.1-essentials_build/bin/ffmpeg -i %s -i %s -c copy %s", videoFilePath, audioFilePath, outputFilePath);
            Process process = Runtime.getRuntime().exec(ffmpegCommand);
            process.waitFor(); // 等待合并完成
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("合并音视频时出错: " + e.getMessage());
        }
    }
}