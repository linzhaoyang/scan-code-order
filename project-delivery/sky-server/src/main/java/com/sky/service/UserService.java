package com.sky.service;

import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;

public interface UserService {

    /**
     * 微信登录
     * @param userLoginDTO
     * @return
     */
    User wxLogin(UserLoginDTO userLoginDTO);

    /**
     * 描述：获取用户手机号
     *
     * @param userId 用户
     * @return {@link String }
     */
    String getPhone(Long userId);
}
