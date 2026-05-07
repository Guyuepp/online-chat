package cn.edu.ncu.onlinechat.module.friend.service.impl;

import cn.edu.ncu.onlinechat.module.friend.dto.FriendMoveDTO;
import cn.edu.ncu.onlinechat.module.friend.dto.FriendRemarkDTO;
import cn.edu.ncu.onlinechat.module.friend.mapper.FriendMapper;
import cn.edu.ncu.onlinechat.module.friend.service.FriendService;
import cn.edu.ncu.onlinechat.module.friend.vo.FriendVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FriendServiceImpl implements FriendService {

    private final FriendMapper friendMapper;

    @Override
    public List<FriendVO> list(Long userId) {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public void moveToGroup(Long userId, Long friendId, FriendMoveDTO dto) {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public void updateRemark(Long userId, Long friendId, FriendRemarkDTO dto) {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public void delete(Long userId, Long friendId) {
        throw new UnsupportedOperationException("TODO: 删除好友需要双向删除");
    }
}
