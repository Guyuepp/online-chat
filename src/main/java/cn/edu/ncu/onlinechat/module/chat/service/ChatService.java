package cn.edu.ncu.onlinechat.module.chat.service;

import cn.edu.ncu.onlinechat.module.chat.dto.SendMessageDTO;
import cn.edu.ncu.onlinechat.module.chat.vo.MessageVO;

import java.util.List;

public interface ChatService {

    MessageVO send(Long fromUserId, SendMessageDTO dto);

    List<MessageVO> getHistory(Long userId, String chatType, Long targetId, int limit, Long beforeId);

    void markRead(Long userId, String chatType, Long targetId);
}
