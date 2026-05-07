package cn.edu.ncu.onlinechat.module.friend.service;

import cn.edu.ncu.onlinechat.module.friend.dto.FriendGroupDTO;
import cn.edu.ncu.onlinechat.module.friend.vo.FriendGroupVO;

import java.util.List;

public interface FriendGroupService {

    List<FriendGroupVO> listWithFriends(Long userId);

    Long create(Long userId, FriendGroupDTO dto);

    void rename(Long userId, Long groupId, FriendGroupDTO dto);

    void delete(Long userId, Long groupId);
}
