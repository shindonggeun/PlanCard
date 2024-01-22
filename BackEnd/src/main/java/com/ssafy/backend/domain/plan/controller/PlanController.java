package com.ssafy.backend.domain.plan.controller;

import com.ssafy.backend.domain.plan.dto.PlanCreateRequestDto;
import com.ssafy.backend.domain.plan.service.PlanService;
import com.ssafy.backend.global.common.dto.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
