package com.goal.taxi.calculator.service;

import com.goal.taxi.calculator.dao.entity.TaxiTripEntity;
import com.goal.taxi.calculator.dao.repository.TaxiTripRepository;
import com.goal.taxi.common.dto.TotalDTO;
import org.junit.ClassRule;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "spring.jpa.hibernate.ddl-auto=create"
        }
)
@ContextConfiguration(initializers = {
        TotalsCalculatorTest.TestContainersInitializer.class
})
class TotalsCalculatorTest {
    @ClassRule
    public static final PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer<>("postgres:14.0")
            .withDatabaseName("taxi")
            .withUsername("sa")
            .withPassword("sa");
    @ClassRule
    public static final GenericContainer<?> redis =
            new GenericContainer<>(DockerImageName.parse("redis:7.0.5-alpine"))
                    .withExposedPorts(6379);

    @Autowired
    private TaxiTripRepository taxiTripRepository;

    @Autowired
    private RedisTemplate<String, TotalDTO> redisTemplate;

    @Autowired
    private TotalsCalculator service;

    @AfterAll
    public static void afterAll() {
        postgreSQLContainer.close();
        redis.close();
    }

    @Test
    void calculateTotalsForADay() {
        // GIVEN
        final var taxiTripEntity1 = buildTaxiTripEntity(111, (short) 1, (short) 2, 123.1);
        final var taxiTripEntity2 = buildTaxiTripEntity(111, (short) 1, (short) 2, 123.2);
        final var taxiTripEntity3 = buildTaxiTripEntity(111, (short) 1, (short) 2, 123.3);
        final var taxiTripEntity4 = buildTaxiTripEntity(111, (short) 1, (short) 1, 123.3);
        taxiTripRepository.saveAllAndFlush(
                List.of(taxiTripEntity1, taxiTripEntity2, taxiTripEntity3, taxiTripEntity4));

        // WHEN
        service.run("");

        // THEN
        final var hash = "111-1-2";
        final var result = (TotalDTO) redisTemplate.opsForHash().get("Total", hash);
        assertThat(result).isNotNull();
        assertThat(result.getDate()).isEqualTo(LocalDate.of(111, 1, 2));
        assertThat(result.getTotal()).isEqualTo(new BigDecimal("369.6").setScale(2));
    }

    @Test
    void calculateTotalsForAMonth() {
        // GIVEN
        final var taxiTripEntity1 = buildTaxiTripEntity(111, (short) 4, (short) 1, 123.1);
        final var taxiTripEntity2 = buildTaxiTripEntity(111, (short) 4, (short) 2, 123.2);
        final var taxiTripEntity3 = buildTaxiTripEntity(111, (short) 4, (short) 3, 123.3);
        final var taxiTripEntity4 = buildTaxiTripEntity(222, (short) 2, (short) 3, 123.3);
        taxiTripRepository.saveAllAndFlush(
                List.of(taxiTripEntity1, taxiTripEntity2, taxiTripEntity3, taxiTripEntity4));

        // WHEN
        service.run("");

        // THEN
        final var hash = "111-4";
        final var result = (TotalDTO) redisTemplate.opsForHash().get("Total", hash);
        assertThat(result).isNotNull();
        assertThat(result.getDate()).isEqualTo(LocalDate.of(111, 4, 30));
        assertThat(result.getTotal()).isEqualTo(new BigDecimal("369.6").setScale(2));
    }

    private TaxiTripEntity buildTaxiTripEntity(final int dropOffYear,
                                               final short dropOffMonth,
                                               final short dropOffDay,
                                               final double totalAmount) {
        return new TaxiTripEntity()
                .dropOffDatetime(LocalDateTime.of(dropOffYear, dropOffMonth, dropOffDay, 0, 0))
                .dropOffYear(dropOffYear)
                .dropOffMonth(dropOffMonth)
                .dropOffDay(dropOffDay)
                .totalAmount(BigDecimal.valueOf(totalAmount));
    }

    static class TestContainersInitializer
            implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            postgreSQLContainer.start();
            redis.start();
            TestPropertyValues.of(
                    "spring.redis.host=" + redis.getHost(),
                    "spring.redis.port=" + redis.getMappedPort(6379),
                    "spring.datasource.url=" + postgreSQLContainer.getJdbcUrl(),
                    "spring.datasource.username=" + postgreSQLContainer.getUsername(),
                    "spring.datasource.password=" + postgreSQLContainer.getPassword()
            ).applyTo(configurableApplicationContext.getEnvironment());
        }
    }

}
