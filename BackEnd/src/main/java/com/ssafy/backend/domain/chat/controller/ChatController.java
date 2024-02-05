package com.ssafy.backend.domain.chat.controller;

import com.ssafy.backend.domain.chat.dto.ChatMessageDto;
import com.ssafy.backend.domain.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RequiredArgsConstructor
@RestController
public class ChatController {
    private final ChatService chatService;

    @MessageMapping("/chat/{roomId}")
    public void send(Principal principal, ChatMessageDto chatMessageDto, @DestinationVariable Long roomId) {
        chatService.sendMessage(chatMessageDto, Long.valueOf(principal.getName()), roomId);
    }
}
