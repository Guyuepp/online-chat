package cn.edu.ncu.onlinechat.module.group.controller;

import cn.edu.ncu.onlinechat.common.result.Result;
import cn.edu.ncu.onlinechat.module.group.dto.CreateGroupDTO;
import cn.edu.ncu.onlinechat.module.group.dto.InviteMemberDTO;
import cn.edu.ncu.onlinechat.module.group.dto.UpdateGroupDTO;
import cn.edu.ncu.onlinechat.module.group.service.GroupService;
import cn.edu.ncu.onlinechat.module.group.vo.GroupMemberVO;
import cn.edu.ncu.onlinechat.module.group.vo.GroupVO;
import cn.edu.ncu.onlinechat.security.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "群聊")
@RestController
@RequestMapping("/groups")
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;

    @Operation(summary = "建群")
    @PostMapping
    public Result<GroupVO> create(@RequestBody @Valid CreateGroupDTO dto) {
        return Result.success(groupService.create(SecurityUtil.currentUserId(), dto));
    }

    @Operation(summary = "修改群资料")
    @PutMapping("/{groupId}")
    public Result<Void> update(@PathVariable Long groupId, @RequestBody @Valid UpdateGroupDTO dto) {
        groupService.update(SecurityUtil.currentUserId(), groupId, dto);
        return Result.success();
    }

    @Operation(summary = "解散群")
    @DeleteMapping("/{groupId}")
    public Result<Void> dismiss(@PathVariable Long groupId) {
        groupService.dismiss(SecurityUtil.currentUserId(), groupId);
        return Result.success();
    }

    @Operation(summary = "我的群列表")
    @GetMapping("/mine")
    public Result<List<GroupVO>> myGroups() {
        return Result.success(groupService.myGroups(SecurityUtil.currentUserId()));
    }

    @Operation(summary = "搜索群")
    @GetMapping("/search")
    public Result<List<GroupVO>> search(@RequestParam String keyword) {
        return Result.success(groupService.search(keyword));
    }

    @Operation(summary = "群详情")
    @GetMapping("/{groupId}")
    public Result<GroupVO> detail(@PathVariable Long groupId) {
        return Result.success(groupService.getById(groupId));
    }

    @Operation(summary = "群成员列表")
    @GetMapping("/{groupId}/members")
    public Result<List<GroupMemberVO>> members(@PathVariable Long groupId) {
        return Result.success(groupService.members(groupId));
    }

    @Operation(summary = "邀请入群")
    @PostMapping("/{groupId}/members")
    public Result<Void> invite(@PathVariable Long groupId, @RequestBody @Valid InviteMemberDTO dto) {
        groupService.inviteMembers(SecurityUtil.currentUserId(), groupId, dto);
        return Result.success();
    }

    @Operation(summary = "踢出成员")
    @DeleteMapping("/{groupId}/members/{memberId}")
    public Result<Void> removeMember(@PathVariable Long groupId, @PathVariable Long memberId) {
        groupService.removeMember(SecurityUtil.currentUserId(), groupId, memberId);
        return Result.success();
    }

    @Operation(summary = "退出群聊")
    @PostMapping("/{groupId}/leave")
    public Result<Void> leave(@PathVariable Long groupId) {
        groupService.leave(SecurityUtil.currentUserId(), groupId);
        return Result.success();
    }

    @Operation(summary = "设置成员角色")
    @PutMapping("/{groupId}/members/{memberId}/role")
    public Result<Void> setRole(@PathVariable Long groupId, @PathVariable Long memberId,
                                @RequestParam String role) {
        groupService.setRole(SecurityUtil.currentUserId(), groupId, memberId, role);
        return Result.success();
    }
}
