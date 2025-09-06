package com.kunfeng2002.be002.controller;

import com.kunfeng2002.be002.service.BlockchainCrawlerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/crawler")
@CrossOrigin(origins = "*")
public class CrawlerController {

    @Autowired
    private BlockchainCrawlerService crawlerService;

    // API để xem trạng thái crawler
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getCrawlerStatus() {
        try {
            Map<String, Object> status = crawlerService.getCrawlerStatus();
            return ResponseEntity.ok(status);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // API để thêm địa chỉ cần theo dõi
    @PostMapping("/watch/{address}")
    public ResponseEntity<String> addWatchedAddress(@PathVariable String address) {
        try {
            crawlerService.addWatchedAddress(address);
            return ResponseEntity.ok("Address added to watch list: " + address);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to add address: " + e.getMessage());
        }
    }

    // API để xóa địa chỉ khỏi danh sách theo dõi
    @DeleteMapping("/watch/{address}")
    public ResponseEntity<String> removeWatchedAddress(@PathVariable String address) {
        try {
            crawlerService.removeWatchedAddress(address);
            return ResponseEntity.ok("Address removed from watch list: " + address);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to remove address: " + e.getMessage());
        }
    }
}