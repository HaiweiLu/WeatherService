package com.example.weatherservice.provider;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.weatherservice.model.Weather;
import com.example.weatherservice.payload.WeatherResponse;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Objects;

/**
 * @author Haiwei Lu
 * @created 2021/7/24 16:55
 */
@Component
public class WeatherProvider {

    @Value("${weatherAPI}")
    private String DAILY_WEATHER_URL;

    @Value("${weatherAPI.publicKey}")
    private String WEATHER_API_PUBLIC_KEY;

    @Value("${weatherAPI.privateKey}")
    private String WEATHER_API_PRIVATE_KEY;

    private String generateSignature(String data, String key) throws SignatureException {
        String result;
        try {
            // get an hmac_sha1 key from the raw key bytes
            SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA1");
            // get an hmac_sha1 Mac instance and initialize with the signing key
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(signingKey);
            // compute the hmac on input data bytes
            byte[] rawHmac = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            result = new String(Base64.getEncoder().encode(rawHmac));
        }
        catch (Exception e) {
            throw new SignatureException("Failed to generate HMAC : " + e.getMessage());
        }
        return result;
    }

    /**
     * Generate the URL to get diary weather
     * @param location  获取天气的位置
     * @param language  结果显示的语言
     * @param unit      温度的单位
     * @param start     起始日期
     * @param days      获取天气的天数
     * @return url      返回一个 URL 字符串
     */
    public String diaryWeatherURL(
            String location,
            String language,
            String unit,
            String start,
            String days
    )  throws SignatureException {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String params = "ts=" + timestamp + "&ttl=1800&uid=" + WEATHER_API_PUBLIC_KEY;
        String signature = URLEncoder.encode(
                generateSignature(params, WEATHER_API_PRIVATE_KEY), StandardCharsets.UTF_8);
        return DAILY_WEATHER_URL + "?" + params
                + "&sig=" + signature + "&location=" + location
                + "&language=" + language + "&unit=" + unit
                + "&start=" + start + "&days=" + days;
    }

    public WeatherResponse getDailyWeather(String location) throws BadHanyuPinyinOutputFormatCombination, SignatureException {

        String locationPinyin = getPinyin(location);
        String url = diaryWeatherURL(locationPinyin, "zh-Hans", "c", "0", "1");

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            String weatherString = Objects.requireNonNull(response.body()).string();

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

        List<WeatherResponse> weatherResponses = new ArrayList<>();

        // 获取天气可能会失败
        try {
            JSONObject resultsJson = weatherJson.getJSONArray("results")
                    .getJSONObject(0);

            JSONArray dailyArray = resultsJson.getJSONArray("daily");
            // System.out.println(dailyArray);

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
        } catch (Exception ignored) {
        }

        return weatherResponses;
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
