package com.goal.taxi.back.dao.repository;

import com.goal.taxi.back.dao.entity.RateCodeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RateCodeRepository extends JpaRepository<RateCodeEntity, Long> {
}
