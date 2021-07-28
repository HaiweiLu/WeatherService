package com.example.weatherservice.controller;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonParseException;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: kind
 * @Date: 2021/07/27/23:46
 * @Description: 测试post请求
 */
@RestController
public class MessageController {

    @PostMapping(value = "")
    public String getMessage(@RequestBody JSONObject jsonParam) {

        String message = jsonParam.getString("raw_message");
        if (message.isEmpty()) {
            return null;
        }

        JSONObject sender = jsonParam.getJSONObject("sender");
        String nickname = sender.getString("nickname");
        String userId = sender.getString("user_id");

        System.out.println("{message: " + message + ", "
                + "nickname: " + nickname + ", "
                + "userId: " + userId + "}");

        // MediaType JSON = MediaType.get("application/json; charset=utf-8");

        OkHttpClient client = new OkHttpClient();

        // RequestBody body = RequestBody.create(JSON, json);
        String url = "http:127.0.0.1:5701/send_private_msg?user_id="
                + userId + "&message=" + message + "\n人类本质是复读机！";
        Request request = new Request.Builder()
                .url(url)
                .build();
        try (Response response = client.newCall(request).execute()) {
            System.out.println(Objects.requireNonNull(response.body()).string());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
