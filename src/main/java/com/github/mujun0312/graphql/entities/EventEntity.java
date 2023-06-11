package com.github.mujun0312.graphql.entities;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @author xiang.zhang
 * @date 2023/6/9 5:34 下午
 */
@Data
@TableName(value = "tb_event")
public class EventEntity {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private String title;

    private String description;

    private Float price;

    private Integer creatorId;

    private Date date;
}
