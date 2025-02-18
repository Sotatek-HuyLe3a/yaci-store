package com.bloxbean.cardano.yaci.store.transaction.processor;

import com.bloxbean.cardano.yaci.core.model.TransactionOutput;
import com.bloxbean.cardano.yaci.helper.model.Transaction;
import com.bloxbean.cardano.yaci.store.common.domain.Amt;
import com.bloxbean.cardano.yaci.store.common.domain.TxOuput;
import com.bloxbean.cardano.yaci.store.common.domain.UtxoKey;
import com.bloxbean.cardano.yaci.store.events.TransactionEvent;
import com.bloxbean.cardano.yaci.store.transaction.domain.Txn;
import com.bloxbean.cardano.yaci.store.transaction.storage.api.TransactionStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class TransactionProcessor {

    private final TransactionStorage transactionStorage;

    @EventListener
    @Order(3)
    @Transactional
    public void handleTransactionEvent(TransactionEvent event) {
        List<Transaction> transactions = event.getTransactions();
        List<Txn> txList = new ArrayList<>();

        transactions.forEach(transaction -> {
            List<UtxoKey> inputs = transaction.getBody().getInputs().stream()
                    .map(transactionInput -> new UtxoKey(transactionInput.getTransactionId(), transactionInput.getIndex()))
                    .collect(Collectors.toList());


            AtomicInteger index = new AtomicInteger(0);
            List<UtxoKey> outputs = transaction.getBody().getOutputs().stream()
                    .map(transactionOutput -> new UtxoKey(transaction.getTxHash(), index.getAndIncrement()))
                    .collect(Collectors.toList());
            //reset
            index.set(0);

            List<UtxoKey> collateralInputs = null;
            if (transaction.getBody().getCollateralInputs() != null) {
                collateralInputs = transaction.getBody().getCollateralInputs().stream()
                        .map(transactionInput -> new UtxoKey(transactionInput.getTransactionId(), transactionInput.getIndex()))
                        .collect(Collectors.toList());
            }

            List<UtxoKey> referenceInputs = null;
            if (transaction.getBody().getReferenceInputs() != null) {
                referenceInputs = transaction.getBody().getReferenceInputs().stream()
                        .map(transactionInput -> new UtxoKey(transactionInput.getTransactionId(), transactionInput.getIndex()))
                        .collect(Collectors.toList());
            }

            Txn txn = Txn.builder()
                    .txHash(transaction.getTxHash())
                    .blockHash(event.getMetadata().getBlockHash())
                    .blockNumber(transaction.getBlockNumber())
                    .blockTime(event.getMetadata().getBlockTime())
                    .slot(transaction.getSlot())
                    .inputs(inputs)
                    .outputs(outputs)
                    .fee(transaction.getBody().getFee())
                    .ttl(transaction.getBody().getTtl())
                    .auxiliaryDataHash(transaction.getBody().getAuxiliaryDataHash())
                    .validityIntervalStart(transaction.getBody().getValidityIntervalStart())
                    .scriptDataHash(transaction.getBody().getScriptDataHash())
                    .collateralInputs(collateralInputs)
                    .collateralReturnJson(convertOutput(transaction.getBody().getCollateralReturn()))
                    .netowrkId(transaction.getBody().getNetowrkId())
                    .totalCollateral(transaction.getBody().getTotalCollateral())
                    .collateralReturn(new UtxoKey(transaction.getTxHash(), outputs.size()))
                    .referenceInputs(referenceInputs)
                    .invalid(transaction.isInvalid())
                    .build();

            txList.add(txn);
        });

        if (txList.size() > 0) {
            transactionStorage.saveAll(txList);
        }
    }


    /**
    private TxResolvedInput resolveInput(String txHash, int outputIndex) {
        return utxoRepository.findById(new UtxoId(txHash, outputIndex))
                .map(addressUtxo ->
                        TxResolvedInput.builder()
                                .txHash(addressUtxo.getTxHash())
                                .outputIndex(addressUtxo.getOutputIndex())
                                .amounts(addressUtxo.getAmounts())
                                .dataHash(addressUtxo.getDataHash())
                                .inlineDatum(addressUtxo.getInlineDatum())
                                .referenceScriptHash(addressUtxo.getReferenceScriptHash())
                                .build())
                .orElse(TxResolvedInput.builder()
                        .txHash(txHash)
                        .outputIndex(outputIndex)
                        .build());
    }

     **/
    private TxOuput convertOutput(TransactionOutput output) {
        if (output == null)
            return null;
        List<Amt> amounts = output.getAmounts().stream().map(amount ->
                        Amt.builder()
                                .unit(amount.getUnit())
                                .policyId(amount.getPolicyId())
                                .assetName(amount.getAssetName().replace('\u0000', ' '))
                                .quantity(amount.getQuantity())
                                .build())
                .collect(Collectors.toList());

        TxOuput txOuput = TxOuput.builder()
                .address(output.getAddress())
                .dataHash(output.getDatumHash())
                .inlineDatum(output.getInlineDatum())
                .referenceScriptHash(output.getScriptRef())
                .amounts(amounts)
                .build();

        return txOuput;
    }

}
