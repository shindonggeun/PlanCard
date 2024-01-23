package com.ssafy.backend.domain.plan.service;


import com.ssafy.backend.domain.plan.dto.PlanCreateRequestDto;
import com.ssafy.backend.domain.plan.dto.PlanNameUpdateRequestDto;
import com.ssafy.backend.domain.plan.entity.Plan;
import com.ssafy.backend.domain.plan.repository.PlanRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class PlanServiceImpl implements PlanService {

    private final PlanRepository planRepository;

    @Override
    public Long createPlan(PlanCreateRequestDto createRequestDto) {
        Plan plan = planRepository.save(createRequestDto.toEntity());
        return plan.getId();
    }

    @Override
    public void updatePlanName(Long planId, PlanNameUpdateRequestDto planNameUpdateRequestDto) {
        Plan plan = planRepository.findById(planId).orElseThrow(() -> new RuntimeException("해당 id의 Plan을 찾을 수 없습니다: " + planId));
        plan.updateName(planNameUpdateRequestDto.getName());
        planRepository.save(plan);

    }
}
