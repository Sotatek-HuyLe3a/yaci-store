package com.bloxbean.cardano.yaci.store.assets.storage.repository;

import com.bloxbean.cardano.yaci.store.assets.storage.model.TxAssetEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TxAssetRepository extends JpaRepository<TxAssetEntity, Long> {
    List<TxAssetEntity> findByTxHash(String txHash);

    Slice<TxAssetEntity> findByFingerprint(String fingerprint, Pageable page);

    Slice<TxAssetEntity> findByPolicy(String policy, Pageable page);
    Slice<TxAssetEntity> findByUnit(String unit, Pageable page);

    @Query("select sum(ta.quantity) from TxAssetEntity ta where ta.fingerprint = ?1")
    Optional<Integer> getSupplyByFingerprint(String fingerprint);

    @Query("select sum(ta.quantity) from TxAssetEntity ta where ta.unit = ?1")
    Optional<Integer> getSupplyByUnit(String unit);

    @Query("select sum(ta.quantity) from TxAssetEntity ta where ta.policy = ?1")
    Optional<Integer> getSupplyByPolicy(String policy);

    int deleteBySlotGreaterThan(Long slot);
}
