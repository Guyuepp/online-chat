package cn.edu.ncu.onlinechat.module.chat.controller;

import cn.edu.ncu.onlinechat.common.result.Result;
import cn.edu.ncu.onlinechat.module.chat.dto.SendMessageDTO;
import cn.edu.ncu.onlinechat.module.chat.service.ChatService;
import cn.edu.ncu.onlinechat.module.chat.vo.MessageVO;
import cn.edu.ncu.onlinechat.security.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "消息")
@RestController
@RequestMapping("/messages")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @Operation(summary = "发送消息")
    @PostMapping
    public Result<MessageVO> send(@RequestBody @Valid SendMessageDTO dto) {
        return Result.success(chatService.send(SecurityUtil.currentUserId(), dto));
    }

    @Operation(summary = "拉取历史消息")
    @GetMapping
    public Result<List<MessageVO>> history(@RequestParam String chatType,
                                           @RequestParam Long targetId,
                                           @RequestParam(defaultValue = "50") int limit,
                                           @RequestParam(required = false) Long beforeId) {
        return Result.success(chatService.getHistory(SecurityUtil.currentUserId(), chatType, targetId, limit, beforeId));
    }

    @Operation(summary = "标记已读")
    @PutMapping("/read")
    public Result<Void> markRead(@RequestParam String chatType, @RequestParam Long targetId) {
        chatService.markRead(SecurityUtil.currentUserId(), chatType, targetId);
        return Result.success();
    }
}
