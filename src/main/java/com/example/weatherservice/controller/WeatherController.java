package com.example.weatherservice.controller;

import com.example.weatherservice.payload.WeatherResponse;
import com.example.weatherservice.provider.WeatherProvider;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.security.SignatureException;

/**
 * @author Haiwei Lu
 * @created 2021/7/24 19:09
 */
@RestController
public class WeatherController {

    private final WeatherProvider weatherProvider;

    @Autowired
    public WeatherController(WeatherProvider weatherProvider) {
        this.weatherProvider = weatherProvider;
    }

    @GetMapping("/{location}")
    public WeatherResponse fetchWeather(
            @PathVariable(value = "location") String location)
            throws SignatureException, BadHanyuPinyinOutputFormatCombination {

        return weatherProvider.getDailyWeather(location);
    }

    /**
     * 使用该函数测试是否成功接收 go-cqhttp 的信息
     *
     * @param message 信息
     */
    @GetMapping("/test/{message}")
    public String testResponse(@PathVariable(value = "message") String message) {

        return String.format("Successful, message: %s", message);
    }
}
