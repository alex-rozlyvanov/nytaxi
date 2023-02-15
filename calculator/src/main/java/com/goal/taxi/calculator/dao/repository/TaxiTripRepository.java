package com.goal.taxi.calculator.dao.repository;

import com.goal.taxi.calculator.dao.entity.ReportView;
import com.goal.taxi.calculator.dao.entity.TaxiTripEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface TaxiTripRepository extends JpaRepository<TaxiTripEntity, UUID> {

    @Query("""
            SELECT new com.goal.taxi.calculator.dao.entity.ReportView(tte.dropOffYear, tte.dropOffMonth, tte.dropOffDay, sum(tte.totalAmount))
            FROM TaxiTripEntity tte
            WHERE  tte.dropOffYear = :year AND tte.dropOffMonth = :month
            GROUP BY tte.dropOffYear,tte.dropOffMonth, tte.dropOffDay"""
    )
    List<ReportView> getReportViewBy(@Param("year") final Integer year, @Param("month") final Short month);

    @Query("""
            SELECT tte.dropOffDatetime
            FROM TaxiTripEntity tte
            """)
    Page<LocalDateTime> findDropOffDatetime(Pageable pageable);
}
