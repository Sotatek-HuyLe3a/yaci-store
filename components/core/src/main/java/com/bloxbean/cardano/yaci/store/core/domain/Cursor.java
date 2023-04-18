package com.bloxbean.cardano.yaci.store.core.domain;

import com.bloxbean.cardano.yaci.core.model.Era;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@ToString
@EqualsAndHashCode
public class Cursor {
    private Long slot;
    private String blockHash;
    private Long block;
    private Era era;
}
