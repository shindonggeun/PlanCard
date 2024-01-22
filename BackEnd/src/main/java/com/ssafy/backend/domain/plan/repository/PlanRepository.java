package com.ssafy.backend.domain.plan.repository;

import com.ssafy.backend.domain.plan.entity.Plan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlanRepository extends JpaRepository<Plan, Long> {
    // 기본적인 CRUD는 JPA에서 제공

}
