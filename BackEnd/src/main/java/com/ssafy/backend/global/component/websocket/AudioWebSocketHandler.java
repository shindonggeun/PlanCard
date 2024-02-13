package com.ssafy.backend.global.component.websocket;

import com.ssafy.backend.domain.stt.service.SttService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;

import java.nio.ByteBuffer;

@Slf4j
@RequiredArgsConstructor
@Component
public class AudioWebSocketHandler extends BinaryWebSocketHandler {

    private final SttService sttService;

    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) throws Exception {
        ByteBuffer payload = message.getPayload();
        log.info("Received binary message of size: {}", payload.remaining());

        byte[] audioData = new byte[payload.remaining()];
        payload.get(audioData);

        // STT 서비스를 통해 오디오 데이터를 텍스트로 변환하고 결과를 처리
        sttService.processAudioData(audioData);
    }
}
