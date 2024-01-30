package com.ssafy.backend.domain.stt.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.HashMap;


import javax.sound.sampled.AudioSystem;

import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.WebSocket;
import okio.ByteString;
import javax.sound.sampled.*;

@Service
@Transactional
@RequiredArgsConstructor
public class SttServiceImpl implements SttService {
    public String getAccessToken(String CLIENT_ID, String CLIENT_SECRET) throws IOException {
        OkHttpClient client = new OkHttpClient();
        RequestBody formBody = new FormBody.Builder()
                .add("client_id", CLIENT_ID)
                .add("client_secret", CLIENT_SECRET)
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
    public void startStt(String CLIENT_ID, String CLIENT_SECRET) throws IOException, LineUnavailableException {
        OkHttpClient client = new OkHttpClient();

        String token = getAccessToken(CLIENT_ID,CLIENT_SECRET);

        HttpUrl.Builder httpBuilder = HttpUrl.get("https://openapi.vito.ai/v1/transcribe:streaming").newBuilder();
        httpBuilder.addQueryParameter("sample_rate","8000");
        httpBuilder.addQueryParameter("encoding","LINEAR16");
        httpBuilder.addQueryParameter("use_itn","true");
        httpBuilder.addQueryParameter("use_disfluency_filter","true");
        httpBuilder.addQueryParameter("use_profanity_filter","true");

        String url = httpBuilder.toString().replace("https://", "wss://");

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization","Bearer "+token)
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
        int i = 0;
        while (true) {
            int bytesRead = line.read(buffer, 0, buffer.length);
            if (bytesRead > 0) {
                rtzrWebSocket.send(ByteString.of(buffer, 0, bytesRead));
            }
        }

    }

}
