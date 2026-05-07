package cn.edu.ncu.onlinechat.module.friend.controller;

import cn.edu.ncu.onlinechat.common.result.Result;
import cn.edu.ncu.onlinechat.module.friend.dto.FriendGroupDTO;
import cn.edu.ncu.onlinechat.module.friend.service.FriendGroupService;
import cn.edu.ncu.onlinechat.module.friend.vo.FriendGroupVO;
import cn.edu.ncu.onlinechat.security.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "好友分组")
@RestController
@RequestMapping("/friend-groups")
@RequiredArgsConstructor
public class FriendGroupController {

    private final FriendGroupService friendGroupService;

    @Operation(summary = "获取分组及组内好友")
    @GetMapping
    public Result<List<FriendGroupVO>> list() {
        return Result.success(friendGroupService.listWithFriends(SecurityUtil.currentUserId()));
    }

    @Operation(summary = "新建分组")
    @PostMapping
    public Result<Long> create(@RequestBody @Valid FriendGroupDTO dto) {
        return Result.success(friendGroupService.create(SecurityUtil.currentUserId(), dto));
    }

    @Operation(summary = "重命名分组")
    @PutMapping("/{groupId}")
    public Result<Void> rename(@PathVariable Long groupId, @RequestBody @Valid FriendGroupDTO dto) {
        friendGroupService.rename(SecurityUtil.currentUserId(), groupId, dto);
        return Result.success();
    }

    @Operation(summary = "删除分组")
    @DeleteMapping("/{groupId}")
    public Result<Void> delete(@PathVariable Long groupId) {
        friendGroupService.delete(SecurityUtil.currentUserId(), groupId);
        return Result.success();
    }
}
