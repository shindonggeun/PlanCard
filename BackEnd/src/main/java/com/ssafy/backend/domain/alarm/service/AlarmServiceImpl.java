package com.ssafy.backend.domain.alarm.service;

import com.ssafy.backend.domain.alarm.dto.AlarmCreateRequestDto;
import com.ssafy.backend.domain.alarm.entity.Alarm;
import com.ssafy.backend.domain.alarm.repository.AlarmRepository;
import com.ssafy.backend.domain.member.entity.Member;
import com.ssafy.backend.domain.member.exception.MemberError;
import com.ssafy.backend.domain.member.exception.MemberException;
import com.ssafy.backend.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AlarmServiceImpl implements AlarmService {

    private final MemberRepository memberRepository;
    private final AlarmRepository alarmRepository;

    @Override
    public void createAlarm(Long fromMemberId, AlarmCreateRequestDto createRequestDto) {
        Member fromMember = memberRepository.findById(fromMemberId).orElseThrow(()
        -> new MemberException(MemberError.NOT_FOUND_MEMBER));
        Member toMember = memberRepository.findById(createRequestDto.getToMemberId()).orElseThrow(()
        -> new MemberException(MemberError.NOT_FOUND_MEMBER));

        Alarm alarm = createRequestDto.toEntity(fromMember, toMember);
        alarmRepository.save(alarm);
    }
}
