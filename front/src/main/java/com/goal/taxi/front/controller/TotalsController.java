package com.goal.taxi.front.controller;

import com.goal.taxi.common.dto.TotalDTO;
import com.goal.taxi.front.mapper.TotalRequestMapper;
import com.goal.taxi.front.service.TotalsService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/total")
@AllArgsConstructor
public class TotalsController {
    private final TotalsService totalsService;
    private final TotalRequestMapper totalRequestMapper;

    @GetMapping
    @Parameters({
            @Parameter(required = true, in = ParameterIn.QUERY, name = "year", schema = @Schema(type = "integer", minimum = "-2147483648", maximum = "2147483647")),
            @Parameter(required = true, in = ParameterIn.QUERY, name = "month", schema = @Schema(type = "integer", minimum = "1", maximum = "12")),
            @Parameter(in = ParameterIn.QUERY, name = "day", schema = @Schema(type = "integer", minimum = "1", maximum = "31")),
            @Parameter(in = ParameterIn.QUERY, name = "allParams", hidden = true),
    })
    public Mono<TotalDTO> getTotals(@RequestParam final Map<String, String> allParams) {
        log.info("Get totals: {}", allParams);

        return totalRequestMapper.map(allParams)
                .flatMap(totalsService::getTotals);
    }

}
