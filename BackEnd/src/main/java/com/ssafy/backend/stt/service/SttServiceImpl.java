package com.ssafy.backend.stt.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.backend.domain.card.dto.CardCreateRequestDto;
import com.ssafy.backend.domain.card.service.CardService;
import com.ssafy.backend.domain.place.entity.Place;
import com.ssafy.backend.domain.place.repository.PlaceInfoRepository;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import okio.ByteString;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
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
public class SttServiceImpl implements SttService {

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
        ObjectMapper objectMapper = new ObjectMapper();

        while (true) {
            int bytesRead = line.read(buffer, 0, buffer.length);
            if (bytesRead > 0) {
                rtzrWebSocket.send(ByteString.of(buffer, 0, bytesRead));
            }
        }

    }


}


@Slf4j
class RTZRWebSocketListener extends WebSocketListener {

//    private PlaceInfoRepository placeInfoRepository;
//    private CardService cardServiceImpl;
//
//    public RTZRWebSocketListener(PlaceInfoRepository placeInfoRepository, CardService cardServiceImpl) {
//        this.placeInfoRepository = placeInfoRepository;
//        this.cardServiceImpl = cardServiceImpl;
//    }

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
//        sttServiceImpl.sttToCard(text);

//        JSONParser jsonParser = new JSONParser();
//        JSONObject jsonObject = null;
//        try {
//            jsonObject = (JSONObject) jsonParser.parse(text);
//            JSONArray alterText = (JSONArray) jsonObject.get("alternatives");
//
//
//            for (Object obj : alterText) {
//                JSONObject object = (JSONObject) jsonParser.parse(obj.toString());
//                String data = (String) object.get("text");
//                log.info(data);
//
//                //저장된 여행지 목록에서 말하는 단어가 포함이 되는지 확인
//                Place findPlace = placeInfoRepository.findByName(data);
//                cardServiceImpl.createCard(1L, findPlace.getId(), new CardCreateRequestDto(""));
//
//            }
//
//        } catch (ParseException e) {
//            throw new RuntimeException(e);
//        }
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

