package com.kunfeng2002.be002.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

@Configuration
public class Web3Config {

    @Value("${network.ethereum.rpc-url}")
    private String ethereumRpc;

    @Value("${network.bsc.rpc-url}")
    private String bscRpc;

    @Bean("ETH")
    public Web3j web3Ethereum() {
        return Web3j.build(new HttpService(ethereumRpc));
    }

    @Bean("BSC-TESTNET")
    public Web3j web3Bsc() {
        return Web3j.build(new HttpService(bscRpc));
    }
}
