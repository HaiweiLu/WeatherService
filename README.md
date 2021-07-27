# WeatherService

## 使用 Spring Boot 搭建一个简单的天气服务

### 参数设置
`application.properties`中参数含义
```shell
# 项目运行端口
server.port
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
* DiaryWeatherUrlBuilder: 实现对 WeatherUrl 进行默认参数设置(未实现)
* FaviconController: 处理浏览器的 favico.ico 请求
* WeatherResponse: 定义返回客服端的 weather 对象

### API接口
* /{location}: location 填入城市名的中文名，返回天气消息
* /test/{message}: message 可以填入任何字符，设计来测试使用

### 参考文档
获取天气的[API文档](https://seniverse.yuque.com/books/share/e52aa43f-8fe9-4ffa-860d-96c0f3cf1c49/sdnhw8)