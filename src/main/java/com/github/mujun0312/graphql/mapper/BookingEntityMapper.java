package com.github.mujun0312.graphql.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.mujun0312.graphql.entities.BookingEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author xiang.zhang
 * @date 2023/6/11 9:49 下午
 */
@Mapper
public interface BookingEntityMapper extends BaseMapper<BookingEntity> {
}
