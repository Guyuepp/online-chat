package cn.edu.ncu.onlinechat.module.group.service;

import cn.edu.ncu.onlinechat.common.result.PageResult;
import cn.edu.ncu.onlinechat.module.group.dto.CreateGroupDTO;
import cn.edu.ncu.onlinechat.module.group.dto.InviteMemberDTO;
import cn.edu.ncu.onlinechat.module.group.dto.UpdateGroupDTO;
import cn.edu.ncu.onlinechat.module.group.vo.GroupMemberVO;
import cn.edu.ncu.onlinechat.module.group.vo.GroupVO;

import java.util.List;

public interface GroupService {

    GroupVO create(Long ownerId, CreateGroupDTO dto);

    void update(Long userId, Long groupId, UpdateGroupDTO dto);

    void dismiss(Long userId, Long groupId);

    GroupVO getById(Long groupId);

    List<GroupVO> myGroups(Long userId);

    List<GroupVO> search(String keyword);

    void inviteMembers(Long inviterId, Long groupId, InviteMemberDTO dto);

    void removeMember(Long operatorId, Long groupId, Long memberId);

    void leave(Long userId, Long groupId);

    List<GroupMemberVO> members(Long groupId);

    void setRole(Long operatorId, Long groupId, Long memberId, String role);
}
