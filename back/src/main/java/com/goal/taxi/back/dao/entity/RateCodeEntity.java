package com.goal.taxi.back.dao.entity;


import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Accessors(chain = true, fluent = true)
@Entity
@Table(name = "rate_code")
public class RateCodeEntity {
    @Id
    @Column(updatable = false, nullable = false)
    private Long id;
}
