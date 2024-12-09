package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.alipay.api.response.AlipayTradeWapPayResponse;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.*;
import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import com.sky.entity.ShoppingCart;
import com.sky.entity.User;
import com.sky.enumeration.AlipayResultType;
import com.sky.exception.OrderBusinessException;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.OrderDetailMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.mapper.UserMapper;
import com.sky.properties.ZfbProperties;
import com.sky.result.PageResult;
import com.sky.service.OrderService;
import com.sky.utils.AlipayUtil;
import com.sky.utils.QRCodeUtil;
import com.sky.utils.WeChatPayUtil;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import com.sky.websocket.WebSocketServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName : OrderServiceImpl
 * @Description :
 * @Date :   2024/05/12
 */
@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private WeChatPayUtil weChatPayUtil;
    @Autowired
    private WebSocketServer webSocketServer;
    @Autowired
    private AlipayUtil alipayUtil;
    @Autowired
    private ZfbProperties zfbProperties;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;


    /**
     * 描述：
     *
     * @param ordersSubmitDTO 订单提交 DTO
     * @return {@link OrderSubmitVO }
     */
    @Override
    @Transactional
    public OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO) {
        //查询当前用户的购物车数据
        Long userId = BaseContext.getCurrentId();

        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUserId(userId);
        List<ShoppingCart> shoppingCartList = shoppingCartMapper.list(shoppingCart);

        if(shoppingCartList == null || shoppingCartList.size() == 0){
            //抛出业务异常
            throw new ShoppingCartBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
        }

        //2. 向订单表插入1条数据
        Orders orders = new Orders();
        BeanUtils.copyProperties(ordersSubmitDTO, orders);
        orders.setOrderTime(LocalDateTime.now());
        orders.setPayStatus(Orders.UN_PAID);
        orders.setStatus(Orders.PENDING_PAYMENT);
        orders.setNumber(String.valueOf(System.currentTimeMillis()));
        orders.setPhone(ordersSubmitDTO.getPhone());
        orders.setDiningType(ordersSubmitDTO.getDiningType());
        orders.setUserId(userId);

        orderMapper.insert(orders);

        List<OrderDetail> orderDetailList = new ArrayList<>();
        //3. 向订单明细表插入n条数据
        for (ShoppingCart cart : shoppingCartList) {
            OrderDetail orderDetail = new OrderDetail();//订单明细
            BeanUtils.copyProperties(cart, orderDetail);
            orderDetail.setOrderId(orders.getId());//设置当前订单明细关联的订单id
            orderDetailList.add(orderDetail);
        }

        orderDetailMapper.insertBatch(orderDetailList);

        //4.将电话号码存储
        User user = new User();
        user.setPhone(ordersSubmitDTO.getPhone());
        user.setId(userId);
        userMapper.updateById(user);

        //5. 清空当前用户的购物车数据
        shoppingCartMapper.deleteByUserId(userId);

        //将订单号存入redis
        redisTemplate.opsForValue().set(userId.toString(),orders.getNumber());

        //7. 封装VO返回结果
        OrderSubmitVO orderSubmitVO = OrderSubmitVO.builder()
                .id(orders.getId())
                .orderTime(orders.getOrderTime())
                .orderNumber(orders.getNumber())
                .orderAmount(orders.getAmount())
                .build();

        return orderSubmitVO;
    }


    /**
     * 订单支付
     *
     * @param ordersPaymentDTO
     * @return
     */
//    @Override
//    public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception {
//        //Todo
//        OrderPaymentVO orderPaymentVO;
//        if (ordersPaymentDTO.getPayMethod().equals(Orders.WX)) {
//            orderPaymentVO=WXPay(ordersPaymentDTO);
//        }else{
//            orderPaymentVO=ZFBPay(ordersPaymentDTO);
//        }
//        return  orderPaymentVO;
//    }

