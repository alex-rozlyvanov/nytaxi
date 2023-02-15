package com.goal.taxi.calculator.dao.entity;


import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Accessors(chain = true, fluent = true)
@Entity
@Table(name = "vendor")
public class VendorEntity {
    @Id
    @Column(updatable = false, nullable = false)
    private Long id;
}
