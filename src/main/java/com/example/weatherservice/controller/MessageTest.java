package com.example.weatherservice.controller;

import com.alibaba.fastjson.JSONObject;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: kind
 * @Date: 2021/07/27/23:46
 * @Description: 测试post请求
 */

@RestController
public class MessageTest {
    @PostMapping(value = "/hello")
    public String hello(@RequestBody JSONObject jsonParam) {
        System.out.println(jsonParam.toJSONString());
        return null;
    }
}
