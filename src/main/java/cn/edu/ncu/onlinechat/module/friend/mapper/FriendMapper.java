package cn.edu.ncu.onlinechat.module.friend.mapper;

import cn.edu.ncu.onlinechat.module.friend.entity.Friend;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface FriendMapper {

    Friend selectById(@Param("id") Long id);

    Friend selectByUserAndFriend(@Param("userId") Long userId, @Param("friendId") Long friendId);

    List<Friend> selectByUserId(@Param("userId") Long userId);

    List<Friend> selectByGroupId(@Param("groupId") Long groupId);

    int insert(Friend friend);

    int updateGroup(@Param("id") Long id, @Param("groupId") Long groupId);

    int updateRemark(@Param("id") Long id, @Param("remark") String remark);

    int deleteById(@Param("id") Long id);

    int deleteByUserAndFriend(@Param("userId") Long userId, @Param("friendId") Long friendId);
}
