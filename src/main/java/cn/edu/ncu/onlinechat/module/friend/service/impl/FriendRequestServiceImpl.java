package cn.edu.ncu.onlinechat.module.friend.service.impl;

import cn.edu.ncu.onlinechat.module.friend.dto.FriendRequestSendDTO;
import cn.edu.ncu.onlinechat.module.friend.mapper.FriendMapper;
import cn.edu.ncu.onlinechat.module.friend.mapper.FriendRequestMapper;
import cn.edu.ncu.onlinechat.module.friend.service.FriendRequestService;
import cn.edu.ncu.onlinechat.module.friend.vo.FriendRequestVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FriendRequestServiceImpl implements FriendRequestService {

    private final FriendRequestMapper friendRequestMapper;
    private final FriendMapper friendMapper;

    @Override
    public Long send(Long fromUserId, FriendRequestSendDTO dto) {
        throw new UnsupportedOperationException("TODO: 发申请前校验目标用户存在、未被拉黑、未已是好友、无待处理申请");
    }

    @Override
    public void resend(Long fromUserId, Long requestId) {
        throw new UnsupportedOperationException("TODO: 复用旧记录改 message 并刷新时间，或新建一条");
    }

    @Override
    public void accept(Long toUserId, Long requestId) {
        throw new UnsupportedOperationException("TODO: 把状态改 ACCEPTED + 双向插好友关系（默认分组）");
    }

    @Override
    public void reject(Long toUserId, Long requestId) {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public List<FriendRequestVO> incoming(Long userId) {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public List<FriendRequestVO> outgoing(Long userId) {
        throw new UnsupportedOperationException("TODO");
    }
}
