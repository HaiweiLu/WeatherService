package com.example.weatherservice.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.weatherservice.model.Sender;
import com.example.weatherservice.payload.WeatherResponse;
import com.example.weatherservice.provider.WeatherProvider;
import com.example.weatherservice.service.ParseStringService;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.SignatureException;
import java.util.Objects;
import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 *
 * @author kind
 * @date 2021/07/27/23:46
 * @description 测试 post 请求
 */
@RestController
public class MessageController {

    @Value("${forwardUrl}")
    private String forwardUrl;

    private final ParseStringService parseStringService;

    private final WeatherProvider weatherProvider;

    @Autowired
    public MessageController(ParseStringService parseStringService, WeatherProvider weatherProvider) {
        this.parseStringService = parseStringService;
        this.weatherProvider = weatherProvider;
    }

    @PostMapping(value = "/")
    public String getMessage(@RequestBody JSONObject jsonParam) throws BadHanyuPinyinOutputFormatCombination, SignatureException {

        // 处理非正常转发(比如心跳信息)
        try {
            jsonParam.getString("raw_message");

            Sender sender = parseStringService.parseJsonToString(jsonParam);

            StringBuilder message = new StringBuilder();
            if (Objects.equals(sender.getDemand(), "")) {
                Random random = new Random();
                int number = random.nextInt(4);
                if (number == 0) {
                    message.append("暂时没有找到哦,你试试发送 广州的天气 哦");
                } else if (number == 1) {
                    message.append("好深奥哦, 换个问题吧?");
                } else if (number == 2) {
                    message.append("啊? 这个我就不懂了哦");
                } else {
                    message.append("这也太难了");
                }

            } else if (sender.getUserId() != null) {
                WeatherResponse weatherResponse = weatherProvider.getDailyWeather(sender.getDemand());
                message.append(sender.getDemand())
                        .append("的天气: \r\n")
                        .append("\r\n")
                        .append("白天天气状况: ")
                        .append(weatherResponse.getDayWeather())
                        .append("\r\n")
                        .append("晚上天气状况: ")
                        .append(weatherResponse.getNightWeather())
                        .append("\r\n")
                        .append("最高温度: ")
                        .append(weatherResponse.getHighTemperature())
                        .append(" 度\r\n")
                        .append("最低温度: ")
                        .append(weatherResponse.getLowTemperature())
                        .append(" 度\r\n")
                        .append("湿度: ")
                        .append(weatherResponse.getHumidity())
                        .append("\r\n")
                        .append("降雨量: ")
                        .append(weatherResponse.getRainfall())
                        .append("\r\n")
                        .append("风力大小: ")
                        .append(weatherResponse.getWindScale())
                        .append(" 级\r\n")
                        .append("风的角度: ")
                        .append(weatherResponse.getWindDirectionDegree())
                        .append(" 度\r\n");
            } else {
                return null;
            }

            OkHttpClient client = new OkHttpClient();

            String url = forwardUrl + "user_id=" + sender.getUserId()
                    + "&message=" + URLEncoder.encode(message.toString(), StandardCharsets.UTF_8);
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            try (Response response = client.newCall(request).execute()) {
                System.out.println(Objects.requireNonNull(response.body()).string());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            return null;
        }

        return null;
    }
}
