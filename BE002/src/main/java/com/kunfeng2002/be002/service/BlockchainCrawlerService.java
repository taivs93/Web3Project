package com.kunfeng2002.be002.service;

import com.kunfeng2002.be002.Telegram.Bot;
import com.kunfeng2002.be002.entity.User;
import com.kunfeng2002.be002.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.methods.response.EthBlock;

import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Convert;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class BlockchainCrawlerService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TelegramBotService telegramBotService;

    @Autowired
    private Bot bot;

    private final Map<String, Web3j> web3jInstances = new ConcurrentHashMap<>();

    // Map lưu block number cuối cùng đã crawl cho từng network
    private final Map<String, BigInteger> lastProcessedBlocks = new ConcurrentHashMap<>();

    // Set các địa chỉ ví cần theo dõi
    private final Set<String> watchedAddresses = Collections.synchronizedSet(new HashSet<>());

    // Thread pool để crawl đồng thời nhiều network
    private final ExecutorService executorService = Executors.newFixedThreadPool(10);

    // Cấu hình các network cần crawl
    private static final Map<String, NetworkConfig> NETWORKS = Map.of(
            "ethereum", new NetworkConfig("Ethereum", "https://mainnet.infura.io/v3/YOUR_INFURA_KEY", 1),
            "bsc", new NetworkConfig("BSC", "https://bsc-dataseed1.binance.org/", 56),
            "polygon", new NetworkConfig("Polygon", "https://polygon-rpc.com/", 137),
            "arbitrum", new NetworkConfig("Arbitrum", "https://arb1.arbitrum.io/rpc", 42161),
            "optimism", new NetworkConfig("Optimism", "https://mainnet.optimism.io/", 10),
            "avalanche", new NetworkConfig("Avalanche", "https://api.avax.network/ext/bc/C/rpc", 43114),
            "fantom", new NetworkConfig("Fantom", "https://rpc.ftm.tools/", 250),
            "localhost", new NetworkConfig("Localhost", "http://localhost:8545", 1337)
    );

    @PostConstruct
    public void initializeCrawler() {
        try {
            // Khởi tạo Web3j connections cho từng network
            initializeNetworks();

            // Load danh sách ví cần theo dõi từ database
            loadWatchedAddresses();

            System.out.println("Blockchain Crawler initialized successfully");

        } catch (Exception e) {
            System.err.println("Failed to initialize Blockchain Crawler: " + e.getMessage());
        }
    }

    private void initializeNetworks() {
        for (Map.Entry<String, NetworkConfig> entry : NETWORKS.entrySet()) {
            String networkName = entry.getKey();
            NetworkConfig config = entry.getValue();

            try {
                Web3j web3j = Web3j.build(new HttpService(config.getRpcUrl()));
                web3jInstances.put(networkName, web3j);

                // Lấy block number hiện tại làm điểm bắt đầu
                BigInteger currentBlock = web3j.ethBlockNumber().send().getBlockNumber();
                lastProcessedBlocks.put(networkName, currentBlock);

                System.out.println("Connected to " + config.getName() + " - Current block: " + currentBlock);

            } catch (Exception e) {
                System.err.println("Failed to connect to " + config.getName() + ": " + e.getMessage());
            }
        }
    }

    private void loadWatchedAddresses() {
        try {
            List<User> users = userRepository.findAll();
            for (User user : users) {
                if (user.getWallet() != null && user.getWallet().getAddress() != null) {
                    watchedAddresses.add(user.getWallet().getAddress().toLowerCase());
                }
            }
            System.out.println("Loaded " + watchedAddresses.size() + " addresses to watch");
        } catch (Exception e) {
            System.err.println("Failed to load watched addresses: " + e.getMessage());
        }
    }

    // Crawl mỗi 30 giây
    @Scheduled(fixedDelay = 30000)
    @Async
    public void crawlAllNetworks() {
        for (String networkName : web3jInstances.keySet()) {
            executorService.submit(() -> crawlNetwork(networkName));
        }
    }

    private void crawlNetwork(String networkName) {
        try {
            Web3j web3j = web3jInstances.get(networkName);
            if (web3j == null) return;

            NetworkConfig config = NETWORKS.get(networkName);
            BigInteger lastBlock = lastProcessedBlocks.get(networkName);
            BigInteger currentBlock = web3j.ethBlockNumber().send().getBlockNumber();

            // Crawl từ block cuối cùng đã xử lý đến block hiện tại
            for (BigInteger blockNum = lastBlock.add(BigInteger.ONE);
                 blockNum.compareTo(currentBlock) <= 0;
                 blockNum = blockNum.add(BigInteger.ONE)) {

                processBlock(web3j, networkName, config, blockNum);
            }

            // Cập nhật block cuối cùng đã xử lý
            lastProcessedBlocks.put(networkName, currentBlock);

        } catch (Exception e) {
            System.err.println("Error crawling " + networkName + ": " + e.getMessage());
        }
    }

    private void processBlock(Web3j web3j, String networkName, NetworkConfig config, BigInteger blockNumber) {
        try {
            EthBlock ethBlock = web3j.ethGetBlockByNumber(
                    DefaultBlockParameter.valueOf(blockNumber), true
            ).send();

            if (ethBlock.getBlock() == null) return;

            List<EthBlock.TransactionResult> transactions = ethBlock.getBlock().getTransactions();

            for (EthBlock.TransactionResult txResult : transactions) {
                if (txResult instanceof EthBlock.TransactionObject) {
                    EthBlock.TransactionObject tx = (EthBlock.TransactionObject) txResult;
                    checkTransactionForWatchedAddresses(tx, networkName, config, blockNumber);
                }
            }

        } catch (Exception e) {
            System.err.println("Error processing block " + blockNumber + " on " + networkName + ": " + e.getMessage());
        }
    }

    private void checkTransactionForWatchedAddresses(EthBlock.TransactionObject tx,
                                                     String networkName,
                                                     NetworkConfig config,
                                                     BigInteger blockNumber) {
        try {
            String from = tx.getFrom().toLowerCase();
            String to = tx.getTo() != null ? tx.getTo().toLowerCase() : "";
            BigInteger value = tx.getValue();

            // Kiểm tra nếu from hoặc to là địa chỉ cần theo dõi
            String watchedAddress = null;
            String direction = "";

            if (watchedAddresses.contains(from)) {
                watchedAddress = from;
                direction = "OUTGOING";
            } else if (watchedAddresses.contains(to)) {
                watchedAddress = to;
                direction = "INCOMING";
            }

            if (watchedAddress != null && value.compareTo(BigInteger.ZERO) > 0) {
                // Tạo transaction notification
                TransactionNotification notification = TransactionNotification.builder()
                        .networkName(config.getName())
                        .chainId(config.getChainId())
                        .txHash(tx.getHash())
                        .from(tx.getFrom())
                        .to(tx.getTo())
                        .value(value)
                        .blockNumber(blockNumber)
                        .watchedAddress(watchedAddress)
                        .direction(direction)
                        .gasPrice(tx.getGasPrice())
                        .gasUsed(tx.getGas())
                        .build();

                sendTransactionNotification(notification);
            }

        } catch (Exception e) {
            System.err.println("Error checking transaction: " + e.getMessage());
        }
    }

    private void sendTransactionNotification(TransactionNotification notification) {
        try {
            // Tìm user theo wallet address
            Optional<User> userOpt = userRepository.findByWalletAddressQuery(notification.getWatchedAddress());

            if (userOpt.isPresent() && userOpt.get().getTelegramUserId() != null) {
                String message = formatTransactionMessage(notification);
                bot.sendText(userOpt.get().getTelegramUserId(), message);

                System.out.println("Sent transaction notification to user: " + userOpt.get().getUsername());
            }

        } catch (Exception e) {
            System.err.println("Failed to send transaction notification: " + e.getMessage());
        }
    }

    private String formatTransactionMessage(TransactionNotification notification) {
        BigDecimal ethValue = Convert.fromWei(new BigDecimal(notification.getValue()), Convert.Unit.ETHER);

        StringBuilder message = new StringBuilder();
        message.append("🚨 TRANSACTION ALERT 🚨\n\n");
        message.append("Network: ").append(notification.getNetworkName()).append("\n");
        message.append("Direction: ").append(notification.getDirection()).append("\n");
        message.append("Amount: ").append(ethValue.toPlainString()).append(" ETH\n");
        message.append("From: ").append(notification.getFrom()).append("\n");
        message.append("To: ").append(notification.getTo()).append("\n");
        message.append("Block: ").append(notification.getBlockNumber()).append("\n");
        message.append("TxHash: ").append(notification.getTxHash()).append("\n");

        return message.toString();
    }

    // API để thêm địa chỉ cần theo dõi
    public void addWatchedAddress(String address) {
        if (address != null && address.matches("^0x[a-fA-F0-9]{40}$")) {
            watchedAddresses.add(address.toLowerCase());
            System.out.println("Added address to watch: " + address);
        }
    }

    // API để xóa địa chỉ khỏi danh sách theo dõi
    public void removeWatchedAddress(String address) {
        watchedAddresses.remove(address.toLowerCase());
        System.out.println("Removed address from watch: " + address);
    }

    // API để lấy trạng thái crawler
    public Map<String, Object> getCrawlerStatus() {
        Map<String, Object> status = new HashMap<>();

        for (Map.Entry<String, BigInteger> entry : lastProcessedBlocks.entrySet()) {
            String network = entry.getKey();
            BigInteger lastBlock = entry.getValue();

            Map<String, Object> networkStatus = new HashMap<>();
            networkStatus.put("lastProcessedBlock", lastBlock.toString());
            networkStatus.put("connected", web3jInstances.containsKey(network));

            status.put(network, networkStatus);
        }

        status.put("watchedAddresses", watchedAddresses.size());
        return status;
    }

    @PreDestroy
    public void shutdown() {
        try {
            executorService.shutdown();

            for (Web3j web3j : web3jInstances.values()) {
                web3j.shutdown();
            }

            System.out.println("Blockchain Crawler shut down successfully");

        } catch (Exception e) {
            System.err.println("Error during crawler shutdown: " + e.getMessage());
        }
    }

    // Inner classes
    private static class NetworkConfig {
        private final String name;
        private final String rpcUrl;
        private final int chainId;

        public NetworkConfig(String name, String rpcUrl, int chainId) {
            this.name = name;
            this.rpcUrl = rpcUrl;
            this.chainId = chainId;
        }

        public String getName() { return name; }
        public String getRpcUrl() { return rpcUrl; }
        public int getChainId() { return chainId; }
    }

    public static class TransactionNotification {
        private String networkName;
        private int chainId;
        private String txHash;
        private String from;
        private String to;
        private BigInteger value;
        private BigInteger blockNumber;
        private String watchedAddress;
        private String direction;
        private BigInteger gasPrice;
        private BigInteger gasUsed;

        public static TransactionNotificationBuilder builder() {
            return new TransactionNotificationBuilder();
        }

        // Getters
        public String getNetworkName() { return networkName; }
        public int getChainId() { return chainId; }
        public String getTxHash() { return txHash; }
        public String getFrom() { return from; }
        public String getTo() { return to; }
        public BigInteger getValue() { return value; }
        public BigInteger getBlockNumber() { return blockNumber; }
        public String getWatchedAddress() { return watchedAddress; }
        public String getDirection() { return direction; }
        public BigInteger getGasPrice() { return gasPrice; }
        public BigInteger getGasUsed() { return gasUsed; }

        public static class TransactionNotificationBuilder {
            private TransactionNotification notification = new TransactionNotification();

            public TransactionNotificationBuilder networkName(String networkName) {
                notification.networkName = networkName;
                return this;
            }

            public TransactionNotificationBuilder chainId(int chainId) {
                notification.chainId = chainId;
                return this;
            }

            public TransactionNotificationBuilder txHash(String txHash) {
                notification.txHash = txHash;
                return this;
            }

            public TransactionNotificationBuilder from(String from) {
                notification.from = from;
                return this;
            }

            public TransactionNotificationBuilder to(String to) {
                notification.to = to;
                return this;
            }

            public TransactionNotificationBuilder value(BigInteger value) {
                notification.value = value;
                return this;
            }

            public TransactionNotificationBuilder blockNumber(BigInteger blockNumber) {
                notification.blockNumber = blockNumber;
                return this;
            }

            public TransactionNotificationBuilder watchedAddress(String watchedAddress) {
                notification.watchedAddress = watchedAddress;
                return this;
            }

            public TransactionNotificationBuilder direction(String direction) {
                notification.direction = direction;
                return this;
            }

            public TransactionNotificationBuilder gasPrice(BigInteger gasPrice) {
                notification.gasPrice = gasPrice;
                return this;
            }

            public TransactionNotificationBuilder gasUsed(BigInteger gasUsed) {
                notification.gasUsed = gasUsed;
                return this;
            }

            public TransactionNotification build() {
                return notification;
            }
        }
    }
}