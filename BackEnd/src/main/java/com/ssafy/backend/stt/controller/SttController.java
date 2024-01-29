package com.ssafy.backend.stt.controller;

import com.ssafy.backend.stt.RTZRSttWebSocketClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/stt")
@Slf4j
@RequiredArgsConstructor
public class SttController {

    private final RTZRSttWebSocketClient rtzrSttWebSocketClient;

    @PostMapping("/wordToText")
    public void wordToText(){


    }


}
