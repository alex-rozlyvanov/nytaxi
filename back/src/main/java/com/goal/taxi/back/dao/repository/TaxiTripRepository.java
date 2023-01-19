package com.goal.taxi.back.dao.repository;

import com.goal.taxi.back.dao.entity.TaxiTripEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TaxiTripRepository extends JpaRepository<TaxiTripEntity, UUID> {
}
