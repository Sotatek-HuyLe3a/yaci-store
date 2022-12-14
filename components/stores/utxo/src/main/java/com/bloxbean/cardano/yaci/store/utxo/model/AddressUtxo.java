package com.bloxbean.cardano.yaci.store.utxo.model;

import com.bloxbean.carano.yaci.store.common.domain.Amt;
import com.bloxbean.carano.yaci.store.common.model.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "address_utxo")
@IdClass(UtxoId.class)
public class AddressUtxo extends BaseEntity {
    @Id
    @Column(name = "tx_hash")
    private String txHash;
    @Id
    @Column(name = "output_index")
    private Integer outputIndex;

    @Column(name = "slot")
    private Long slot;

    @Column(name = "block")
    private Long block;

    @Column(name = "block_hash")
    private String blockHash;

    @Column(name = "owner_addr")
    private String ownerAddr;

    @Column(name = "owner_stake_addr")
    private String ownerStakeAddr;

    @Column(name = "owner_payment_key_hash")
    private String ownerPaymentKeyHash;

    @Column(name = "owner_stake_key_hash")
    private String ownerStakeKeyHash;

    @Type(type = "json")
    private List<Amt> amounts;

    @Column(name = "data_hash")
    private String dataHash;

    @Lob
    @Column(name = "inline_datum")
    private String inlineDatum;

    @Lob
    @Column(name = "script_ref")
    private String scriptRef;

    @Column(name = "spent")
    private Boolean spent;

    @Column(name = "spent_at_slot")
    private Long spentAtSlot;

    @Column(name = "spent_tx_hash")
    private String spentTxHash;

    @Column(name = "is_collateral_return")
    private Boolean isCollateralReturn;
}
