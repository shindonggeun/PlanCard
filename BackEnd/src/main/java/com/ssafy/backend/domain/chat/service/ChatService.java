package com.ssafy.backend.domain.chat.service;

import com.ssafy.backend.domain.chat.dto.ChatMessageDto;

public interface ChatService {
    void sendMessage(ChatMessageDto messageDto, Long memberId, Long roomId);
}
