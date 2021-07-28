package com.example.weatherservice.service;

import com.alibaba.fastjson.JSONObject;
import com.example.weatherservice.model.Sender;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author Haiwei Lu
 * @date 2021/7/28 19:07
 */
@Service
public class ParseStringService {

    // 简单实现指令集合 TODO 希望下次可以使用分词
    // private static final ArrayList instructions = new ArrayList<>("天气", "的天气");

    public Sender parseJsonToString(JSONObject jsonObject) {

        try {
            String message = jsonObject.getString("raw_message");
            Sender sender = new Sender();
            if (Objects.equals(message.substring(message.length()-2), "天气")) {
                sender.setDemand(message.substring(0, message.length()-3));
            } else {
                // TODO 目前只处理天气请求，所以一切非天气请求，都设置为无请求
                sender.setDemand("");
            }

            JSONObject senderInfo = jsonObject.getJSONObject("sender");
            String nickname = senderInfo.getString("nickname");
            String userId = senderInfo.getString("user_id");
            sender.setMessage(message);
            sender.setUserId(userId);
            sender.setNickname(nickname);

            return sender;
        } catch (Exception ignored) {
        }

        return new Sender();
    }
}
