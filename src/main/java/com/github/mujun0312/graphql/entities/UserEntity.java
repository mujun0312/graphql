package com.github.mujun0312.graphql.entities;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author xiang.zhang
 * @date 2023/6/11 4:11 下午
 */
@Data
@TableName(value = "tb_user")
public class UserEntity {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private String email;

    private String password;
}
