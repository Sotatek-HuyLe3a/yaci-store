package com.bloxbean.cardano.yaci.store.core.service;

import com.bloxbean.cardano.yaci.core.model.Era;
import com.bloxbean.cardano.yaci.store.core.StoreProperties;
import com.bloxbean.cardano.yaci.store.core.domain.Cursor;
import com.bloxbean.cardano.yaci.store.core.storage.api.CursorStorage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Component
@Slf4j
public class CursorService {
    private final CursorStorage cursorStorage;
    private final StoreProperties storeProperties;

    private AtomicLong count;
    private boolean syncMode;

    public CursorService(CursorStorage cursorStorage, StoreProperties storeProperties) {
        this.cursorStorage = cursorStorage;
        this.storeProperties = storeProperties;
        this.count = new AtomicLong(0);
    }

    public void setCursor(Cursor cursor) {
        if (cursor.getBlockHash() == null)
            throw new RuntimeException("BlockHash can't be null.");

        cursorStorage.saveCursor(storeProperties.getEventPublisherId(), cursor);
        printLog(cursor.getBlock(), cursor.getEra());
    }

    public Optional<Cursor> getCursor() {
        //Get last 50 blocks and select the lowest block number
        return cursorStorage.getCursorAtCurrentMinusOffset(storeProperties.getEventPublisherId(), 50);
    }

    public Optional<Cursor> getCursorByBlockHash(String blockHash) {
        return cursorStorage.findByBlockHash(storeProperties.getEventPublisherId(), blockHash);
    }

    @Transactional
    public void rollback(long slot) {
        log.info("Rollback cursor_ to slot : " + slot);
        int count = cursorStorage.deleteBySlotGreaterThan(storeProperties.getEventPublisherId(), slot);
        log.info("Rollback -- {} cursor records", count);

        Cursor cursor
                = cursorStorage.getCurrentCursor(storeProperties.getEventPublisherId()).orElse(new Cursor());
        log.info("Cursor : Slot=" + cursor.getSlot() + ", Hash=" + cursor.getBlockHash());
    }

    public boolean isSyncMode() {
        return syncMode;
    }

    public void setSyncMode(boolean syncMode) {
        this.syncMode = syncMode;
    }

    private void printLog(long block, Era era) {
        count.incrementAndGet();
        double val = count.get() % 100;

        if (!syncMode) {
            if (val == 0) {
                log.info("# of blocks written: " + count.get());
                log.info("Block No: " + block + "  , Era: " + era);
            }

        } else {
            log.info("# of blocks written: " + count.get());
            log.info("Block No: " + block);
        }
    }

}
