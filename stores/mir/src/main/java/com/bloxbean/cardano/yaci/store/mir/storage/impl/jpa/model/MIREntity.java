package com.bloxbean.cardano.yaci.store.mir.storage.impl.jpa.model;

import com.bloxbean.cardano.yaci.store.common.model.BlockAwareEntity;
import com.bloxbean.cardano.yaci.store.mir.domain.MirPot;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigInteger;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "mir")
public class MIREntity extends BlockAwareEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "tx_hash")
    private String txHash;

    @Column(name = "cert_index")
    private long certIndex;

    @Column(name = "pot")
    @Enumerated(EnumType.STRING)
    private MirPot pot;

    @Column(name = "credential")
    private String credential;

    @Column(name = "address")
    private String address;

    @Column(name = "amount")
    private BigInteger amount;

    @Column(name = "epoch")
    private Integer epoch;

    @Column(name = "slot")
    private Long slot;

    @Column(name = "block_hash")
    private String blockHash;
}