//    private OrderPaymentVO WXPay(OrdersPaymentDTO ordersPaymentDTO) throws Exception {
//        //当前登录用户id
//        Long userId = BaseContext.getCurrentId();
//        User user = userMapper.getById(userId);
//
//        //调用微信支付接口，生成预支付交易单
//        JSONObject jsonObject = weChatPayUtil.pay(
//                ordersPaymentDTO.getOrderNumber(), //商户订单号
//                new BigDecimal(0.01), //支付金额，单位 元
//                "订单", //商品描述
//                user.getOpenid() //微信用户的openid
//        );
//
//        if (jsonObject.getString("code") != null && jsonObject.getString("code").equals("ORDERPAID")) {
//            throw new OrderBusinessException("该订单已支付");
//        }
//
//        OrderPaymentVO orderPaymentVO = jsonObject.toJavaObject(OrderPaymentVO.class);
//        orderPaymentVO.setPackageStr(jsonObject.getString("package"));
//
//        return orderPaymentVO;
//    }
//
//    private OrderPaymentVO ZFBPay(OrdersPaymentDTO ordersPaymentDTO){
//        Orders orders = this.getOrderStatus(ordersPaymentDTO.getOrderNumber());
//        Orders order = Orders.builder().id(orders.getId()).payMethod(Orders.ZFB).build();
//        //修改支付方式
//        orderMapper.update(order);
//        //生成二维码
//        QRCodeUtil qrCodeUtil = new QRCodeUtil();
//        String orderNumber = ordersPaymentDTO.getOrderNumber();
//        String qrCode;
//        try {
//            String url = String.format(zfbProperties.getPayUrl(), orderNumber);
//            qrCode = qrCodeUtil.createQRCode(url, 200, 200);
//        } catch (IOException e) {
//            throw new OrderBusinessException("二维码生成出错");
//        }
//        OrderPaymentVO orderPaymentVO = new OrderPaymentVO();
//        orderPaymentVO.setQrcode(qrCode);
//        return orderPaymentVO;
//    }

    /**
     * 支付成功，修改订单状态
     *
     * @param outTradeNo
     */
    @Override
    public void paySuccess(String outTradeNo) {
        // 根据订单号查询当前用户的订单
        Orders ordersDB = this.getOrderStatus(outTradeNo);

        // 根据订单id更新订单的状态、支付方式、支付状态、结账时间
        Orders orders = Orders.builder()
                .id(ordersDB.getId())
                .status(Orders.TO_BE_CONFIRMED)
                .payStatus(Orders.PAID)
                .checkoutTime(LocalDateTime.now())
                .build();

        orderMapper.update(orders);

        //通过websocket向客户端浏览器推送消息 type orderId content
        Map<String, java.io.Serializable> map = new HashMap<>();
        map.put("type",1); // 1表示来单提醒 2表示客户催单
        map.put("orderId",ordersDB.getId());
        map.put("content","订单号：" + outTradeNo);

//        String json = JSON.toJSONString(map);
//        webSocketServer.sendToAllClient(json);
    }

    /**
     * 用户端订单分页查询
     *
     * @param pageNum
     * @param pageSize
     * @param status
     * @return
     */
    @Override
    public PageResult pageQuery4User(int pageNum, int pageSize, Integer status) {
        // 设置分页
        PageHelper.startPage(pageNum, pageSize);

        OrdersPageQueryDTO ordersPageQueryDTO = new OrdersPageQueryDTO();
        ordersPageQueryDTO.setUserId(BaseContext.getCurrentId());
        ordersPageQueryDTO.setStatus(status);

        // 分页条件查询
        Page<Orders> page = orderMapper.pageQuery(ordersPageQueryDTO);

        List<OrderVO> list = new ArrayList<>();

        // 查询出订单明细，并封装入OrderVO进行响应
        if (page != null && page.getTotal() > 0) {
            for (Orders orders : page) {
                Long orderId = orders.getId();// 订单id

                // 查询订单明细
                List<OrderDetail> orderDetails = orderDetailMapper.getByOrderId(orderId);

                OrderVO orderVO = new OrderVO();
                BeanUtils.copyProperties(orders, orderVO);
                orderVO.setOrderDetailList(orderDetails);

                list.add(orderVO);
            }
        }
        return new PageResult(page.getTotal(), list);
    }

    /**
     * 查询订单详情
     *
     * @param id
     * @return
     */
    @Override
    public OrderVO details(Long id) {
        // 根据id查询订单
        Orders orders = orderMapper.getById(id);

        // 查询该订单对应的菜品/套餐明细
        List<OrderDetail> orderDetailList = orderDetailMapper.getByOrderId(orders.getId());

        // 将该订单及其详情封装到OrderVO并返回
        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(orders, orderVO);
        orderVO.setOrderDetailList(orderDetailList);

        return orderVO;
    }

    /**
     * 用户取消订单
     *
     * @param id
     */
    @Override
    public void userCancelById(Long id) throws Exception {
        // 根据id查询订单
        Orders ordersDB = orderMapper.getById(id);

        // 校验订单是否存在
        if (ordersDB == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }

        //订单状态 1待付款 2待接单 3已接单  4已完成 5已取消
        if (ordersDB.getStatus() > 2) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }

        Orders orders = new Orders();
        orders.setId(ordersDB.getId());

        // 订单处于待接单状态下取消，需要进行退款
        if (ordersDB.getStatus().equals(Orders.TO_BE_CONFIRMED)) {
            //获取支付方式
//            Integer payMethod = ordersDB.getPayMethod();
//            if(payMethod.equals(Orders.WX)){
//            //调用微信支付退款接口
//            weChatPayUtil.refund(ordersDB.getNumber(), ordersDB.getNumber(), ordersDB.getAmount(), ordersDB.getAmount());
//            }else{
//                alipayUtil.refund(ordersDB.getNumber(),ordersDB.getAmount(),"用户取消");
//            }

            //支付状态修改为 退款
            orders.setPayStatus(Orders.REFUND);
        }

        // 更新订单状态、取消原因、取消时间
        orders.setStatus(Orders.CANCELLED);
        orders.setCancelReason("用户取消");
        orders.setCancelTime(LocalDateTime.now());
        orderMapper.update(orders);
    }

    /**
     * 再来一单
     *
     * @param id
     */
    @Override
    public void repetition(Long id) {
        // 查询当前用户id
        Long userId = BaseContext.getCurrentId();

        // 根据订单id查询当前订单详情
        List<OrderDetail> orderDetailList = orderDetailMapper.getByOrderId(id);

        // 将订单详情对象转换为购物车对象
        List<ShoppingCart> shoppingCartList = orderDetailList.stream().map(x -> {
            ShoppingCart shoppingCart = new ShoppingCart();

            // 将原订单详情里面的菜品信息重新复制到购物车对象中
            BeanUtils.copyProperties(x, shoppingCart, "id");
            shoppingCart.setUserId(userId);
            shoppingCart.setCreateTime(LocalDateTime.now());

            return shoppingCart;
        }).collect(Collectors.toList());

        // 将购物车对象批量添加到数据库
        shoppingCartMapper.insertBatch(shoppingCartList);
    }

    /**
     * 订单搜索
     *
     * @param ordersPageQueryDTO
     * @return
     */
    @Override
    public PageResult conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO) {
        PageHelper.startPage(ordersPageQueryDTO.getPage(), ordersPageQueryDTO.getPageSize());

        Page<Orders> page = orderMapper.pageQuery(ordersPageQueryDTO);

        // 部分订单状态，需要额外返回订单菜品信息，将Orders转化为OrderVO
        List<OrderVO> orderVOList = getOrderVOList(page);

        return new PageResult(page.getTotal(), orderVOList);
    }

    private List<OrderVO> getOrderVOList(Page<Orders> page) {
        // 需要返回订单菜品信息，自定义OrderVO响应结果
        List<OrderVO> orderVOList = new ArrayList<>();

        List<Orders> ordersList = page.getResult();
        if (!CollectionUtils.isEmpty(ordersList)) {
            for (Orders orders : ordersList) {
                // 将共同字段复制到OrderVO
                OrderVO orderVO = new OrderVO();
                BeanUtils.copyProperties(orders, orderVO);
                String orderDishes = getOrderDishesStr(orders);

                // 将订单菜品信息封装到orderVO中，并添加到orderVOList
                orderVO.setOrderDishes(orderDishes);
                orderVOList.add(orderVO);
            }
        }
        return orderVOList;
    }

    /**
     * 根据订单id获取菜品信息字符串
     *
     * @param orders
     * @return
     */
    private String getOrderDishesStr(Orders orders) {
        // 查询订单菜品详情信息（订单中的菜品和数量）
        List<OrderDetail> orderDetailList = orderDetailMapper.getByOrderId(orders.getId());

        // 将每一条订单菜品信息拼接为字符串（格式：宫保鸡丁*3；）
        List<String> orderDishList = orderDetailList.stream().map(x -> {
            String orderDish = x.getName() + "*" + x.getNumber() + ";";
            return orderDish;
        }).collect(Collectors.toList());

        // 将该订单对应的所有菜品信息拼接在一起
        return String.join("", orderDishList);
    }

    /**
     * 各个状态的订单数量统计
     *
     * @return
     */
    @Override
    public OrderStatisticsVO statistics() {
        // 根据状态，分别查询出待接单、已接单、已完成的订单数量
        Integer toBeConfirmed = orderMapper.countStatus(Orders.TO_BE_CONFIRMED);
        Integer confirmed = orderMapper.countStatus(Orders.CONFIRMED);
        Integer deliveryInProgress = orderMapper.countStatus(Orders.COMPLETED);

        // 将查询出的数据封装到orderStatisticsVO中响应
        OrderStatisticsVO orderStatisticsVO = new OrderStatisticsVO();
        orderStatisticsVO.setToBeConfirmed(toBeConfirmed);
        orderStatisticsVO.setConfirmed(confirmed);
        orderStatisticsVO.setDeliveryInProgress(deliveryInProgress);
        return orderStatisticsVO;
    }

    /**
     * 接单
     *
     * @param ordersConfirmDTO
     */
    @Override
    public void confirm(OrdersConfirmDTO ordersConfirmDTO) {
        Orders orders = Orders.builder()
                .id(ordersConfirmDTO.getId())
                .status(Orders.CONFIRMED)
                .build();

        orderMapper.update(orders);
    }

    /**
     * 拒单
     *
     * @param ordersRejectionDTO
     */
    @Override
    public void rejection(OrdersRejectionDTO ordersRejectionDTO) throws Exception {
        // 根据id查询订单
        Orders ordersDB = orderMapper.getById(ordersRejectionDTO.getId());

        // 订单只有存在且状态为2（待接单）才可以拒单
        if (ordersDB == null || !ordersDB.getStatus().equals(Orders.TO_BE_CONFIRMED)) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }

        //支付状态
