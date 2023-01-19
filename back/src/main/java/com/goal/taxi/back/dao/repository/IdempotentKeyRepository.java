package com.goal.taxi.back.dao.repository;

import com.goal.taxi.back.dao.entity.IdempotencyKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface IdempotentKeyRepository extends JpaRepository<IdempotencyKey, UUID> {
}
