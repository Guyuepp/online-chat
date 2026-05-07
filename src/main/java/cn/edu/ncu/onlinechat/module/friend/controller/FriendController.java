package cn.edu.ncu.onlinechat.module.friend.controller;

import cn.edu.ncu.onlinechat.common.result.Result;
import cn.edu.ncu.onlinechat.module.friend.dto.FriendMoveDTO;
import cn.edu.ncu.onlinechat.module.friend.dto.FriendRemarkDTO;
import cn.edu.ncu.onlinechat.module.friend.service.FriendService;
import cn.edu.ncu.onlinechat.module.friend.vo.FriendVO;
import cn.edu.ncu.onlinechat.security.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "好友")
@RestController
@RequestMapping("/friends")
@RequiredArgsConstructor
public class FriendController {

    private final FriendService friendService;

    @Operation(summary = "好友列表（扁平）")
    @GetMapping
    public Result<List<FriendVO>> list() {
        return Result.success(friendService.list(SecurityUtil.currentUserId()));
    }

    @Operation(summary = "把好友移动到指定分组")
    @PutMapping("/{friendId}/group")
    public Result<Void> move(@PathVariable Long friendId, @RequestBody @Valid FriendMoveDTO dto) {
        friendService.moveToGroup(SecurityUtil.currentUserId(), friendId, dto);
        return Result.success();
    }

    @Operation(summary = "修改好友备注")
    @PutMapping("/{friendId}/remark")
    public Result<Void> remark(@PathVariable Long friendId, @RequestBody @Valid FriendRemarkDTO dto) {
        friendService.updateRemark(SecurityUtil.currentUserId(), friendId, dto);
        return Result.success();
    }

    @Operation(summary = "删除好友")
    @DeleteMapping("/{friendId}")
    public Result<Void> delete(@PathVariable Long friendId) {
        friendService.delete(SecurityUtil.currentUserId(), friendId);
        return Result.success();
    }
}
