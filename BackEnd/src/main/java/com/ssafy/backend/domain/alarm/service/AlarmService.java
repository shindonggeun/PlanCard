package com.ssafy.backend.domain.alarm.service;

import com.ssafy.backend.domain.alarm.dto.AlarmCreateRequestDto;

public interface AlarmService {
    void createAlarm(Long fromMemberId, AlarmCreateRequestDto createRequestDto);

}
