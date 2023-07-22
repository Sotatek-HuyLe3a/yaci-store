package com.bloxbean.cardano.yaci.store.common.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class BlockAwareDomain {
    private Long blockNumber;
    private Long blockTime;
}