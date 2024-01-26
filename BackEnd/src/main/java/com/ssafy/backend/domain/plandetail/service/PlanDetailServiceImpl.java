package com.ssafy.backend.domain.plandetail.service;

import com.ssafy.backend.domain.card.entity.Card;
import com.ssafy.backend.domain.card.repository.CardRepository;
import com.ssafy.backend.domain.plan.entity.Plan;
import com.ssafy.backend.domain.plan.repository.PlanRepository;
import com.ssafy.backend.domain.plandetail.dto.PlanDetailCreateRequestDto;
import com.ssafy.backend.domain.plandetail.entity.PlanDetail;
import com.ssafy.backend.domain.plandetail.repository.PlanDetailRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class PlanDetailServiceImpl implements PlanDetailService{

    private final PlanRepository planRepository;
    private final CardRepository cardRepository;

    private final PlanDetailRepository planDetailRepository;
    @Override
    public void createPlanDetail(Long planId, PlanDetailCreateRequestDto planDetailCreateRequestDto) {
           Plan plan = planRepository.findById(planId).orElseThrow();
           Card card = cardRepository.findById(planDetailCreateRequestDto.getCardId()).orElseThrow();
           planDetailRepository.save(planDetailCreateRequestDto.toEntity(card, plan));
    }
}
