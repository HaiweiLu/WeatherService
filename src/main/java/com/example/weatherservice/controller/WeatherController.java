package com.example.weatherservice.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.weatherservice.model.Weather;
import com.example.weatherservice.payload.WeatherResponse;
import com.example.weatherservice.provider.WeatherProvider;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
            throws UnsupportedEncodingException, SignatureException, BadHanyuPinyinOutputFormatCombination {

        String locationPinyin = getPinyin(location);

        String url = weatherProvider.diaryWeatherURL(locationPinyin, "zh-Hans", "c", "0", "1");

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            String weatherString = Objects.requireNonNull(response.body()).string();
            // TODO 如果返回中没有包含正确的信息即 result, 要进行异常处理
            List<WeatherResponse> weatherResponses = getWeatherResponses(weatherString);

            return weatherResponses.get(0);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new WeatherResponse();
    }

    @NotNull
    private List<WeatherResponse> getWeatherResponses(String weatherString) {
        JSONObject weatherJson = JSONObject.parseObject(weatherString);

        JSONObject resultsJson= weatherJson.getJSONArray("results")
                .getJSONObject(0);

        JSONArray dailyArray = resultsJson.getJSONArray("daily");
        System.out.println(dailyArray);

        List<WeatherResponse> weatherResponses = new ArrayList<>();

        List<Weather> weathers = JSON.parseArray(dailyArray.toJSONString(), Weather.class);
        for (Weather weather : weathers) {
            WeatherResponse weatherResponse = new WeatherResponse();

            weatherResponse.setTime(weather.getDate());
            weatherResponse.setLocation(
                    resultsJson.getJSONObject("location").getString("name"));
            weatherResponse.setDayWeather(weather.getText_day());
            weatherResponse.setNightWeather(weather.getText_night());
            weatherResponse.setHighTemperature(weather.getHigh());
            weatherResponse.setLowTemperature(weather.getLow());
            weatherResponse.setHumidity(weather.getHumidity());
            weatherResponse.setRainfall(weather.getRainfall());
            weatherResponse.setWindDirectionDegree(weather.getWind_direction_degree());
            weatherResponse.setWindScale(weather.getWind_scale());

            weatherResponses.add(weatherResponse);
        }
        return weatherResponses;
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

    @NotNull
    private String getPinyin(String location) throws BadHanyuPinyinOutputFormatCombination {
        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        // 设置格式为小写
        format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        // 设置没有声调
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        // 设置 U 为 V
        format.setVCharType(HanyuPinyinVCharType.WITH_V);

        char[] chars = location.toCharArray();
        StringBuilder result = new StringBuilder();
        for (char c : chars) {
            // 判断 c 是否为中文字符,汉字在UTF-8的位置基本位于[0x4e00, 0x9fa5]
            if (c >= 0x4e00 &&  c <= 0x9fa5) {
                result.append(PinyinHelper.toHanyuPinyinStringArray(c, format)[0]);
            }
        }
        return result.toString();
    }

}
