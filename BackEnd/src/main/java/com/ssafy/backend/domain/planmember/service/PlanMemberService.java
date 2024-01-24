package com.ssafy.backend.domain.planmember.service;

public interface PlanMemberService {
    //계획에 친구 초대

    //계획 초대 수락
    void acceptPlan(Long planId, Long memberId);

}
