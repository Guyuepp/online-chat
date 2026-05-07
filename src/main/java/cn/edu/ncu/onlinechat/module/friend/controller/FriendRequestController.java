package cn.edu.ncu.onlinechat.module.friend.controller;

import cn.edu.ncu.onlinechat.common.result.Result;
import cn.edu.ncu.onlinechat.module.friend.dto.FriendRequestSendDTO;
import cn.edu.ncu.onlinechat.module.friend.service.FriendRequestService;
import cn.edu.ncu.onlinechat.module.friend.vo.FriendRequestVO;
import cn.edu.ncu.onlinechat.security.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "好友申请")
@RestController
@RequestMapping("/friend-requests")
@RequiredArgsConstructor
public class FriendRequestController {

    private final FriendRequestService friendRequestService;

    @Operation(summary = "我收到的申请")
    @GetMapping("/incoming")
    public Result<List<FriendRequestVO>> incoming() {
        return Result.success(friendRequestService.incoming(SecurityUtil.currentUserId()));
    }

    @Operation(summary = "我发出的申请")
    @GetMapping("/outgoing")
    public Result<List<FriendRequestVO>> outgoing() {
        return Result.success(friendRequestService.outgoing(SecurityUtil.currentUserId()));
    }

    @Operation(summary = "发送好友申请")
    @PostMapping
    public Result<Long> send(@RequestBody @Valid FriendRequestSendDTO dto) {
        return Result.success(friendRequestService.send(SecurityUtil.currentUserId(), dto));
    }

    @Operation(summary = "重新发送好友申请")
    @PostMapping("/{requestId}/resend")
    public Result<Void> resend(@PathVariable Long requestId) {
        friendRequestService.resend(SecurityUtil.currentUserId(), requestId);
        return Result.success();
    }

    @Operation(summary = "同意好友申请")
    @PostMapping("/{requestId}/accept")
    public Result<Void> accept(@PathVariable Long requestId) {
        friendRequestService.accept(SecurityUtil.currentUserId(), requestId);
        return Result.success();
    }

    @Operation(summary = "拒绝好友申请")
    @PostMapping("/{requestId}/reject")
    public Result<Void> reject(@PathVariable Long requestId) {
        friendRequestService.reject(SecurityUtil.currentUserId(), requestId);
        return Result.success();
    }
}
