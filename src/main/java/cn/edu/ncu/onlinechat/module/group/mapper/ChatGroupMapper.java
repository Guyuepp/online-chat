package cn.edu.ncu.onlinechat.module.group.mapper;

import cn.edu.ncu.onlinechat.module.group.entity.ChatGroup;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ChatGroupMapper {

    ChatGroup selectById(@Param("id") Long id);

    List<ChatGroup> selectByUserId(@Param("userId") Long userId);

    List<ChatGroup> searchByName(@Param("keyword") String keyword);

    int insert(ChatGroup group);

    int update(ChatGroup group);

    int deleteById(@Param("id") Long id);
}
