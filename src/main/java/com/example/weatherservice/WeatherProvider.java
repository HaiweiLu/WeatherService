package com.example.weatherservice;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.SignatureException;
import java.util.Base64;
import java.util.Date;

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
            // result = new sun.misc.BASE64Encoder().encode(rawHmac);
        }
        catch (Exception e) {
            throw new SignatureException("Failed to generate HMAC : " + e.getMessage());
        }
        return result;
    }

    /**
     * Generate the URL to get diary weather
     * @param location
     * @param language
     * @param unit
     * @param start
     * @param days
     * @return
     */
    public String diaryWeatherURL(
            String location,
            String language,
            String unit,
            String start,
            String days
    )  throws SignatureException, UnsupportedEncodingException {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String params = "ts=" + timestamp + "&ttl=1800&uid=" + WEATHER_API_PUBLIC_KEY;
        String signature = URLEncoder.encode(
                generateSignature(params, WEATHER_API_PRIVATE_KEY), StandardCharsets.UTF_8);
        return DAILY_WEATHER_URL + "?" + params
                + "&sig=" + signature + "&location=" + location
                + "&language=" + language + "&unit=" + unit
                + "&start=" + start + "&days=" + days;
    }
}
