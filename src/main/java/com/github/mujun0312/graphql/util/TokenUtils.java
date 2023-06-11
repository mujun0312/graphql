package com.github.mujun0312.graphql.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Date;

/**
 * @author xiang.zhang
 * @date 2023/6/11 6:06 下午
 *
 * token工具类
 */
public class TokenUtils {

    // token颁发者，自定义
    private static final String ISS_USER = "mujun0312";

    private static final String USER_ID = "userId";

    private static final int MILLI_SECONDS_IN_HOUR = 60 * 60 * 1000;

    // 自定义加密算法，用HMAC256，token的信息保存到my_secret_key
    // 解密也是通过该key来进行解密
    // 特点：可以看到token，但是不能篡改token，一旦篡改，用该key解密就会失败
    private static final Algorithm algorithm = Algorithm.HMAC256("my_secret_key");

    public static String getToken(Integer userId, Integer expirationInHour) {
        return JWT.create()
                .withIssuer(ISS_USER)
                .withClaim(USER_ID, userId) // 将自定义的信息编织到token中
                .withExpiresAt(new Date(System.currentTimeMillis() + expirationInHour * MILLI_SECONDS_IN_HOUR))
                .sign(algorithm);
    }

    public static Integer verifyToken(String token) {
        JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer(ISS_USER)
                .build();
        // 如果这个token不合法，会抛出异常
        DecodedJWT decodedJWT = verifier.verify(token);
        return decodedJWT.getClaim(USER_ID).asInt();
    }
}