//        Integer payStatus = ordersDB.getPayStatus();
//        if (payStatus.equals(Orders.PAID)) {
            //用户已支付，需要退款
//            Integer payMethod = ordersDB.getPayMethod();
//            if(payMethod.equals(Orders.WX)){
//                String refund = weChatPayUtil.refund(ordersDB.getNumber(), ordersDB.getNumber(), ordersDB.getAmount(), ordersDB.getAmount());
//                log.info("申请退款：{}", refund);
//            }else{
//                alipayUtil.refund(ordersDB.getNumber(),ordersDB.getAmount(),ordersRejectionDTO.getRejectionReason());
//            }
//        }

        // 拒单需要退款，根据订单id更新订单状态、拒单原因、取消时间
        Orders orders = new Orders();
        orders.setId(ordersDB.getId());
        orders.setStatus(Orders.CANCELLED);
        orders.setRejectionReason(ordersRejectionDTO.getRejectionReason());
        orders.setCancelTime(LocalDateTime.now());

        orderMapper.update(orders);
    }

    /**
     * 取消订单
     *
     * @param ordersCancelDTO
     */
    @Override
    public void cancel(OrdersCancelDTO ordersCancelDTO) throws Exception {
        // 根据id查询订单
        Orders ordersDB = orderMapper.getById(ordersCancelDTO.getId());

        //支付状态
//        Integer payStatus = ordersDB.getPayStatus();
//        if (payStatus.equals(Orders.PAID)) {
//            //用户已支付，需要退款
//            Integer payMethod = ordersDB.getPayMethod();
//            if(payMethod.equals(Orders.WX)){
//                String refund = weChatPayUtil.refund(ordersDB.getNumber(), ordersDB.getNumber(), ordersDB.getAmount(), ordersDB.getAmount());
//                log.info("申请退款：{}", refund);
//            }else{
//                alipayUtil.refund(ordersDB.getNumber(),ordersDB.getAmount(),ordersCancelDTO.getCancelReason());
//            }
//        }

        // 管理端取消订单需要退款，根据订单id更新订单状态、取消原因、取消时间
        Orders orders = new Orders();
        orders.setId(ordersCancelDTO.getId());
        orders.setStatus(Orders.CANCELLED);
        orders.setCancelReason(ordersCancelDTO.getCancelReason());
        orders.setCancelTime(LocalDateTime.now());
        orderMapper.update(orders);
    }

    /**
     * 派送订单
     *
     * @param id
     */
    @Override
    public void delivery(Long id) { }

    /**
     * 完成订单
     *
     * @param id
     */
    @Override
    public void complete(Long id) {
        // 根据id查询订单
        Orders ordersDB = orderMapper.getById(id);

        // 校验订单是否存在，并且状态为3
        if (ordersDB == null || !ordersDB.getStatus().equals(Orders.CONFIRMED)) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }

        Orders orders = new Orders();
        orders.setId(ordersDB.getId());
        // 更新订单状态,状态转为完成
        orders.setStatus(Orders.COMPLETED);
        orders.setDeliveryTime(LocalDateTime.now());

        orderMapper.update(orders);
    }

    /**
     * 客户催单
     * @param id
     */
    @Override
    public void reminder(Long id) {
        // 根据id查询订单
        Orders ordersDB = orderMapper.getById(id);

        // 校验订单是否存在
        if (ordersDB == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }

        Map<String, java.io.Serializable> map = new HashMap<>();
        map.put("type",2); //1表示来单提醒 2表示客户催单
        map.put("orderId",id);
        map.put("content","订单号：" + ordersDB.getNumber());

        //通过websocket向客户端浏览器推送消息
//        webSocketServer.sendToAllClient(JSON.toJSONString(map));
    }

    /**
     * 描述：支付宝支付结果异步通知
     *
     * @param request  请求
     * @param response 响应
     */
    @Override
    public String alipayResult(HttpServletRequest request, HttpServletResponse response) {
        try {
            boolean verifyResult = alipayUtil.isVerifyResult(request);
            if(verifyResult) {
                //验证成功
                //商户订单号
                String outTradeNo = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"),"UTF-8");

                //交易状态
                String tradeStatus = new String(request.getParameter("trade_status").getBytes("ISO-8859-1"),"UTF-8");

                log.info("商户订单号{}，交易状态{}",outTradeNo,tradeStatus);

                Orders orders = getOrderStatus(outTradeNo);
                Integer status=orders.getStatus();
                //——请根据您的业务逻辑来编写程序（以下代码仅作参考）——
                //交易结束，不可退款
                if (AlipayResultType.TRADE_FINISHED.toString().equals(tradeStatus)) {
                    //判断该笔订单是否在商户网站中已经做过处理
                    //如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
                    if(status.equals(Orders.PENDING_PAYMENT)){
                       this.paySuccess(outTradeNo);
                    }
                    //请务必判断请求时的total_fee、seller_id与通知时获取的total_fee、seller_id为一致的
                    if(status.equals(Orders.COMPLETED)||status.equals(Orders.CANCELLED)){

                    }

                    //如果有做过处理，不执行商户的业务程序

                    //注意：
                    //如果签约的是可退款协议，退款日期超过可退款期限后（如三个月可退款），支付宝系统发送该交易状态通知
                    //如果没有签约可退款协议，那么付款完成后，支付宝系统发送该交易状态通知。
                } else if (AlipayResultType.TRADE_SUCCESS.toString().equals(tradeStatus)) {
                    //交易成功
                    //判断该笔订单是否在商户网站中已经做过处理
                    //如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
                    if(status.equals(Orders.PENDING_PAYMENT)){
                        this.paySuccess(outTradeNo);
                    }
                    //请务必判断请求时的total_fee、seller_id与通知时获取的total_fee、seller_id为一致的
                    //如果有做过处理，不执行商户的业务程序

                    //注意：
                    //如果签约的是可退款协议，那么付款完成后，支付宝系统发送该交易状态通知。
                }
                return "success";
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new OrderBusinessException("支付结果异常");
        }
        return "fail";
    }

    /**
     * 描述：获取订单状态
     *
     * @param orderNumber 订单号
     * @return {@link Orders }
     */
    private Orders getOrderStatus(String orderNumber){
        Long userId = BaseContext.getCurrentId();
        return orderMapper.getByNumberAndUserId(orderNumber, userId);
    }

    /**
     * 描述：支付宝支付
     * @param httpRequest  HTTP 请求
     * @param httpResponse HTTP 响应
     * @return {@link AlipayTradeWapPayResponse }
     */
    @Override
    public void alipayPay(HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        String payNo = httpRequest.getParameter("payNo");
        Orders orders = orderMapper.getByNumber(payNo);
        try {
            alipayUtil.doPost(httpResponse,payNo,orders.getAmount());
        } catch (IOException | AlipayApiException e) {
            throw new OrderBusinessException("支付宝支付异常");
        }
    }

    /**
     * 描述：查询交易
     * @return {@link String }
     */
    @Override
    public boolean getAlipayPayDetails() {
        Long key = BaseContext.getCurrentId();
        if(ObjectUtils.isEmpty(key)){
            throw new OrderBusinessException("用户不存在");
        }
        if(!redisTemplate.hasKey(key.toString())){
            throw new OrderBusinessException("订单号不存在");
        }
        String orderNum = redisTemplate.opsForValue().get(String.valueOf(key));
        try {
            AlipayTradeQueryResponse response = alipayUtil.getAlipay(orderNum);
            if(response.isSuccess()){
                String status = response.getTradeStatus();
                if(status.equals(AlipayResultType.TRADE_FINISHED.toString())||status.equals(AlipayResultType.TRADE_SUCCESS.toString())){
                    return true;
                }
            }
        } catch (AlipayApiException e) {
            throw new OrderBusinessException("订单号:"+orderNum+"异常");
        }
        return false;
    }
}
