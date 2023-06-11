package com.github.mujun0312.graphql.custom;

import com.github.mujun0312.graphql.entities.UserEntity;
import com.github.mujun0312.graphql.mapper.UserEntityMapper;
import com.github.mujun0312.graphql.util.TokenUtils;
import com.netflix.graphql.dgs.context.DgsCustomContextBuilderWithRequest;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.WebRequest;

import java.util.Map;

/**
 * @author xiang.zhang
 * @date 2023/6/11 7:21 下午
 */
@Slf4j
@Component
public class AuthContextBuilder implements DgsCustomContextBuilderWithRequest {

    @Autowired
    private UserEntityMapper userEntityMapper;

    private static final String AUTHORIZATION_HEADER = "Authorization";

    @Override
    public Object build(@Nullable Map map, @Nullable HttpHeaders httpHeaders, @Nullable WebRequest webRequest) {
        log.info("Building auth context...");
        AuthContext authContext = new AuthContext();
        if (!httpHeaders.containsKey(AUTHORIZATION_HEADER)) {
            // 如果用户认证了，会将Authorization编织到httpHeaders请求头
            log.info("用户未认证");
            return authContext;
        }

        String authorization = httpHeaders.getFirst(AUTHORIZATION_HEADER);
        String token = authorization.replace("Bearer ", "");

        // 此处获取userId是为了从数据库中获取到认证的用户信息
        Integer userId;
        try {
            userId = TokenUtils.verifyToken(token);
        } catch (Exception e) {
            authContext.setTokenInvalid(true);
            return authContext;
        }

        UserEntity userEntity = userEntityMapper.selectById(userId);
        if (userEntity == null) {
            // 可能secret泄露，有人伪造了token
            authContext.setTokenInvalid(true);
            return authContext;
        }
        authContext.setUserEntity(userEntity);
        log.info("用户认证成功，userId : {}", userId);

        return authContext;
    }
}
