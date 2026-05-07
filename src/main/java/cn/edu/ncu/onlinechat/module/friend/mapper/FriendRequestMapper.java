package cn.edu.ncu.onlinechat.module.friend.mapper;

import cn.edu.ncu.onlinechat.module.friend.entity.FriendRequest;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface FriendRequestMapper {

    FriendRequest selectById(@Param("id") Long id);

    FriendRequest selectLatest(@Param("fromUserId") Long fromUserId, @Param("toUserId") Long toUserId);

    List<FriendRequest> selectIncoming(@Param("toUserId") Long toUserId);

    List<FriendRequest> selectOutgoing(@Param("fromUserId") Long fromUserId);

    int insert(FriendRequest request);

    int updateStatus(@Param("id") Long id, @Param("status") String status);
}
