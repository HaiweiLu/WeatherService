package com.example.weatherservice.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 处理浏览器的 favicon.ico 请求, 返回为空
 * 其他处理<a href="https://www.baeldung.com/spring-boot-favicon">方法</a>
 * @author Haiwei Lu
 * @created 2021/7/27 17:11
 */
@Controller
public class FaviconController {

    @GetMapping("favicon.ico")
    @ResponseBody
    void returnNoFavicon() {
    }
}
