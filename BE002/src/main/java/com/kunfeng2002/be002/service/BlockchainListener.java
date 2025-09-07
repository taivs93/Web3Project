package com.kunfeng2002.be002.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kunfeng2002.be002.dto.request.WalletActivityEvent;
import com.kunfeng2002.be002.repository.FollowedAddressRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.Transaction;

@Service
@RequiredArgsConstructor
public class BlockchainListener {

    private final FollowedAddressRepository repository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Qualifier("ETH")
    private final Web3j ethWeb3;

    @Qualifier("BSC")
    private final Web3j bscWeb3;

    @PostConstruct
    public void start() {
        startListener("ETH", ethWeb3);
        startListener("BSC", bscWeb3);
    }

    private void startListener(String network, Web3j web3) {
        web3.blockFlowable(false).subscribe(block -> {
            block.getBlock().getTransactions().forEach(txObj -> {
                Transaction tx = (Transaction) txObj.get();
                checkAndPublish(network, tx);
            });
        });
    }

    private void checkAndPublish(String network, Transaction tx) {
        String from = tx.getFrom() != null ? tx.getFrom().toLowerCase() : null;
        String to   = tx.getTo() != null ? tx.getTo().toLowerCase() : null;

        boolean fromFollowed = from != null && repository.existsByAddressAndNetwork(from, network);
        boolean toFollowed   = to != null && repository.existsByAddressAndNetwork(to, network);

        if (fromFollowed || toFollowed) {
            WalletActivityEvent event = new WalletActivityEvent(
                    tx.getHash(),
                    tx.getFrom(),
                    tx.getTo(),
                    network,
                    tx.getValue().toString()
            );

            try {
                kafkaTemplate.send(
                        "wallet-activity",
                        tx.getHash(),
                        objectMapper.writeValueAsString(event)
                );
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Failed to serialize WalletActivityEvent", e);
            }
        }
    }
}
