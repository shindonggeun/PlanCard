package com.ssafy.backend.domain.fcm.service;

import com.ssafy.backend.domain.fcm.repository.FCMRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FCMServiceImpl implements FCMService {
    private final FCMRepository fcmRepository;

    @Override
    public void saveFCMToken(Long memberId, String token) {
        // redis에 id : FCM Token 쌍으로 저장
        fcmRepository.save(memberId, token);
    }

    @Override
    public void deleteFCMToken(Long memberId) {
        // redis에서 key가 id인 값 찾아서 삭제
        fcmRepository.delete(memberId);
    }

    @Override
    public void sendMessage() {
        // token 찾고


        // FCM에 전송
    }
}
