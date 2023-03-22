package com.bloxbean.cardano.yaci.store.utxo.domain;

import com.bloxbean.carano.yaci.store.common.domain.Amt;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressUtxo {
    private String txHash;
    private Integer outputIndex;
    private Long slot;
    private Long block;
    private String blockHash;
    private String ownerAddr;
    private String ownerStakeAddr;
    private String ownerPaymentKeyHash;
    private String ownerStakeKeyHash;
    private List<Amt> amounts;
    private String dataHash;
    private String inlineDatum;
    private String scriptRef;
    private Boolean spent;
    private Long spentAtSlot;
    private String spentTxHash;
    private Boolean isCollateralReturn;
}