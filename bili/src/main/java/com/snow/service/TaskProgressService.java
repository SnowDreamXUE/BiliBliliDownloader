package com.snow.service;

import com.snow.utils.DownloadProgress;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TaskProgressService {
    private final Map<String, DownloadProgress> tasks = new ConcurrentHashMap<>();

    public String createNewTask() {
        String taskId = UUID.randomUUID().toString();
        tasks.put(taskId, new DownloadProgress(0, "INIT"));
        return taskId;
    }

    public void updateProgress(String taskId, DownloadProgress progress) {
        tasks.put(taskId, progress);
    }

    public DownloadProgress getProgress(String taskId) {
        return tasks.get(taskId);
    }

    public void removeTask(String taskId) {
        tasks.remove(taskId);
    }
}