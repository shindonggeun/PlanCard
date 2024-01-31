package com.ssafy.backend.stt.controller;

import com.ssafy.backend.stt.service.SttService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.parser.ParseException;
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

    private final SttService sttServiceImpl;

    @PostMapping("/wordToText")
    public void wordToText() throws LineUnavailableException, IOException, ParseException {
        sttServiceImpl.startTranscribe();
    }


}
