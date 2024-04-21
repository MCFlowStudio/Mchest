package com.flow.mchest.config;

public class ChestConfig {
    String title;
    int size;

    public ChestConfig(String title, int size) {
        this.title = title;
        this.size = size;
    }

    public String getTitle() {
        return title;
    }

    public int getSize() {
        return size;
    }
}