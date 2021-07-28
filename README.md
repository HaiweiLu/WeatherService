# WeatherService

## 使用 Spring Boot 搭建一个简单的天气服务

### 参数设置
`application.properties`中参数含义
```shell
# 项目运行端口
server.port
# 转发地址
forwardUrl
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
* ParseStringService: 处理 go-cqhttp 传递过来的消息

### API接口
* /{location}: location 填入城市名的中文名，返回天气消息
* /test/{message}: message 可以填入任何字符，设计来测试使用
* /: 通过传入 JSON ，返回 null

### 对 go-cqhttp 的转发消息处理如下
目前仅可以处理天气，其中的 `raw_message` 格式为 {城市中文名称}+的天气，如果不是这种格式，都被视为其他请求。
对于其他请求都返回 `暂时没有找到哦,你试试发送 广州的天气 哦`。

### 参考文档
获取天气的[API文档](https://seniverse.yuque.com/books/share/e52aa43f-8fe9-4ffa-860d-96c0f3cf1c49/sdnhw8)