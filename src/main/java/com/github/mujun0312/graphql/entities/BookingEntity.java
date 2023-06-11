package com.github.mujun0312.graphql.entities;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @author xiang.zhang
 * @date 2023/6/11 9:46 下午
 */
@Data
@TableName(value = "tb_booking")
public class BookingEntity {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer userId;

    private Integer eventId;

    private Date createdAt;

    private Date updatedAt;
}
