package cn.edu.ncu.onlinechat.module.friend.service;

import cn.edu.ncu.onlinechat.module.friend.dto.FriendMoveDTO;
import cn.edu.ncu.onlinechat.module.friend.dto.FriendRemarkDTO;
import cn.edu.ncu.onlinechat.module.friend.vo.FriendVO;

import java.util.List;

public interface FriendService {

    List<FriendVO> list(Long userId);

    void moveToGroup(Long userId, Long friendId, FriendMoveDTO dto);

    void updateRemark(Long userId, Long friendId, FriendRemarkDTO dto);

    void delete(Long userId, Long friendId);
}
