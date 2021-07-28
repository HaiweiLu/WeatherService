package com.example.weatherservice.model;

import java.util.List;

/**
 * @author Haiwei Lu
 * @date 2021/7/28 21:57
 */
public class MessageSegment<T> {

    private String type;
    private List<T> data;

    public MessageSegment() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }
}
