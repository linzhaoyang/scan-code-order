package com.sky.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;


/**
 * @ClassName : ZfbProperties
 * @Description :
 * @Date :   2024/05/12
 */
@Component
@ConfigurationProperties(prefix="zfb")
@PropertySource("classpath:/zfbinfo.properties")
@Data
public class ZfbProperties {
    //支付宝公钥
    private String alipayPublicKey;
    //应用id
    private String appId;
    //应用私钥
    private String appPrivateKey;
    // 服务器异步通知页面路径 需http://或者https://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    private  String notifyUrl;
    // 页面跳转同步通知页面路径 需http://或者https://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问 商户可以自定义同步跳转地址
    private  String returnUrl;
    // 请求网关地址
    private  String url;
    // 编码
    private  String charset;
    // 返回格式
    private  String format;
    // 日志记录目录
    private  String logPath;
    // RSA2
    private  String signtype;

    private String payUrl;
}
