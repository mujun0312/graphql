package com.github.mujun0312.graphql.type;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author xiang.zhang
 * @date 2023/6/11 6:29 下午
 *
 * graphql中自定义的对象，一定要有与之相对应的java对象
 * 这里AuthData.java对应schema.graphqls中自定义的AuthData对象
 */
@Data
@Accessors(chain = true)
public class AuthData {

    private Integer userId;

    private String token;

    private Integer tokenExpiration;
}
