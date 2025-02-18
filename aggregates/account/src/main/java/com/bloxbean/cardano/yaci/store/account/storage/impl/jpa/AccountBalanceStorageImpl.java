package com.bloxbean.cardano.yaci.store.account.storage.impl.jpa;

import com.bloxbean.cardano.yaci.store.account.domain.AddressBalance;
import com.bloxbean.cardano.yaci.store.account.domain.StakeAddressBalance;
import com.bloxbean.cardano.yaci.store.account.storage.AccountBalanceStorage;
import com.bloxbean.cardano.yaci.store.account.storage.impl.jpa.mapper.AccountMapper;
import com.bloxbean.cardano.yaci.store.account.storage.impl.jpa.model.AddressBalanceEntity;
import com.bloxbean.cardano.yaci.store.account.storage.impl.jpa.model.StakeAddressBalanceEntity;
import com.bloxbean.cardano.yaci.store.account.storage.impl.jpa.repository.AddressBalanceRepository;
import com.bloxbean.cardano.yaci.store.account.storage.impl.jpa.repository.StakeBalanceRepository;
import com.bloxbean.cardano.yaci.store.common.model.Order;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
public class AccountBalanceStorageImpl implements AccountBalanceStorage {
    private final AddressBalanceRepository addressBalanceRepository;
    private final StakeBalanceRepository stakeBalanceRepository;
    private final AccountMapper mapper = AccountMapper.INSTANCE;

    @Override
    public Optional<AddressBalance> getAddressBalance(String address, String unit, long slot) {
        return addressBalanceRepository.findTopByAddressAndUnitAndSlotIsLessThanEqualOrderBySlotDesc(address, unit, slot)
                .map(mapper::toAddressBalance);
    }

    @Override
    public Optional<AddressBalance> getAddressBalanceByTime(String address, String unit, long time) {
        if (time == 0)
            throw new IllegalArgumentException("Time cannot be 0");
        return addressBalanceRepository.findTopByAddressAndUnitAndBlockTimeIsLessThanEqualOrderByBlockTimeDesc(address, unit, time)
                .map(mapper::toAddressBalance);
    }

    @Override
    public List<AddressBalance> getAddressBalance(String address) {
        return addressBalanceRepository.findLatestAddressBalanceByAddress(address).stream()
                .map(mapper::toAddressBalance)
                .toList();
    }

    @Override
    public void saveAddressBalances(@NonNull List<AddressBalance> addressBalances) {
        List<AddressBalanceEntity> entities = addressBalances.stream().map(mapper::toAddressBalanceEntity)
                .toList();
        addressBalanceRepository.saveAll(entities);
    }

    @Override
    public int deleteAddressBalanceBeforeSlotExceptTop(String address, String unit, long slot) {
        //Find the latest address balance before the slot and delete all address balances before that
        return addressBalanceRepository.findTopByAddressAndUnitAndSlotIsLessThanEqualOrderBySlotDesc(address, unit, slot)
                .map(addressBalanceEntity -> addressBalanceRepository.deleteAllBeforeSlot(address, unit, addressBalanceEntity.getSlot() - 1)).orElse(0);
    }

    @Override
    public int deleteAddressBalanceBySlotGreaterThan(Long slot) {
        return addressBalanceRepository.deleteBySlotGreaterThan(slot);
    }

    @Override
    public Optional<StakeAddressBalance> getStakeAddressBalance(String address, String unit, long slot) {
        return stakeBalanceRepository.findTopByAddressAndUnitAndSlotIsLessThanEqualOrderBySlotDesc(address, unit, slot)
                .map(mapper::toStakeBalance);
    }

    @Override
    public Optional<StakeAddressBalance> getStakeAddressBalanceByTime(String address, String unit, long time) {
        if (time == 0)
            throw new IllegalArgumentException("Time cannot be 0");

        return stakeBalanceRepository.findTopByAddressAndUnitAndBlockTimeIsLessThanEqualOrderByBlockTimeDesc(address, unit, time)
                .map(mapper::toStakeBalance);
    }

    @Override
    public List<StakeAddressBalance> getStakeAddressBalance(String address) {
        return stakeBalanceRepository.findLatestAddressBalanceByAddress(address).stream()
                .map(mapper::toStakeBalance)
                .toList();
    }

    @Override
    public void saveStakeAddressBalances(List<StakeAddressBalance> stakeBalances) {
        List<StakeAddressBalanceEntity> entities = stakeBalances.stream().map(mapper::toStakeBalanceEntity)
                .toList();
        stakeBalanceRepository.saveAll(entities);
    }

    @Override
    public int deleteStakeBalanceBeforeSlotExceptTop(String address, String unit, long slot) {
        //Find the latest stake address balance before the slot and delete all address balances before that
        return stakeBalanceRepository.findTopByAddressAndUnitAndSlotIsLessThanEqualOrderBySlotDesc(address, unit, slot)
                .map(addressBalanceEntity -> stakeBalanceRepository.deleteAllBeforeSlot(address, unit, addressBalanceEntity.getSlot() - 1)).orElse(0);
    }

    @Override
    public int deleteStakeAddressBalanceBySlotGreaterThan(Long slot) {
        return stakeBalanceRepository.deleteBySlotGreaterThan(slot);
    }

    @Override
    public List<AddressBalance> getAddressesByAsset(String unit, int page, int count, Order sort) {
        Pageable sortedBySlot =
                PageRequest.of(page, count, sort == Order.asc? Sort.by("slot").ascending() : Sort.by("slot").descending());

        return addressBalanceRepository.findLatestAddressBalanceByUnit(unit, sortedBySlot).stream()
                .map(mapper::toAddressBalance)
                .toList();
    }
}
