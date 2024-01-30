package com.ssafy.backend.stt.controller;

import com.ssafy.backend.stt.service.sttService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sound.sampled.LineUnavailableException;
import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/api/v1/stt")
@RequiredArgsConstructor
public class SttController {

    private final sttService sttServiceImpl;

    @PostMapping("/wordToText")
    public void wordToText() throws LineUnavailableException, IOException {
        sttServiceImpl.startTranscribe();
    }


}
