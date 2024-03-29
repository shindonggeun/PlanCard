package com.ssafy.backend.domain.plan.entity;


import com.ssafy.backend.domain.card.entity.Card;
import com.ssafy.backend.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;


@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Plan extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private LocalDate startDate;

    private LocalDate endDate;

    @OneToMany(mappedBy = "plan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlanMember> planMembers;

    @OneToMany(mappedBy = "plan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Card> cards;

    public void updateName(String name) {
        this.name = name;
    }

    public void updateStartDate(LocalDate startDate){
        this.startDate = startDate;
    }

    public void updateEndDate(LocalDate endDate){
        this.endDate = endDate;
    }



}
