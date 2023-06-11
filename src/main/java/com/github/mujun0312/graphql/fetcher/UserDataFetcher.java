package com.github.mujun0312.graphql.fetcher;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.mujun0312.graphql.custom.AuthContext;
import com.github.mujun0312.graphql.entities.BookingEntity;
import com.github.mujun0312.graphql.entities.EventEntity;
import com.github.mujun0312.graphql.entities.UserEntity;
import com.github.mujun0312.graphql.mapper.BookingEntityMapper;
import com.github.mujun0312.graphql.mapper.EventEntityMapper;
import com.github.mujun0312.graphql.mapper.UserEntityMapper;
import com.github.mujun0312.graphql.type.*;
import com.github.mujun0312.graphql.util.DateUtils;
import com.github.mujun0312.graphql.util.TokenUtils;
import com.netflix.graphql.dgs.*;
import com.netflix.graphql.dgs.context.DgsContext;
import graphql.schema.DataFetchingEnvironment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author xiang.zhang
 * @date 2023/6/11 4:31 下午
 */
@Slf4j
@DgsComponent
public class UserDataFetcher {

    @Autowired
    private UserEntityMapper userEntityMapper;

    @Autowired
    private EventEntityMapper eventEntityMapper;

    @Autowired
    private BookingEntityMapper bookingEntityMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @DgsQuery
    public List<User> users(DataFetchingEnvironment dfe) {
        // 这里通过调用DgsContext的getCustomContext()方法，
        // 是通过自定义的Component - AuthContextBuilder调用它自定义的build方法生成的自定义的AuthContext对象
        AuthContext authContext = DgsContext.getCustomContext(dfe);
        authContext.ensureAuthenticated();

        List<UserEntity> userEntities = userEntityMapper.selectList(null);
        return userEntities.stream().map(this::toUser).collect(Collectors.toList());
    }

    @DgsQuery
    public AuthData login(@InputArgument LoginInput loginInput) {
        UserEntity userEntity = findUserByEmail(loginInput.getEmail());
        if (userEntity == null) {
            throw new RuntimeException("账号不存在");
        }
        // 使用passwordEncoder的matches方法，来校验用户输入的密码和数据库保存的加密后的密码是否一致
        boolean matches = passwordEncoder.matches(loginInput.getPassword(), userEntity.getPassword());
        if (!matches) {
            throw new RuntimeException("密码不正确");
        }
        // 获取token
        String token = TokenUtils.getToken(userEntity.getId(), 1);
        return new AuthData()
                .setUserId(userEntity.getId())
                .setToken(token)
                .setTokenExpiration(1);
    }

    @DgsMutation
    public User createUser(@InputArgument UserInput userInput) {
        validateEmail(userInput);

        UserEntity entity = new UserEntity();
        entity.setEmail(userInput.getEmail());
        entity.setPassword(passwordEncoder.encode(userInput.getPassword()));

        userEntityMapper.insert(entity);

        return toUser(entity);
    }

    @DgsData(parentType = "User", field = "createdEvents")
    public List<Event> createdEvents(DgsDataFetchingEnvironment dfe) {
        User user = dfe.getSource();

        QueryWrapper<EventEntity> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(EventEntity::getCreatorId, user.getId());
        List<EventEntity> eventEntities = eventEntityMapper.selectList(wrapper);

        List<Event> list = new ArrayList<>();
        eventEntities.forEach(entity -> {
            Event event = toEvent(entity);
            list.add(event);
        });

        return list;
    }

    @DgsData(parentType = "User", field = "bookings")
    public List<Booking> bookings(DgsDataFetchingEnvironment dfe) {
        User user = dfe.getSource();

        QueryWrapper<BookingEntity> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(BookingEntity::getUserId, user.getId());
        List<BookingEntity> bookingEntities = bookingEntityMapper.selectList(wrapper);
        return bookingEntities.stream().map(this::toBooking).collect(Collectors.toList());
    }

    private void validateEmail(UserInput userInput) {
        QueryWrapper<UserEntity> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(UserEntity::getEmail, userInput.getEmail());
        if (userEntityMapper.selectCount(wrapper) >= 1) {
            throw new RuntimeException("该email地址已被使用!");
        }
    }

    private User toUser(UserEntity entity) {
        User user = new User();
        user.setId(entity.getId());
        user.setEmail(entity.getEmail());
        return user;
    }

    private Event toEvent(EventEntity entity) {
        Event event = new Event();

        event.setId(String.valueOf(entity.getId()));
        event.setTitle(entity.getTitle());
        event.setDescription(entity.getDescription());
        event.setPrice(entity.getPrice());
        event.setDate(DateUtils.getSdf("yyyy-MM-dd HH:mm:ss").format(entity.getDate()));
        event.setCreatorId(entity.getCreatorId());

        return event;
    }

    private Booking toBooking(BookingEntity entity) {
        Booking booking = new Booking();
        booking.setId(entity.getId());
        booking.setUserId(entity.getUserId());
        booking.setEventId(entity.getEventId());
        booking.setCreatedAt(DateUtils.getSdf("yyyy-MM-dd HH:mm:ss").format(entity.getCreatedAt()));
        booking.setUpdatedAt(DateUtils.getSdf("yyyy-MM-dd HH:mm:ss").format(entity.getUpdatedAt()));
        return booking;
    }

    private UserEntity findUserByEmail(String email) {
        QueryWrapper<UserEntity> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(UserEntity::getEmail, email);
        return userEntityMapper.selectOne(wrapper);
    }
}
