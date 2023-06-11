package com.github.mujun0312.graphql.fetcher;

import com.github.mujun0312.graphql.custom.AuthContext;
import com.github.mujun0312.graphql.entities.BookingEntity;
import com.github.mujun0312.graphql.entities.EventEntity;
import com.github.mujun0312.graphql.entities.UserEntity;
import com.github.mujun0312.graphql.mapper.BookingEntityMapper;
import com.github.mujun0312.graphql.mapper.EventEntityMapper;
import com.github.mujun0312.graphql.mapper.UserEntityMapper;
import com.github.mujun0312.graphql.type.Booking;
import com.github.mujun0312.graphql.type.Event;
import com.github.mujun0312.graphql.type.User;
import com.github.mujun0312.graphql.util.DateUtils;
import com.netflix.graphql.dgs.*;
import com.netflix.graphql.dgs.context.DgsContext;
import graphql.schema.DataFetchingEnvironment;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author xiang.zhang
 * @date 2023/6/11 10:04 下午
 */
@DgsComponent
public class BookingDataFetcher {

    @Autowired
    private BookingEntityMapper bookingEntityMapper;

    @Autowired
    private EventEntityMapper eventEntityMapper;

    @Autowired
    private UserEntityMapper userEntityMapper;

    @DgsQuery
    public List<Booking> bookings() {
        List<BookingEntity> bookingEntities = bookingEntityMapper.selectList(null);
        return bookingEntities.stream().map(this::toBooking).collect(Collectors.toList());
    }

    @DgsMutation
    public Booking bookEvent(@InputArgument Integer eventId, DataFetchingEnvironment dfe) {
        // 确保用户是认证过的
        AuthContext authContext = DgsContext.getCustomContext(dfe);
        authContext.ensureAuthenticated();

        UserEntity userEntity = authContext.getUserEntity();
        BookingEntity bookingEntity = new BookingEntity();
        bookingEntity.setUserId(userEntity.getId());
        bookingEntity.setEventId(eventId);
        bookingEntity.setCreatedAt(new Date());
        bookingEntity.setUpdatedAt(new Date());

        bookingEntityMapper.insert(bookingEntity);
        return toBooking(bookingEntity);
    }

    @DgsMutation
    public Event cancelBooking(@InputArgument Integer bookingId, DataFetchingEnvironment dfe) {
        // 确保用户是认证过的
        AuthContext authContext = DgsContext.getCustomContext(dfe);
        authContext.ensureAuthenticated();

        BookingEntity bookingEntity = bookingEntityMapper.selectById(bookingId);
        Integer userId = bookingEntity.getUserId();
        UserEntity userEntity = authContext.getUserEntity();
        if (!Objects.equals(userId, userEntity.getId())) {
            throw new RuntimeException("不允许cancel其他人的booking");
        }

        bookingEntityMapper.deleteById(bookingId);

        Integer eventId = bookingEntity.getEventId();
        EventEntity eventEntity = eventEntityMapper.selectById(eventId);
        return toEvent(eventEntity);
    }

    @DgsData(parentType = "Booking", field = "user")
    public User user(DgsDataFetchingEnvironment dfe) {
        Booking booking = dfe.getSource();
        UserEntity userEntity = userEntityMapper.selectById(booking.getUserId());
        return toUser(userEntity);
    }

    @DgsData(parentType = "Booking", field = "event")
    public Event event(DgsDataFetchingEnvironment dfe) {
        Booking booking = dfe.getSource();
        EventEntity eventEntity = eventEntityMapper.selectById(booking.getEventId());
        return toEvent(eventEntity);
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

    private User toUser(UserEntity entity) {
        User user = new User();
        user.setId(entity.getId());
        user.setEmail(entity.getEmail());
        return user;
    }
}
