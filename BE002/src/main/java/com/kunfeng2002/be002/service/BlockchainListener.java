package com.kunfeng2002.be002.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kunfeng2002.be002.dto.request.WalletActivityEvent;
import com.kunfeng2002.be002.repository.FollowedAddressRepository;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.Transaction;

@Service
@Slf4j
public class BlockchainListener {

    private final FollowedAddressRepository repository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final Web3j ethWeb3;
    private final Web3j bscWeb3;

    public BlockchainListener(
            FollowedAddressRepository repository,
            KafkaTemplate<String, String> kafkaTemplate,
            ObjectMapper objectMapper,
            @Qualifier("ETH") Web3j ethWeb3,
            @Qualifier("BSC-TESTNET") Web3j bscWeb3
    ) {
        this.repository = repository;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
        this.ethWeb3 = ethWeb3;
        this.bscWeb3 = bscWeb3;
    }



    @PostConstruct
    public void start() {
        startListener("ETH", ethWeb3);
        startListener("BSC-TESTNET", bscWeb3);
        kafkaTemplate.send("wallet-activity", "test-key", "{\"message\":\"Hello Kafka\"}");
    }

    private void startListener(String network, Web3j web3) {
        web3.blockFlowable(true)
                .subscribe(
                        block -> processBlock(network, block),
                        error -> log.error("Error while listening to {} blockchain", network, error)
                );
    }

    private void processBlock(String network, EthBlock ethBlock) {
        ethBlock.getBlock().getTransactions().forEach(txResult -> {
            Transaction tx = (Transaction) txResult.get();
            checkAndPublish(network, tx);
        });
    }

    private void checkAndPublish(String network, Transaction tx) {
        String from = tx.getFrom() != null ? tx.getFrom().toLowerCase() : null;
        String to = tx.getTo() != null ? tx.getTo().toLowerCase() : null;

        boolean fromFollowed = from != null && repository.existsByAddressAndNetwork(from, network);
        boolean toFollowed = to != null && repository.existsByAddressAndNetwork(to, network);

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
                log.info("Published wallet activity for {}", tx.getHash());
            } catch (JsonProcessingException e) {
                log.error("Failed to serialize WalletActivityEvent for tx {}", tx.getHash(), e);
            }
        }
    }
}
