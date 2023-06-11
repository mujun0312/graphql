package com.github.mujun0312.graphql.type;

import lombok.Data;

/**
 * @author xiang.zhang
 * @date 2023/6/11 9:59 下午
 */
@Data
public class Booking {

    private Integer id;

    private User user;

    private Integer userId;

    private Event event;

    private Integer eventId;

    private String createdAt;

    private String updatedAt;
}
