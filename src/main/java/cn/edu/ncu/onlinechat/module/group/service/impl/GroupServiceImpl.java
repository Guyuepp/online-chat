package cn.edu.ncu.onlinechat.module.group.service.impl;

import cn.edu.ncu.onlinechat.module.group.dto.CreateGroupDTO;
import cn.edu.ncu.onlinechat.module.group.dto.InviteMemberDTO;
import cn.edu.ncu.onlinechat.module.group.dto.UpdateGroupDTO;
import cn.edu.ncu.onlinechat.module.group.mapper.ChatGroupMapper;
import cn.edu.ncu.onlinechat.module.group.mapper.GroupMemberMapper;
import cn.edu.ncu.onlinechat.module.group.service.GroupService;
import cn.edu.ncu.onlinechat.module.group.vo.GroupMemberVO;
import cn.edu.ncu.onlinechat.module.group.vo.GroupVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GroupServiceImpl implements GroupService {

    private final ChatGroupMapper chatGroupMapper;
    private final GroupMemberMapper groupMemberMapper;

    @Override
    public GroupVO create(Long ownerId, CreateGroupDTO dto) {
        throw new UnsupportedOperationException("TODO: 建群 + 群主自动入群");
    }

    @Override
    public void update(Long userId, Long groupId, UpdateGroupDTO dto) {
        throw new UnsupportedOperationException("TODO: 仅群主/管理员可修改");
    }

    @Override
    public void dismiss(Long userId, Long groupId) {
        throw new UnsupportedOperationException("TODO: 仅群主可解散，清成员+消息");
    }

    @Override
    public GroupVO getById(Long groupId) {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public List<GroupVO> myGroups(Long userId) {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public List<GroupVO> search(String keyword) {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public void inviteMembers(Long inviterId, Long groupId, InviteMemberDTO dto) {
        throw new UnsupportedOperationException("TODO: 仅已入群成员可邀请，被邀请人必须是好友");
    }

    @Override
    public void removeMember(Long operatorId, Long groupId, Long memberId) {
        throw new UnsupportedOperationException("TODO: 仅群主/管理员可踢人");
    }

    @Override
    public void leave(Long userId, Long groupId) {
        throw new UnsupportedOperationException("TODO: 群主不能直接退，需先转让或解散");
    }

    @Override
    public List<GroupMemberVO> members(Long groupId) {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public void setRole(Long operatorId, Long groupId, Long memberId, String role) {
        throw new UnsupportedOperationException("TODO: 仅群主可设置管理员");
    }
}
