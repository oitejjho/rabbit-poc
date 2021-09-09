package com.example.rabbitpoc.domain;

public class Message {
    private String uuid = java.util.UUID.randomUUID().toString();

    public String getUuid() {
        return this.uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @Override
    public String toString() {
        return "Message [timestamp=" + uuid + "]";
    }
}
