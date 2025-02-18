package com.bloxbean.cardano.yaci.store.metadata.storage.impl.jpa;

import com.bloxbean.cardano.yaci.store.metadata.domain.TxMetadataLabel;
import com.bloxbean.cardano.yaci.store.metadata.storage.TxMetadataStorage;
import com.bloxbean.cardano.yaci.store.metadata.storage.impl.jpa.model.TxMetadataLabelEntity;
import com.bloxbean.cardano.yaci.store.metadata.storage.impl.jpa.repository.TxMetadataLabelRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

@RequiredArgsConstructor
public class TxMetadataStorageImpl implements TxMetadataStorage {
    private final TxMetadataLabelRepository metadataLabelRepository;
    private final MetadataMapper metadataMapper;

    @Override
    public List<TxMetadataLabel> saveAll(@NonNull List<TxMetadataLabel> txMetadataLabelList) {
        List<TxMetadataLabelEntity> txMetadataLabelEntities = txMetadataLabelList.stream()
                .map(metadataMapper::toTxMetadataLabelEntity)
                .toList();

        List<TxMetadataLabelEntity> savedEntities = metadataLabelRepository.saveAll(txMetadataLabelEntities);
        return savedEntities.stream()
                .map(metadataMapper::toTxMetadataLabel)
                .toList();
    }

    @Override
    public List<TxMetadataLabel> findByTxHash(String txHash) {
        return metadataLabelRepository.findByTxHash(txHash)
                .stream()
                .map(metadataMapper::toTxMetadataLabel)
                .toList();
    }

    @Override
    public List<TxMetadataLabel> findByLabel(String label, int page, int count) {
        Pageable sortedBySlot =
                PageRequest.of(page, count, Sort.by("slot").descending());

        return metadataLabelRepository.findByLabel(label, sortedBySlot)
                .stream()
                .map(metadataMapper::toTxMetadataLabel)
                .toList();
    }

    @Override
    public int deleteBySlotGreaterThan(long slot) {
        return metadataLabelRepository.deleteBySlotGreaterThan(slot);
    }

}
