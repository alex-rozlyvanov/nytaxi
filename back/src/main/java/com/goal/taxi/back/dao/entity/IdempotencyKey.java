package com.goal.taxi.back.dao.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Accessors(chain = true, fluent = true)
@Entity
@Table(name = "idempotency_key")
public class IdempotencyKey {
    @Id
    @Column(updatable = false, nullable = false)
    private UUID id;

    @CreationTimestamp
    @JsonFormat(pattern = "MM/dd/yyyy hh:mm:ss a")
    @Column(updatable = false, nullable = false)
    private LocalDateTime creationTime;
}
