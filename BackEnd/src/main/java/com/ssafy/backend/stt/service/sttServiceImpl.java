package com.ssafy.backend.stt.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import okio.ByteString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import java.util.HashMap;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

@Slf4j
@Service
@RequiredArgsConstructor
public class sttServiceImpl implements sttService {

    @Value("${stt.client_id}")
    String client_id;

    @Value("${stt.client_secret}")
    String client_secret;

    @Override
    public String getAccessToken() throws IOException {


        OkHttpClient client = new OkHttpClient();
        RequestBody formBody = new FormBody.Builder()
                .add("client_id", client_id)
                .add("client_secret", client_secret)
                .build();

        Request request = new Request.Builder()
                .url("https://openapi.vito.ai/v1/authenticate")
                .post(formBody)
                .build();

        Response response = client.newCall(request).execute();
        ObjectMapper objectMapper = new ObjectMapper();
        HashMap<String,String> map = objectMapper.readValue(response.body().string(), HashMap.class);

        return map.get("access_token");

    }

    @Override
    public void startTranscribe() throws IOException, LineUnavailableException {

        String accessToken = getAccessToken();
//        log.info(accessToken);

        OkHttpClient client = new OkHttpClient();

        HttpUrl.Builder httpBuilder = HttpUrl.get("https://openapi.vito.ai/v1/transcribe:streaming").newBuilder();
        httpBuilder.addQueryParameter("sample_rate","8000");
        httpBuilder.addQueryParameter("encoding","LINEAR16");
        httpBuilder.addQueryParameter("use_itn","true");
        httpBuilder.addQueryParameter("use_disfluency_filter","true");
        httpBuilder.addQueryParameter("use_profanity_filter","true");

        String url = httpBuilder.toString().replace("https://", "wss://");

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization","Bearer "+ accessToken)
                .build();

        RTZRWebSocketListener webSocketListener = new RTZRWebSocketListener();
        WebSocket rtzrWebSocket = client.newWebSocket(request, webSocketListener);

        // 오디오 형식 설정
        AudioFormat audioFormat = new AudioFormat(8000.0f, 16, 1, true, false);

        // 타겟 데이터 라인 얻기
        TargetDataLine line = AudioSystem.getTargetDataLine(audioFormat);
        line.open(audioFormat);
        line.start();

        byte[] buffer = new byte[1024];
        while (true) {
            int bytesRead = line.read(buffer, 0, buffer.length);
            if (bytesRead > 0) {
                rtzrWebSocket.send(ByteString.of(buffer, 0, bytesRead));
            }
        }

    }
}

class RTZRWebSocketListener extends WebSocketListener {
    private static final Logger logger = Logger.getLogger(RTZRWebSocketListener.class.getName());
    private static final int NORMAL_CLOSURE_STATUS = 1000;

    private static void log(Level level, String msg, Object... args) {
        logger.log(level, msg, args);
    }

    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        log(Level.INFO, "Open " + response.message());
    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
        System.out.println(text);
    }

    @Override
    public void onMessage(WebSocket webSocket, ByteString bytes) {
        System.out.println(bytes.hex());
    }

    @Override
    public void onClosing(WebSocket webSocket, int code, String reason) {
        webSocket.close(NORMAL_CLOSURE_STATUS, null);
        log(Level.INFO, "Closing", code, reason);
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        t.printStackTrace();

    }

}

