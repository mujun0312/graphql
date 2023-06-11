package com.github.mujun0312.graphql.custom;

import com.github.mujun0312.graphql.entities.UserEntity;
import lombok.Data;

/**
 * @author xiang.zhang
 * @date 2023/6/11 7:17 下午
 */
@Data
public class AuthContext {

    private UserEntity userEntity;

    private boolean tokenInvalid;

    // 确保token有效
    public void ensureAuthenticated() {
        if (tokenInvalid) {
            throw new RuntimeException("token无效");
        }
        // 如果token有效，但是userEntity == null，这种情况意味着，获取到了有效的token
        // 但是获取的方式很可能是通过接口的方式，而非登录的方式
        if (userEntity == null) {
            throw new RuntimeException("请先登录");
        }

    }
}
