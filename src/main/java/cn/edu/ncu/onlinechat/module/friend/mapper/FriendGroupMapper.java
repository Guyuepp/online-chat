package cn.edu.ncu.onlinechat.module.friend.mapper;

import cn.edu.ncu.onlinechat.module.friend.entity.FriendGroup;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface FriendGroupMapper {

    FriendGroup selectById(@Param("id") Long id);

    List<FriendGroup> selectByUserId(@Param("userId") Long userId);

    int insert(FriendGroup group);

    int update(FriendGroup group);

    int deleteById(@Param("id") Long id);
}
