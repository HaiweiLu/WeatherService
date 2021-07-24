# WeatherService

## 使用 Spring Boot 搭建一个简单的天气服务

### 参数设置
`application.properties`中参数含义
```shell
# 获取天气的 API
weatherAPI
# API 的公钥
weatherAPI.publicKey
# API 的私钥
weatherAPI.privateKey
```
因为获取天气的 `API` 使用公私钥联合加密认证。

### 功能分析
* WeatherProvider: 从 API 处获取天气信息
* WeatherController: 返回请求给客户端
* DiaryWeatherUrlBuilder: 实现对 WeatherUrl 进行默认参数设置