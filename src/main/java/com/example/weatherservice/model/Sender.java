package com.example.weatherservice.model;

/**
 * @author Haiwei Lu
 * @date 2021/7/28 18:55
 */
public class Sender {

    private String nickname;
    private String message;
    private String userId;
    private String demand;

    public Sender() {
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDemand() {
        return demand;
    }

    public void setDemand(String demand) {
        this.demand = demand;
    }
}
