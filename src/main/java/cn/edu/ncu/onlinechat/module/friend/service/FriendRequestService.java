package cn.edu.ncu.onlinechat.module.friend.service;

import cn.edu.ncu.onlinechat.module.friend.dto.FriendRequestSendDTO;
import cn.edu.ncu.onlinechat.module.friend.vo.FriendRequestVO;

import java.util.List;

public interface FriendRequestService {

    Long send(Long fromUserId, FriendRequestSendDTO dto);

    void resend(Long fromUserId, Long requestId);

    void accept(Long toUserId, Long requestId);

    void reject(Long toUserId, Long requestId);

    List<FriendRequestVO> incoming(Long userId);

    List<FriendRequestVO> outgoing(Long userId);
}
