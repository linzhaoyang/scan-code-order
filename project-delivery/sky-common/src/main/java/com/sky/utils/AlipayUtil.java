package com.sky.utils;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.AlipayConfig;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeQueryModel;
import com.alipay.api.domain.AlipayTradeRefundModel;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.alipay.api.response.AlipayTradeWapPayResponse;
import com.sky.exception.OrderBusinessException;
import com.sky.properties.ZfbProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
@Slf4j
@Component
public class AlipayUtil {

    @Autowired
    private ZfbProperties zfbProperties;


    /**
     * 描述：下单
     *
     * @param httpResponse HTTP 响应
     * @param outTradeNo   订单编号
     * @param amount       金额
     */
    public void doPost(HttpServletResponse httpResponse, String outTradeNo, BigDecimal amount) throws  IOException, AlipayApiException {
        //获得初始化的AlipayClient
        AlipayClient alipayClient = new DefaultAlipayClient(getAlipayConfig());
        //创建API对应的request
        AlipayTradeWapPayRequest request = new AlipayTradeWapPayRequest();

        //异步接收地址，仅支持http/https，公网可访问
        request.setNotifyUrl(zfbProperties.getNotifyUrl());
        //同步跳转地址，仅支持http/https
        request.setReturnUrl("");
        /******必传参数******/
        JSONObject bizContent = new JSONObject();
        //商户订单号，商家自定义，保持唯一性
        bizContent.put("out_trade_no", outTradeNo);
        //支付金额
        bizContent.put("total_amount", amount);
        //订单标题，不可使用特殊符号
        bizContent.put("subject", "商户订单号"+outTradeNo);
        // 设置订单过期时间为15分钟
        JSONObject extendParams = new JSONObject();
        extendParams.put("timeout_express", "15m");
        bizContent.put("extend_params", extendParams);
        request.setBizContent(bizContent.toString());

        AlipayTradeWapPayResponse response = alipayClient.pageExecute(request,"POST");
// 如果需要返回GET请求，请使用
// AlipayTradeWapPayResponse response = alipayClient.pageExecute(request,"GET");
        String pageRedirectionData = response.getBody();
        httpResponse.setContentType("text/html;charset=" + zfbProperties.getCharset());
        //直接将完整的表单html输出到页面
        httpResponse.getWriter().write(pageRedirectionData);
        httpResponse.getWriter().flush();
    }


    /**
     * 描述：验签
     *
     * @param request 请求
     * @return boolean
     */
    public boolean isVerifyResult(HttpServletRequest request) throws AlipayApiException {
        Map<String,String> params = new HashMap<>();
        Map requestParams = request.getParameterMap();
        for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            //乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
            //valueStr = new String(valueStr.getBytes("ISO-8859-1"), "gbk");
            params.put(name, valueStr);
        }

        //获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以上仅供参考)//
        //计算得出通知验证结果
        //boolean AlipaySignature.rsaCheckV1(Map<String, String> params, String publicKey, String charset, String sign_type)
        return AlipaySignature.rsaCheckV1(params, zfbProperties.getAlipayPublicKey(), zfbProperties.getCharset(), zfbProperties.getSigntype());
    }


    /**
     * 描述：获取交易详情
     *
     * @param outTradeNo 出站交易编号
     * @return boolean
     */
    public AlipayTradeQueryResponse getAlipay(String outTradeNo) throws AlipayApiException {
        // 初始化SDK
        AlipayClient alipayClient = new DefaultAlipayClient(getAlipayConfig());

        // 构造请求参数以调用接口
        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        AlipayTradeQueryModel model = new AlipayTradeQueryModel();

        // 设置订单支付时传入的商户订单号
        model.setOutTradeNo(outTradeNo);

        request.setBizModel(model);
        return alipayClient.execute(request);
    }


    /**
     * 描述：退款
     */
    public  void refund(String outTradeNo, BigDecimal amount,String refundReason ) throws AlipayApiException {
        // 初始化SDK
        AlipayClient alipayClient = new DefaultAlipayClient(getAlipayConfig());

        // 构造请求参数以调用接口
        AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();
        AlipayTradeRefundModel model = new AlipayTradeRefundModel();

        // 设置商户订单号
        model.setOutTradeNo(outTradeNo);

        // 设置退款金额
        model.setRefundAmount(String.valueOf(amount));

        // 设置退款原因说明
        model.setRefundReason(refundReason);

        request.setBizModel(model);
        AlipayTradeRefundResponse response = alipayClient.execute(request);

        if (response.isSuccess()) {
            log.info("订单号：{}-已退款金额：{}",response.getOutTradeNo(),response.getRefundFee());
        } else {
            throw new OrderBusinessException("订单退款异常");
        }
    }



    private  AlipayConfig getAlipayConfig() {
        String privateKey  = zfbProperties.getAppPrivateKey();
        String alipayPublicKey = zfbProperties.getAlipayPublicKey();
        AlipayConfig alipayConfig = new AlipayConfig();
        alipayConfig.setServerUrl(zfbProperties.getUrl());
        alipayConfig.setAppId(zfbProperties.getAppId());
        alipayConfig.setPrivateKey(privateKey);
        alipayConfig.setFormat(zfbProperties.getFormat());
        alipayConfig.setAlipayPublicKey(alipayPublicKey);
        alipayConfig.setCharset(zfbProperties.getCharset());
        alipayConfig.setSignType(zfbProperties.getSigntype());
        return alipayConfig;
    }

}
