package com.github.mujun0312.graphql.type;

import lombok.Data;

/**
 * @author xiang.zhang
 * @date 2023/6/9 4:30 下午
 *
 * graphql中自定义的对象，一定要有与之相对应的java对象
 * 该java类对应了schema中定义的Event对象
 */
@Data
public class Event {

    private String id;

    private String title;

    private String description;

    private Float price;

    private String date;

    private Integer creatorId;

    private User creator;
}
