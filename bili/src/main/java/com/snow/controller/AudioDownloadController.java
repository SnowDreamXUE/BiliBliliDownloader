package com.snow.controller;

import cn.hutool.http.HttpUtil;
import com.snow.utils.Audio;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;

@RestController
public class AudioDownloadController {

    private static final String downloadPath = "download/";

    @PostMapping("/downloadAudio")
    public ResponseEntity<String> downloadAudio(@RequestBody Audio audio) {

        try {
            String originTitle = audio.getTitle();
            String title = originTitle.replaceAll("[\\\\/]", "_");
            String audioFilePath = downloadPath + title + "_audio.m4s";
            String mp3FilePath = downloadPath + title + ".mp3";

            if(new File(mp3FilePath).exists()) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("文件已存在"); // 返回冲突状态码
            }

            downloadFile(audio.getAudioUrl(), audioFilePath);

            if (!new File(audioFilePath).exists()) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("音频文件下载失败");
            }

            convertToMp3(audioFilePath, mp3FilePath);

            new File(audioFilePath).delete();
            return ResponseEntity.ok("音频文件下载成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("音频文件下载失败");
        }

    }

    private static void downloadFile(String url, String filePath) {
        HttpUtil.createGet(url)
                .header("Referer", "https://www.bilibili.com")
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                .execute()
                .writeBody(new File(filePath));
    }

    private static void convertToMp3(String audioPath, String mp3Path) {
        try {
            String ffmpegPath = "D:/Program Files/ffmpeg-7.1-essentials_build/bin/ffmpeg";
            ProcessBuilder pb = new ProcessBuilder(
                    ffmpegPath,
                    "-i", audioPath, // 输入文件
                    "-vn", "-acodec", "libmp3lame", // 转换为 MP3 格式
                    mp3Path // 输出文件
            );

            Process process = pb.start();
            process.waitFor();
            if (process.exitValue() != 0) {
                throw new RuntimeException("FFmpeg音频转码失败");
            }
        } catch (Exception e) {
            throw new RuntimeException("音频转码失败: " + e.getMessage());
        }
    }
}
