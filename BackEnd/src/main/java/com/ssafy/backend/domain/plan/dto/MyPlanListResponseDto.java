package com.ssafy.backend.domain.plan.dto;

import com.ssafy.backend.domain.planmember.entity.PlanMember;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MyPlanListResponseDto {

    private Long id;

    private String name;

    private LocalDate startDate;

    private LocalDate endDate;

    private List<PlanMember> planMembers;

}
