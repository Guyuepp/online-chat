package cn.edu.ncu.onlinechat.module.chat.mapper;

import cn.edu.ncu.onlinechat.module.chat.entity.Message;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface MessageMapper {

    Message selectById(@Param("id") Long id);

    List<Message> selectHistory(@Param("chatType") String chatType,
                                @Param("targetId") Long targetId,
                                @Param("before") LocalDateTime before,
                                @Param("limit") int limit);

    List<Message> selectAllHistory(@Param("chatType") String chatType,
                                   @Param("targetId") Long targetId);

    int insert(Message message);

    int markRead(@Param("chatType") String chatType,
                 @Param("targetId") Long targetId,
                 @Param("toUserId") Long toUserId);
}
