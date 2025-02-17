package com.snow.utils;

// 进度监听接口
@FunctionalInterface
public interface ProgressListener {
    void onProgress(long downloaded, long total);
}
