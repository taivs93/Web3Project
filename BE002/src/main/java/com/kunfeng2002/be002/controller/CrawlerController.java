package com.kunfeng2002.be002.controller;

import com.kunfeng2002.be002.service.BlockchainCrawlerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/crawler")
public class CrawlerController {

    private final BlockchainCrawlerService crawlerService;

    @Autowired
    public CrawlerController(BlockchainCrawlerService crawlerService) {
        this.crawlerService = crawlerService;
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getStatus() {
        return ResponseEntity.ok(crawlerService.getCrawlerStatus());
    }

    @PostMapping("/watch")
    public ResponseEntity<String> addAddress(@RequestParam String address) {
        crawlerService.addWatchedAddress(address);
        return ResponseEntity.ok("Address added: " + address);
    }

    @DeleteMapping("/watch")
    public ResponseEntity<String> removeAddress(@RequestParam String address) {
        crawlerService.removeWatchedAddress(address);
        return ResponseEntity.ok("Address removed: " + address);
    }


    @GetMapping("/watch")
    public ResponseEntity<Set<String>> listAddresses() {
        Map<String, Object> status = crawlerService.getCrawlerStatus();
        int count = (int) status.getOrDefault("watchedAddresses", 0);

        return ResponseEntity.ok(crawlerService.getWatchedAddresses());
    }

    @PostMapping("/crawl")
    public ResponseEntity<String> manualCrawl() {
        crawlerService.crawlAllNetworks();
        return ResponseEntity.ok("Manual crawl triggered");
    }
}
