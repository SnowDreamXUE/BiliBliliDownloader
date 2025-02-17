package com.snow.utils;

@FunctionalInterface
public interface ProgressCallback {
    void onProgress(int progress);
}
