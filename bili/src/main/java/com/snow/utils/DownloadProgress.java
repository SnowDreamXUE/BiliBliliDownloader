package com.snow.utils;


public class DownloadProgress {
    private int progress;
    private String stage; // VIDEO, AUDIO, MERGE, COMPLETE

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public String getStage() {
        return stage;
    }

    public DownloadProgress(int progress, String stage) {
        this.progress = progress;
        this.stage = stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }
}
