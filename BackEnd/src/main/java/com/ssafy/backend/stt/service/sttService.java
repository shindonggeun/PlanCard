package com.ssafy.backend.stt.service;

import javax.sound.sampled.LineUnavailableException;
import java.io.IOException;

public interface sttService {

    String getAccessToken() throws IOException;

    void startTranscribe() throws IOException, LineUnavailableException;


}
