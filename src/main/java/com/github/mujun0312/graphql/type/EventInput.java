package com.github.mujun0312.graphql.type;

import lombok.Data;

/**
 * @author xiang.zhang
 * @date 2023/6/9 4:34 下午
 */
@Data
public class EventInput {

    private String title;

    private String description;

    private Float price;

    private String date;
}
