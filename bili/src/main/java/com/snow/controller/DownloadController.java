package com.snow.controller;


import com.snow.service.TaskProgressService;
import com.snow.utils.Constants;
import com.snow.utils.DownloadProgress;

import com.snow.utils.Video;
import com.snow.utils.ProgressListener;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CompletableFuture;

@RestController
public class DownloadController {

    @Resource
    private TaskProgressService taskProgressService;

    private static final String downloadPath = Constants.PATH_DOWNLOAD;

    @PostMapping("/downloadVideo")
    public ResponseEntity<String> downloadVideo(@RequestBody Video video) {

        String taskId = taskProgressService.createNewTask();

        String originTitle = video.getTitle();
        String title = originTitle.replaceAll("[\\\\/]", "_");
        String videoFilePath = downloadPath + title + "_video.m4s";
        String audioFilePath = downloadPath + title + "_audio.m4s";
        String mergedFilePath = downloadPath + title + ".mp4";

        if (new File(mergedFilePath).exists()) {
            taskProgressService.removeTask(taskId); // 移除任务
            return ResponseEntity.status(HttpStatus.CONFLICT).body("文件已存在"); // 返回冲突状态码
        }

        CompletableFuture.runAsync(() -> {
            try {
                // 下载视频流
                taskProgressService.updateProgress(taskId, new DownloadProgress(0, "VIDEO"));
                downloadFileWithProgress(video.getVideoUrl(), videoFilePath, (downloaded, total) -> {
                    int progress = (int) ((downloaded * 100) / total);
                    int finalProgress = progress / 3;  // 视频下载占总进度的1/3
                    taskProgressService.updateProgress(taskId, new DownloadProgress(finalProgress, "VIDEO"));
                });

                // 下载音频流
                taskProgressService.updateProgress(taskId, new DownloadProgress(33, "AUDIO"));
                downloadFileWithProgress(video.getAudioUrl(), audioFilePath, (downloaded, total) -> {
                    int progress = (int) ((downloaded * 100) / total);
                    int finalProgress = 33 + (progress / 3);  // 音频下载占总进度的1/3
                    taskProgressService.updateProgress(taskId, new DownloadProgress(finalProgress, "AUDIO"));
                });

                // 合并音视频
                taskProgressService.updateProgress(taskId, new DownloadProgress(66, "MERGE"));
                mergeVideoAndAudio(videoFilePath, audioFilePath, mergedFilePath);

                // 完成
                taskProgressService.updateProgress(taskId, new DownloadProgress(100, "COMPLETE"));

                // 清理临时文件
                new File(videoFilePath).delete();
                new File(audioFilePath).delete();

                // 延迟移除任务
                Thread.sleep(2000);
                taskProgressService.removeTask(taskId);
            } catch (Exception e) {
                e.printStackTrace();
                taskProgressService.removeTask(taskId);
            }
        });

        return ResponseEntity.ok(taskId);
    }

    @GetMapping("/task/{taskId}/progress")
    public ResponseEntity<DownloadProgress> getProgress(@PathVariable String taskId) {
        DownloadProgress progress = taskProgressService.getProgress(taskId);
        return progress != null ? ResponseEntity.ok(progress) : ResponseEntity.notFound().build();
    }

    private void downloadFileWithProgress(String url, String filePath, ProgressListener listener) {
        try {
            URL urlObj = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) urlObj.openConnection();
            conn.setRequestProperty("Referer", "https://www.bilibili.com");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)");

            int contentLength = conn.getContentLength();
            try (InputStream in = conn.getInputStream();
                 FileOutputStream fos = new FileOutputStream(filePath)) {

                byte[] buffer = new byte[8192];
                int len;
                long downloaded = 0;

                while ((len = in.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                    downloaded += len;
                    if (listener != null && contentLength > 0) {
                        listener.onProgress(downloaded, contentLength);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("下载文件失败: " + e.getMessage());
        }
    }

    private void mergeVideoAndAudio(String videoPath, String audioPath, String outputPath) {
        try {
            String ffmpegPath = Constants.PATH_FFMPEG;
            ProcessBuilder pb = new ProcessBuilder(
                    ffmpegPath,
                    "-i", videoPath,
                    "-i", audioPath,
                    "-c", "copy",
                    outputPath
            );

            Process process = pb.start();
            process.waitFor();

            if (process.exitValue() != 0) {
                throw new RuntimeException("FFmpeg合并失败");
            }
        } catch (Exception e) {
            throw new RuntimeException("合并音视频失败: " + e.getMessage());
        }
    }

}