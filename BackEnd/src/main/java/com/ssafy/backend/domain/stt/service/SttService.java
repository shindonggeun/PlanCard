package com.ssafy.backend.domain.stt.service;

import javax.sound.sampled.LineUnavailableException;
import java.io.IOException;

public interface SttService {
    //
    void startStt(String CLIENT_ID, String CLIENT_SECRET) throws IOException, LineUnavailableException;
}
