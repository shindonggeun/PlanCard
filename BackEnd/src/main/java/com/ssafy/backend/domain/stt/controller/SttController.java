package com.ssafy.backend.domain.stt.controller;

import com.ssafy.backend.domain.stt.service.SttService;
import com.ssafy.backend.global.common.dto.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sound.sampled.LineUnavailableException;
import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class SttController {
    private final SttService sttService;

    @Value("${stt.id}")
    private String CLIENT_ID;

    @Value("${stt.secret}")
    private String CLIENT_SECRET;


    @PostMapping("/stt")
    public ResponseEntity<Message<Void>> startStt() throws LineUnavailableException, IOException {
        sttService.startStt(CLIENT_ID,CLIENT_SECRET);
        return ResponseEntity.ok().body(Message.success());
    }

}
