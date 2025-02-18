package com.bloxbean.cardano.yaci.store.metadata.storage.impl.jpa.model;

import com.bloxbean.cardano.yaci.store.common.model.BlockAwareEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "transaction_metadata")
public class TxMetadataLabelEntity extends BlockAwareEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "slot")
    private Long slot;

    @Column(name = "tx_hash")
    private String txHash;

    @Column(name = "label")
    private String label;

    @Column(name = "body")
    private String body;

    @Column(name = "cbor")
    private String cbor;
}
