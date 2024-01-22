package com.ssafy.backend.domain.plan.service;


import com.ssafy.backend.domain.plan.dto.PlanCreateRequestDto;

public interface PlanService {
    //여행 계획 생성
    Long createPlan(PlanCreateRequestDto createRequestDto);

}
