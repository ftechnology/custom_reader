package com.microasset.saiful.util;
public class DataEvent {
    private final String message;
    private final int key;

    public DataEvent(int key, String message) {
        this.key = key;
        this.message = message;
    }

    public int getKey() {
        return key;
    }

    public String getMessage() {
        return message;
    }
}
