package com.ssafy.backend.domain.plandetail.service;

import com.ssafy.backend.domain.card.entity.Card;
import com.ssafy.backend.domain.card.repository.CardRepository;
import com.ssafy.backend.domain.plan.entity.Plan;
import com.ssafy.backend.domain.plan.repository.PlanRepository;
import com.ssafy.backend.domain.plandetail.dto.PlanDetailCreateRequestDto;
import com.ssafy.backend.domain.plandetail.dto.PlanDetailListResponseDto;
import com.ssafy.backend.domain.plandetail.entity.PlanDetail;
import com.ssafy.backend.domain.plandetail.repository.PlanDetailRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class PlanDetailServiceImpl implements PlanDetailService{

    private final PlanRepository planRepository;
    private final CardRepository cardRepository;
    private final PlanDetailRepository planDetailRepository;
    @Override
    public void createAndUpdatePlanDetail(Long planId, String action,
                                          List<PlanDetailCreateRequestDto> planDetailCreateRequestDtoList) {
        if (planDetailCreateRequestDtoList == null || planDetailCreateRequestDtoList.isEmpty()) {
            throw new RuntimeException("생성할 여행 상세 계획이 없습니다.");
        }
        Plan plan = planRepository.findById(planId).orElseThrow();


        for (PlanDetailCreateRequestDto planDetailCreateRequestDto : planDetailCreateRequestDtoList) {
//            if pla
            Card card = cardRepository.findById(planDetailCreateRequestDto.getCardId()).orElseThrow();
            planDetailRepository.save(planDetailCreateRequestDto.toEntity(card, plan));
        }
    }

    @Override
    public List<PlanDetailListResponseDto> getPlanDetailList(Long planId) {
        List<PlanDetail> planDetails = planDetailRepository.findByPlanId(planId);

        return planDetails.stream()
                .map(planDetail -> new PlanDetailListResponseDto(
                        planDetail.getId(),
                        planDetail.getCard().getId(),
                        planDetail.getCard().getPosition(),
                        planDetail.getCard().getMemo(),
                        planDetail.getCard().getPlace().getName(),
                        planDetail.getCard().getPlace().getAddress(),
                        planDetail.getOrderNumber(),
                        planDetail.getDay()))
                .collect(Collectors.toList());
    }
}
