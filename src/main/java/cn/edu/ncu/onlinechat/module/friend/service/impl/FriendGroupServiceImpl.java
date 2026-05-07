package cn.edu.ncu.onlinechat.module.friend.service.impl;

import cn.edu.ncu.onlinechat.module.friend.dto.FriendGroupDTO;
import cn.edu.ncu.onlinechat.module.friend.mapper.FriendGroupMapper;
import cn.edu.ncu.onlinechat.module.friend.mapper.FriendMapper;
import cn.edu.ncu.onlinechat.module.friend.service.FriendGroupService;
import cn.edu.ncu.onlinechat.module.friend.vo.FriendGroupVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FriendGroupServiceImpl implements FriendGroupService {

    private final FriendGroupMapper friendGroupMapper;
    private final FriendMapper friendMapper;

    @Override
    public List<FriendGroupVO> listWithFriends(Long userId) {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public Long create(Long userId, FriendGroupDTO dto) {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public void rename(Long userId, Long groupId, FriendGroupDTO dto) {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public void delete(Long userId, Long groupId) {
        throw new UnsupportedOperationException("TODO: 删除分组前要把组内好友移到默认分组");
    }
}
