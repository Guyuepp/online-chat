package cn.edu.ncu.onlinechat.module.chat.service.impl;

import cn.edu.ncu.onlinechat.module.chat.dto.SendMessageDTO;
import cn.edu.ncu.onlinechat.module.chat.mapper.MessageMapper;
import cn.edu.ncu.onlinechat.module.chat.service.ChatService;
import cn.edu.ncu.onlinechat.module.chat.vo.MessageVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final MessageMapper messageMapper;

    @Override
    public MessageVO send(Long fromUserId, SendMessageDTO dto) {
        throw new UnsupportedOperationException("TODO: 消息落库 → 推送 WebSocket → 离线消息存 Redis");
    }

    @Override
    public List<MessageVO> getHistory(Long userId, String chatType, Long targetId, int limit, Long beforeId) {
        throw new UnsupportedOperationException("TODO: 按时间倒序翻页拉取历史消息，校验用户有权限看");
    }

    @Override
    public void markRead(Long userId, String chatType, Long targetId) {
        throw new UnsupportedOperationException("TODO");
    }
}
