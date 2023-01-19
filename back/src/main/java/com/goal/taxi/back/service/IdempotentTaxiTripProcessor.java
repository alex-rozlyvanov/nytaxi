package com.goal.taxi.back.service;

import com.goal.taxi.back.dao.entity.IdempotencyKey;
import com.goal.taxi.back.dao.repository.IdempotentKeyRepository;
import com.goal.taxi.back.exceptions.DuplicateEventException;
import com.goal.taxi.common.model.TaxiTrip;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class IdempotentTaxiTripProcessor {
    private final TaxiTripProcessor taxiTripProcessor;
    private final IdempotentKeyRepository idempotentKeyRepository;

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void process(final String idempotencyKey, final TaxiTrip taxiTrip) {
        final var idempotentKey = buildIdempotencyKey(idempotencyKey);
        deduplicate(idempotentKey);
        taxiTripProcessor.process(taxiTrip);
    }

    private void deduplicate(final IdempotencyKey idempotentKey) throws DuplicateEventException {
        try {
            idempotentKeyRepository.findById(idempotentKey.id())
                    .ifPresentOrElse(
                            found -> throwDataIntegrityViolationException(),
                            () -> saveAndFlush(idempotentKey));
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateEventException("Duplicated event processing occurred: %s".formatted(idempotentKey.id()), e);
        }
    }

    private void throwDataIntegrityViolationException() {
        throw new DataIntegrityViolationException("Duplicated event processing occurred");
    }

    private IdempotencyKey saveAndFlush(IdempotencyKey idempotentKey) {
        return idempotentKeyRepository.saveAndFlush(idempotentKey);
    }

    private IdempotencyKey buildIdempotencyKey(final String idempotencyKey) {
        final var id = UUID.fromString(idempotencyKey);

        return new IdempotencyKey().id(id);
    }
}
