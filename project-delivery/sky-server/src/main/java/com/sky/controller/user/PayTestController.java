//package com.sky.controller.user;
//
//import com.alibaba.fastjson.JSONObject;
//import com.alipay.api.AlipayApiException;
//import com.alipay.api.AlipayClient;
//import com.alipay.api.DefaultAlipayClient;
//import com.alipay.api.internal.util.AlipaySignature;
//import com.alipay.api.request.AlipayTradeWapPayRequest;
//import com.alipay.api.response.AlipayTradeWapPayResponse;
//import com.google.common.collect.Maps;
//import com.sky.config.AlipayConfig;
//import com.sky.enumeration.AlipayResultType;
//import com.sky.properties.ZfbProperties;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import java.util.Iterator;
//import java.util.Map;
//
//@Controller
//@RequestMapping("/pay")
//public class PayTestController {
//
//    @Autowired
//    private ZfbProperties zfbProperties;
//
//    /**
//     * 支付
//     *
//     * @param httpRequest  http请求
//     * @param httpResponse http响应
//     */
//    @RequestMapping("/alipaytest")
//    public void doPost(HttpServletRequest httpRequest,HttpServletResponse httpResponse) throws ServletException, IOException, AlipayApiException {
//        //获得初始化的AlipayClient
//        AlipayClient alipayClient = new DefaultAlipayClient(AlipayConfig.URL, zfbProperties.getAppId(), zfbProperties.getAppPrivateKey(), AlipayConfig.FORMAT, AlipayConfig.CHARSET, zfbProperties.getAlipayPublicKey(),AlipayConfig.SIGNTYPE);
//        //创建API对应的request
//        AlipayTradeWapPayRequest request = new AlipayTradeWapPayRequest();
//
//        //异步接收地址，仅支持http/https，公网可访问
//        request.setNotifyUrl("http://zmwvaa.natappfree.cc/pay/alipayresult");
//        //同步跳转地址，仅支持http/https
//        request.setReturnUrl("");
//        /******必传参数******/
//        JSONObject bizContent = new JSONObject();
//        //商户订单号，商家自定义，保持唯一性
//        bizContent.put("out_trade_no", "2021010011019");
//        //支付金额，最小值0.01元
//        bizContent.put("total_amount", 100);
//        //订单标题，不可使用特殊符号
//        bizContent.put("subject", "测试商品");
//        // 设置订单过期时间为15分钟
//        JSONObject extendParams = new JSONObject();
//        extendParams.put("timeout_express", "15m");
//        bizContent.put("extend_params", extendParams);
//
//        request.setBizContent(bizContent.toString());
//        AlipayTradeWapPayResponse response = alipayClient.pageExecute(request,"POST");
//// 如果需要返回GET请求，请使用
//// AlipayTradeWapPayResponse response = alipayClient.pageExecute(request,"GET");
//        String pageRedirectionData = response.getBody();
//        httpResponse.setContentType("text/html;charset=" + AlipayConfig.CHARSET);
//        //直接将完整的表单html输出到页面
//        httpResponse.getWriter().write(pageRedirectionData);
//        httpResponse.getWriter().flush();
//    }
//
//    /**
//     * 支付结果异步通知
//     * @param request  请求
//     * @param response 响应
//     */
//    @PostMapping("/alipayresult")
//    public void alipaytest(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException, AlipayApiException {
//        Map<String,String> params = Maps.newHashMap();
//        Map requestParams = request.getParameterMap();
//        for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
//            String name = (String) iter.next();
//            String[] values = (String[]) requestParams.get(name);
//            String valueStr = "";
//            for (int i = 0; i < values.length; i++) {
//                valueStr = (i == values.length - 1) ? valueStr + values[i]
//                        : valueStr + values[i] + ",";
//            }
//            //乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
//            //valueStr = new String(valueStr.getBytes("ISO-8859-1"), "gbk");
//            params.put(name, valueStr);
//        }
//
//        //获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以上仅供参考)//
//        //计算得出通知验证结果
//        //boolean AlipaySignature.rsaCheckV1(Map<String, String> params, String publicKey, String charset, String sign_type)
//        boolean verifyResult = AlipaySignature.rsaCheckV1(params, zfbProperties.getAlipayPublicKey(), AlipayConfig.CHARSET, AlipayConfig.SIGNTYPE);
//
//        if(verifyResult) {
//            //验证成功
//            //////////////////////////////////////////////////////////////////////////////////////////
//            //请在这里加上商户的业务逻辑程序代码
//            //商户订单号
//            String out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"),"UTF-8");
//
//            //支付宝交易号
//            String trade_no = new String(request.getParameter("trade_no").getBytes("ISO-8859-1"),"UTF-8");
//
//            //交易状态
//            String trade_status = new String(request.getParameter("trade_status").getBytes("ISO-8859-1"),"UTF-8");
//
//            //——请根据您的业务逻辑来编写程序（以下代码仅作参考）——
//            //交易结束，不可退款
//            if (AlipayResultType.TRADE_FINISHED.toString().equals(trade_status)) {
//                //判断该笔订单是否在商户网站中已经做过处理
//
//                //如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
//                //请务必判断请求时的total_fee、seller_id与通知时获取的total_fee、seller_id为一致的
//                //如果有做过处理，不执行商户的业务程序
//
//                //注意：
//                //如果签约的是可退款协议，退款日期超过可退款期限后（如三个月可退款），支付宝系统发送该交易状态通知
//                //如果没有签约可退款协议，那么付款完成后，支付宝系统发送该交易状态通知。
//            } else if (AlipayResultType.TRADE_SUCCESS.toString().equals(trade_status)) {
//                //交易成功
//                System.out.println(trade_status);
//                //判断该笔订单是否在商户网站中已经做过处理
//                //如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
//                //请务必判断请求时的total_fee、seller_id与通知时获取的total_fee、seller_id为一致的
//                //如果有做过处理，不执行商户的业务程序
//
//                //注意：
//                //如果签约的是可退款协议，那么付款完成后，支付宝系统发送该交易状态通知。
//            }
//            response.getWriter().write("success");
//        }else{
//            response.getWriter().write("fail");
//        }
//    }
//}
