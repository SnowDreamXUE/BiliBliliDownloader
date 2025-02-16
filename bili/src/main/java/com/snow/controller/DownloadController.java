package com.snow.controller;

import cn.hutool.http.HttpUtil;
import com.snow.utils.Video;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;

@RestController
public class DownloadController {

    private static final String downloadPath = "download/";

    @PostMapping("/downloadVideo")
    public String downloadVideo(@RequestBody Video video) {

        String title = video.getTitle();
        String videoUrl = video.getVideoUrl();
        String audioUrl = video.getAudioUrl();

        File dir = new File(downloadPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String videoFilePath = downloadPath + title + "_video.m4s";
        String audioFilePath = downloadPath + title + "_audio.m4s";
        String mergedFilePath = downloadPath + title + ".mp4";

        downloadFile(videoUrl, videoFilePath);
        downloadFile(audioUrl, audioFilePath);
        mergeVideoAndAudio(videoFilePath, audioFilePath, mergedFilePath);

        // 检查合并是否成功
        File mergedFile = new File(mergedFilePath);
        if (mergedFile.exists()) {
            // 删除原始的视频和音频文件
            new File(videoFilePath).delete();
            new File(audioFilePath).delete();
            return "合并成功！文件储存在：" + mergedFile.getAbsolutePath();
        } else {

            return "合并后的文件不存在，未删除原始文件！";
        }

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
     * @param mergedFilePath 输出文件路径
     */
    private static void mergeVideoAndAudio(String videoFilePath, String audioFilePath, String mergedFilePath) {
        try {
            // 使用 FFmpeg 合并音视频
            String ffmpegCommand = String.format("D:/Program Files/ffmpeg-7.1-essentials_build/bin/ffmpeg -i %s -i %s -c copy %s", videoFilePath, audioFilePath, mergedFilePath);
            Process process = Runtime.getRuntime().exec(ffmpegCommand);
            process.waitFor(); // 等待合并完成

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("合并音视频时出错: " + e.getMessage());
        }
    }
}
