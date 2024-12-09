package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sky.constant.MessageConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.exception.LoginFailedException;
import com.sky.mapper.UserMapper;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    //微信服务接口地址
    public static final String WX_LOGIN = "https://api.weixin.qq.com/sns/jscode2session";

    //获取全局唯一后台接口调用凭据access_token
    public static final String WX_LOGIN_TOKEN = "https://api.weixin.qq.com/cgi-bin/token";

    //手机号地址
    public static final String WX_LOGIN_PHONE="https://api.weixin.qq.com/wxa/business/getuserphonenumber?access_token=";

    @Autowired
    private WeChatProperties weChatProperties;
    @Autowired
    private UserMapper userMapper;

    /**
     * 微信登录
     * @param userLoginDTO
     * @return
     */
    @Override
    public User wxLogin(UserLoginDTO userLoginDTO) {
        String openid = getOpenid(userLoginDTO.getCode());

        //判断openid是否为空，如果为空表示登录失败，抛出业务异常
        if(openid == null){
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
        }

        //判断当前用户是否为新用户
        User user = userMapper.getByOpenid(openid);

        //如果是新用户，自动完成注册
        if(user == null){
            user = User.builder()
                    .openid(openid)
                    .name(userLoginDTO.getName())
                    .createTime(LocalDateTime.now())
                    .build();
            userMapper.insert(user);
        }

        //返回这个用户对象
        return user;
    }

    /**
     * 调用微信接口服务，获取微信用户的openid
     * @param code
     * @return
     */
    private String getOpenid(String code){
        //调用微信接口服务，获得当前微信用户的openid
        Map<String, String> map = new HashMap<>();
        map.put("appid",weChatProperties.getAppid());
        map.put("secret",weChatProperties.getSecret());
        map.put("js_code",code);
        map.put("grant_type","authorization_code");
        String json = HttpClientUtil.doGet(WX_LOGIN, map);

        JSONObject jsonObject = JSON.parseObject(json);
        String openid = jsonObject.getString("openid");
        return openid;
    }

    @Override
    public String getPhone(Long userId) {
        User user = userMapper.selectById(userId);
        return user.getPhone();
    }

    /**
     * 获取全局唯一后台接口调用凭据access_token
     * @param
     * @return
     */
    private String getAccessToken(){
        Map<String, String> map = new HashMap<>();
        map.put("grant_type","client_credential");
        map.put("appid",weChatProperties.getAppid());
        map.put("secret",weChatProperties.getSecret());
        String json = HttpClientUtil.doGet(WX_LOGIN_TOKEN, map);
        JSONObject jsonObject = JSON.parseObject(json);
        String accessToken = jsonObject.getString("access_token");
        return accessToken;
    }


    /**
     * 描述：调用微信接口服务，获取微信用户的手机号码
     *
     * @param accessToken 访问令牌
     * @param code        法典
     * @return {@link String }
     */
    private String getPhoneNumber(String accessToken,String code){
        //调用微信接口服务，获得当前微信用户的openid
        String url=WX_LOGIN_PHONE+accessToken;
        Map<String, String> map = new HashMap<>();
        map.put("code",code);
        String json = null;
        try {
            json = HttpClientUtil.doPost4Json(WX_LOGIN, map);
        } catch (IOException e) {
            e.printStackTrace();
        }

        JSONObject jsonObject = JSON.parseObject(json);
        JSONObject phoneInfo=jsonObject.getJSONObject("phone_info");
        String phoneNumber = phoneInfo.get("phoneNumber").toString();
        return phoneNumber;
    }
}
