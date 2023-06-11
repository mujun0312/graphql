package com.github.mujun0312.graphql.type;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xiang.zhang
 * @date 2023/6/11 4:26 下午
 *
 * graphql中自定义的对象，一定要有与之相对应的java对象
 * 这里User.java对应schema.graphqls中自定义的User对象
 */
@Data
public class User {

    private Integer id;

    private String email;

    private String password;

    private List<Event> createdEvents = new ArrayList<>();

    private List<Booking> bookings = new ArrayList<>();
}
