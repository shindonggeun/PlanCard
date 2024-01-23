package com.ssafy.backend.domain.plan.controller;

import com.ssafy.backend.domain.plan.dto.PlanCreateRequestDto;
import com.ssafy.backend.domain.plan.dto.PlanNameUpdateRequestDto;
import com.ssafy.backend.domain.plan.service.PlanService;
import com.ssafy.backend.global.common.dto.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/plan")
public class PlanController {
    private final PlanService planService;

    @PostMapping("/create")
    public ResponseEntity<Message<Long>> createPlan(@RequestBody PlanCreateRequestDto planCreateRequestDto) {
        Long planId = planService.createPlan(planCreateRequestDto);
        return ResponseEntity.ok().body(Message.success(planId));
    }

    @PatchMapping("/update/{planId}/name")
    public ResponseEntity<Message<Void>> updatePlanName(@PathVariable("planId") Long planId,
                                                    @RequestBody PlanNameUpdateRequestDto planNameUpdateRequestDto) {
        System.out.println(planNameUpdateRequestDto);
        planService.updatePlanName(planId, planNameUpdateRequestDto);
        System.out.println(planNameUpdateRequestDto);
        return ResponseEntity.ok().body(Message.success());
    }

}
