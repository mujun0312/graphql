package com.github.mujun0312.graphql.fetcher;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.mujun0312.graphql.custom.AuthContext;
import com.github.mujun0312.graphql.entities.EventEntity;
import com.github.mujun0312.graphql.entities.UserEntity;
import com.github.mujun0312.graphql.mapper.EventEntityMapper;
import com.github.mujun0312.graphql.mapper.UserEntityMapper;
import com.github.mujun0312.graphql.type.Event;
import com.github.mujun0312.graphql.type.EventInput;
import com.github.mujun0312.graphql.type.User;
import com.github.mujun0312.graphql.util.DateUtils;
import com.netflix.graphql.dgs.*;
import com.netflix.graphql.dgs.context.DgsContext;
import graphql.schema.DataFetchingEnvironment;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author xiang.zhang
 * @date 2023/6/9 3:55 下午
 *
 * 需要在fetcher内实现Query和Mutation
 */
@DgsComponent
public class EventDataFetcher {

    /*---------------------------简单类型的输入和输出---------------------------------*/

    /**
     * 对schema中定义的Query类型的实现
     * 注意：方法名、入参、出参一定要跟schema中的定义保持一致
     */
//    @DgsQuery
//    public List<String> events() {
//        return Arrays.asList("event-1", "event-2", "event-3");
//    }

    /**
     * 对schema中定义的Mutation类型的实现
     * 注意：方法名、入参、出参一定要跟schema中的定义保持一致
     */
//    @DgsMutation
//    public String createEvent(@InputArgument String name) {
//        return name + " created";
//    }

    /*---------------------------对象类型的输入和输出---------------------------------*/

    @Autowired
    private EventEntityMapper eventEntityMapper;

    @Autowired
    private UserEntityMapper userEntityMapper;

    @DgsQuery
    public List<Event> events() {
        List<Event> list = new ArrayList<>();
        QueryWrapper<EventEntity> wrapper = new QueryWrapper<>();
        List<EventEntity> entityList = eventEntityMapper.selectList(wrapper);
        entityList.forEach(entity -> {
            Event event = toEvent(entity);
            // 有了@DgsData这个注解标注的方法后，就不再需要手动填充Event对象中的creator属性了
            // populateCreator(event, entity.getCreatorId());
            list.add(event);
        });
        return list;
    }

    /**
     * 注意：
     * 1.只有认证过的用户，才能创建Event对象
     *   意味着输入参数需要有些调整
     */
    @DgsMutation
    public Event createEvent(@InputArgument(name = "eventInput") EventInput input,
                             DataFetchingEnvironment dfe) throws ParseException {
        // 这里通过调用DgsContext的getCustomContext()方法，
        // 是通过自定义的Component - AuthContextBuilder调用它自定义的build方法生成的自定义的AuthContext对象
        AuthContext authContext = DgsContext.getCustomContext(dfe);
        authContext.ensureAuthenticated();

        EventEntity entity = new EventEntity();

        entity.setTitle(input.getTitle());
        entity.setDescription(input.getDescription());
        entity.setPrice(input.getPrice());
        // entity.setCreatorId(input.getCreatorId());
        // 不再需要从入参中手动输入creatorId，认证信息中包含了UserEntity的id信息
        entity.setCreatorId(authContext.getUserEntity().getId());
        entity.setDate(DateUtils.getSdf("yyyy-MM-dd HH:mm:ss").parse(input.getDate()));

        eventEntityMapper.insert(entity);

        Event event = toEvent(entity);
        // 有了@DgsData这个注解标注的方法后，就不再需要手动填充Event对象中的creator属性了
        // populateCreator(event, input.getCreatorId());
        return event;
    }

    @DgsData(parentType = "Event", field = "creator")
    public User creator(DgsDataFetchingEnvironment dfe) {
        Event event = dfe.getSource();

        UserEntity userEntity = userEntityMapper.selectById(event.getCreatorId());
        return toUser(userEntity);
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

//    private void populateCreator(Event event, Integer creatorId) {
//        UserEntity userEntity = userEntityMapper.selectById(creatorId);
//        User user = toUser(userEntity);
//        event.setCreator(user);
//    }

    private User toUser(UserEntity entity) {
        User user = new User();
        user.setId(entity.getId());
        user.setEmail(entity.getEmail());
        return user;
    }
}
