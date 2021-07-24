package com.example.weatherservice;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.SignatureException;
import java.util.Objects;

/**
 * @author Haiwei Lu
 * @created 2021/7/24 19:09
 */
@RestController
public class WeatherController {

    @Autowired
    private WeatherProvider weatherProvider;

    @GetMapping("/")
    public void getWeather() throws UnsupportedEncodingException, SignatureException {

        String url = weatherProvider.diaryWeatherURL("beijing", "zh-Hans", "c", "0", "5");
        System.out.println(url);

        OkHttpClient client = new OkHttpClient();


        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            System.out.println(Objects.requireNonNull(response.body()).string());
            // return Objects.requireNonNull(response.body()).string();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // return url;
    }

}
