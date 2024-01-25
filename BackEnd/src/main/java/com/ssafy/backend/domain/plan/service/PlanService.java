package com.ssafy.backend.domain.plan.service;


import com.ssafy.backend.domain.plan.dto.MyPlanListResponseDto;
import com.ssafy.backend.domain.plan.dto.PlanCreateRequestDto;
import com.ssafy.backend.domain.plan.dto.PlanDateUpdateRequestDto;
import com.ssafy.backend.domain.plan.dto.PlanNameUpdateRequestDto;

import java.util.List;

public interface PlanService {
    //여행 계획 생성
    Long createPlan(PlanCreateRequestDto createRequestDto);

    //여행 이름 수정
    void updatePlanName(Long planId, PlanNameUpdateRequestDto planNameUpdateRequestDto);

    //여행 날짜 수정
    void updatePlanDate(Long planId, PlanDateUpdateRequestDto planDateUpdateRequestDto);

    List<MyPlanListResponseDto> getMyPlanList(Long memberId);
}
