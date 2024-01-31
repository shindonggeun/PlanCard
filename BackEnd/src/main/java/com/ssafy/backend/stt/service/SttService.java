package com.ssafy.backend.stt.service;

import org.json.simple.parser.ParseException;

import javax.sound.sampled.LineUnavailableException;
import java.io.IOException;

public interface SttService {

    String getAccessToken() throws IOException;

    void startTranscribe() throws IOException, LineUnavailableException, ParseException;

//    Long sttToCard(String text);

}
